package uk.gov.hmcts.reform.em.hrs.ingestor.storage;

import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpFileSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

public interface CvpBlobstoreClient {
    Set<String> getFolders();

    CvpFileSet findByFolder(String folderName);

    void downloadFile(String filename, ByteArrayOutputStream output) throws IOException;
}
