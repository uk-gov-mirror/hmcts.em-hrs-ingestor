package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.AntivirusClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.AvScanResult;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.HrsFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.HrsApiException;
import uk.gov.hmcts.reform.em.hrs.ingestor.http.HrsApiClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.storage.CvpBlobstoreClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @Inject
    public DefaultIngestorService(final CvpBlobstoreClient cvpBlobstoreClient,
                                  final HrsApiClient hrsApiClient,
                                  final IngestionFilterer ingestionFilterer,
                                  final AntivirusClient antivirusClient) {
        this.cvpBlobstoreClient = cvpBlobstoreClient;
        this.hrsApiClient = hrsApiClient;
        this.ingestionFilterer = ingestionFilterer;
        this.antivirusClient = antivirusClient;
    }

    @Override
    public void ingest() {
        final Set<String> folders = cvpBlobstoreClient.getFolders();
        folders.forEach(x -> {
            final Set<String> filteredSet = getFilesToIngest(x);
            filteredSet.forEach(y -> {
                if (isFileClean(y)) {
                    // parse metadata
                    hrsApiClient.post(y);
                }
            });
        });
    }

    private Set<String> getFilesToIngest(final String folder) {
        try {
            final CvpFileSet cvpFileSet = cvpBlobstoreClient.findByFolder(folder);
            final HrsFileSet hrsFileSet = hrsApiClient.getIngestedFiles(folder);
            return ingestionFilterer.filter(cvpFileSet, hrsFileSet);
        } catch (HrsApiException | IOException e) {
            LOGGER.error("", e); // TODO: covered by EM-3582
            return Collections.emptySet();
        }
    }

    private boolean isFileClean(final String file) {
        try {
            final AvScanResult result = downloadAndScan(file);
            if (result == AvScanResult.INFECTED) {
                LOGGER.info("File is infected: " + file);  // TODO: covered by EM-3582
            }
            return result == AvScanResult.CLEAN;
        } catch (IOException e) {
            LOGGER.error("Error AV checking {}: ", file, e);
            return false;
        }
    }

    private AvScanResult downloadAndScan(final String file) throws IOException {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            cvpBlobstoreClient.downloadFile(file, output);
            try (final ByteArrayInputStream inStream = new ByteArrayInputStream(output.toByteArray())) {
                return antivirusClient.scan(inStream);
            }
        }
    }
}
