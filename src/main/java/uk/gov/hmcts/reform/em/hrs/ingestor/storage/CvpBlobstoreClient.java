package uk.gov.hmcts.reform.em.hrs.ingestor.storage;

import java.util.Set;

public interface CvpBlobstoreClient {
    Set<String> getFolders();

    Set<String> findByFolder(String folderName);
}
