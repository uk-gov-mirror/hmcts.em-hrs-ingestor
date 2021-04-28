package uk.gov.hmcts.reform.em.hrs.ingestor.storage;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CvpBlobstoreClientImplTest {

    private static final Logger log = LoggerFactory.getLogger(CvpBlobstoreClientImplTest.class);


    CvpBlobstoreClientImpl cvpBlobstoreClient = new CvpBlobstoreClientImpl(null);

    @Test
    void testParsesBadFoldersWithoutExceptions() {
        cvpBlobstoreClient.parseFolderFromPath("");
        cvpBlobstoreClient.parseFolderFromPath("/folder");
        cvpBlobstoreClient.parseFolderFromPath("/folder/");
        cvpBlobstoreClient.parseFolderFromPath("/folder/with_filename.mp4");
    }

}
