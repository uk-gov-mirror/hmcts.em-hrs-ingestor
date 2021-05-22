package uk.gov.hmcts.reform.em.hrs.ingestor.storage;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BlobHelperTest {

    private static final Logger log = LoggerFactory.getLogger(BlobHelperTest.class);


    @Test
    void testParsesBadFoldersWithoutExceptions() {
        BlobHelper.parseFolderFromPath("");
        BlobHelper.parseFolderFromPath("/folder");
        BlobHelper.parseFolderFromPath("/folder/");
        BlobHelper.parseFolderFromPath("/folder/with_filename.mp4");
    }


    @Test
    void testParsesNullMD5WithoutExceptions() {
        String expected = BlobHelper.NULLMD_5;
        String actual = BlobHelper.getMd5Hash(null);
        Assertions.assertEquals(expected, actual);
    }


}
