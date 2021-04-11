package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import com.gc.iotools.stream.os.OutputStreamToInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.AntivirusClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.AvScanResult;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.FileParsingException;
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

    private final CvpBlobstoreClient cvpBlobstoreClient;
    private final HrsApiClient hrsApiClient;
    private final IngestionFilterer ingestionFilterer;
    private final AntivirusClient antivirusClient;
    private final MetadataResolver metadataResolver;

    private static int filesAttempted;
    private static int filesParsedOk;
    private static int filesSubmittedOk;

    @Autowired
    public DefaultIngestorService(final CvpBlobstoreClient cvpBlobstoreClient,
                                  final HrsApiClient hrsApiClient,
                                  final IngestionFilterer ingestionFilterer,
                                  final AntivirusClient antivirusClient,
                                  final MetadataResolver metadataResolver) {
        this.cvpBlobstoreClient = cvpBlobstoreClient;
        this.hrsApiClient = hrsApiClient;
        this.ingestionFilterer = ingestionFilterer;
        this.antivirusClient = antivirusClient;
        this.metadataResolver = metadataResolver;

        LOGGER.info("Constructing DefaultIngestorService");
    }

    @Override
    public void ingest() {
        filesAttempted = 0;
        filesParsedOk = 0;
        filesSubmittedOk = 0;
        LOGGER.info("Ingestion Started");
        final Set<String> folders = cvpBlobstoreClient.getFolders();
        folders.forEach(folder -> {
            LOGGER.info("Inspecting folder: {}", folder);
            final Set<CvpItem> filteredSet = getFilesToIngest(folder);
            filteredSet.forEach(file -> {
                filesAttempted++;
                if (isFileClean(file.getFilename())) {
                    postToHrsApi(file);
                }
            });
            LOGGER.info("Running Total of Files Attempted: {}", filesAttempted);

        });
        LOGGER.info("Ingestion Complete");
        LOGGER.info("Total filesAttempted: {}", filesAttempted);
        LOGGER.info("Total filesParsedOk: {}", filesParsedOk);
        LOGGER.info("Total filesSubmittedOk: {}", filesSubmittedOk);

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
            LOGGER.error("Error AV checking {}: ", file, e);  // TODO: covered by EM-3582
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

    private void postToHrsApi(final CvpItem item) {
        Metadata metadata = null;
        try {
            metadata = metadataResolver.resolve(item);
            filesParsedOk++;
        } catch (
            FileParsingException e) {
            LOGGER.error("Error Parsing FileName {}:: ", item.getFilename(), e);  // TODO: covered by EM-3582
            return;
        }


        try {
            boolean successful = hrsApiClient.postFile(metadata);
            if (successful) {
                filesSubmittedOk++;
            }

        } catch (IOException | HrsApiException e) {
            LOGGER.error("Error posting {} to em-hrs-api:: ", item.getFilename(), e);  // TODO: covered by EM-3582
        } catch (NumberFormatException e) {
            LOGGER.error("Error Parsing FileName {}:: ", item.getFilename(), e);  // TODO: covered by EM-3582
        } catch (Exception e) {
            LOGGER.error(
                "Unhandled Exception parsing/posting file {}:: ",
                item.getFilename(),
                e
            );  // TODO: covered by EM-3582
        }
    }

}
