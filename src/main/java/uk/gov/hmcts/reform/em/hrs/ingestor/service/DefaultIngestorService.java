package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import com.gc.iotools.stream.os.OutputStreamToInputStream;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.AntivirusClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.AvScanResult;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.HrsApiException;
import uk.gov.hmcts.reform.em.hrs.ingestor.http.HrsApiClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItemSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.HrsFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.Metadata;
import uk.gov.hmcts.reform.em.hrs.ingestor.storage.CvpBlobstoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

@Component
public class DefaultIngestorService implements IngestorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIngestorService.class);
    private static int filesAttempted;
    private static int filesParsedOk;
    private static int filesSubmittedOk;
    private final CvpBlobstoreClient cvpBlobstoreClient;
    private final HrsApiClient hrsApiClient;
    private final IngestionFilterer ingestionFilterer;
    private final AntivirusClient antivirusClient;
    private final MetadataResolver metadataResolver;

    @Setter
    @Value("${ingestion.max-number-of-files-to-process-per-batch}")
    private Integer maxNumberOfFilesToProcessPerBatch = 100;


    @Autowired
    public DefaultIngestorService(final CvpBlobstoreClient cvpBlobstoreClient,
                                  final HrsApiClient hrsApiClient,
                                  final IngestionFilterer ingestionFilterer,
                                  final AntivirusClient antivirusClient,
                                  final MetadataResolver metadataResolver

    ) {
        this.cvpBlobstoreClient = cvpBlobstoreClient;
        this.hrsApiClient = hrsApiClient;
        this.ingestionFilterer = ingestionFilterer;
        this.antivirusClient = antivirusClient;
        this.metadataResolver = metadataResolver;

    }

    @Override
    public void ingest() {
        filesAttempted = 0;
        filesParsedOk = 0;
        filesSubmittedOk = 0;
        LOGGER.info("Ingestion Started");
        final Set<String> folders = cvpBlobstoreClient.getFolders();
        folders.forEach(folder -> {
            if (batchProcessingLimitReached()) {
                LOGGER.info("BATCH PROCESSING LIMIT REACHED FOR FOLDER: {}", folder);
                return;
            }

            LOGGER.info("Inspecting folder: {}", folder);
            final Set<CvpItem> filteredSet = getFilesToIngest(folder);
            filteredSet.forEach(file -> {
                if (batchProcessingLimitReached()) {
                    return;
                }
                filesAttempted++;
                if (isFileClean(file.getFilename())) {
                    resolveMetaDataAndPostFileToHrs(file);

                }
            });
            LOGGER.info("Running Total of Files Attempted: {}", filesAttempted);

        });
        LOGGER.info("Ingestion Complete");
        LOGGER.info("Total filesAttempted: {}", filesAttempted);
        LOGGER.info("Total filesParsedOk: {}", filesParsedOk);
        LOGGER.info("Total filesSubmittedOk: {}", filesSubmittedOk);

    }

    private void resolveMetaDataAndPostFileToHrs(CvpItem file) {
        try {
            Metadata metaData = metadataResolver.resolve(file);
            filesParsedOk++;
            hrsApiClient.postFile(metaData);
            filesSubmittedOk++;


        } catch (HrsApiException hrsApi) {
            LOGGER.error(
                "Response error: {} => {} => {}",
                hrsApi.getCode(),
                hrsApi.getMessage(),
                hrsApi.getBody()
            );

        } catch (Exception e) {
            LOGGER.error(
                "Exception processing file {}:: ",
                file.getFilename(),
                e
            ); // TODO: covered by EM-3582
        }
    }

    private boolean batchProcessingLimitReached() {
        return filesAttempted >= maxNumberOfFilesToProcessPerBatch;
    }

    private Set<CvpItem> getFilesToIngest(final String folder) {
        try {
            final CvpItemSet cvpItemSet = cvpBlobstoreClient.findByFolder(folder);
            final HrsFileSet hrsFileSet = hrsApiClient.getIngestedFiles(folder);
            Set<CvpItem> filesToIngest = ingestionFilterer.filter(cvpItemSet, hrsFileSet);

            int cvpFilesCount = cvpItemSet.getCvpItems().size();
            int hrsFileCount = hrsFileSet.getHrsFiles().size();
            int filesToIngestCount = filesToIngest.size();

            LOGGER.info("Folder:{}, CVP Files:{}, HRS Files:{}, To Ingest:{}",
                        folder, cvpFilesCount, hrsFileCount, filesToIngestCount
            );
            return filesToIngest;
        } catch (HrsApiException | IOException e) {
            LOGGER.error("", e); // TODO: covered by EM-3582
            return Collections.emptySet();
        }
    }

    private boolean isFileClean(final String file) {
        try {
            final AvScanResult result = downloadAndScan(file);
            if (result == AvScanResult.INFECTED) {
                LOGGER.info("File is infected: {}", file);  // TODO: covered by EM-3582
            }
            return result == AvScanResult.CLEAN;
        } catch (Exception e) {
            LOGGER.error("Error AV checking {}:", file, e);  // TODO: covered by EM-3582
            return false;
        }
    }

    private AvScanResult downloadAndScan(final String file) throws Exception {
        try (final OutputStreamToInputStream<AvScanResult> output = new OutputStreamToInputStream<>() {
            @Override
            protected AvScanResult doRead(final InputStream input) throws Exception {
                return antivirusClient.scan(input);
            }
        }) {
            cvpBlobstoreClient.downloadFile(file, output);
            return output.getResult();
        }
    }


}
