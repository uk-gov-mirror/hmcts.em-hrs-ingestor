package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import com.gc.iotools.stream.os.OutputStreamToInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class DefaultIngestorService implements IngestorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIngestorService.class);

    private final CvpBlobstoreClient cvpBlobstoreClient;
    private final HrsApiClient hrsApiClient;
    private final IngestionFilterer ingestionFilterer;
    private final AntivirusClient antivirusClient;
    private final MetadataResolver metadataResolver;

    @Inject
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
    }

    @Override
    public void ingest() {
        final Set<String> folders = cvpBlobstoreClient.getFolders();
        folders.forEach(x -> {
            final Set<CvpItem> filteredSet = getFilesToIngest(x);
            filteredSet.forEach(y -> {
                if (isFileClean(y.getFilename())) {
                    postToHrsApi(y);
                }
            });
        });
    }

    private Set<CvpItem> getFilesToIngest(final String folder) {
        try {
            final CvpItemSet cvpItemSet = cvpBlobstoreClient.findByFolder(folder);
            final HrsFileSet hrsFileSet = hrsApiClient.getIngestedFiles(folder);
            return ingestionFilterer.filter(cvpItemSet, hrsFileSet);
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
        try {
            final Metadata metadata = metadataResolver.resolve(item);
            hrsApiClient.postFile(metadata);
        } catch (IOException | HrsApiException e) {
            LOGGER.error("Error posting {} to em-hrs-api:: ", item.getFilename(), e);  // TODO: covered by EM-3582
        } catch (FileParsingException e) {
            LOGGER.error("Error Parsing FileName {}:: ", item.getFilename(), e);  // TODO: covered by EM-3582
        } catch (NumberFormatException e) {
            LOGGER.error("Error Parsing FileName {}:: ", item.getFilename(), e);  // TODO: covered by EM-3582
        } catch (Exception e) {
            LOGGER.error("Unhandled Exception parsing/posting file {}:: ",
                         item.getFilename(),
                         e
            );  // TODO: covered by EM-3582
        }
    }

}
