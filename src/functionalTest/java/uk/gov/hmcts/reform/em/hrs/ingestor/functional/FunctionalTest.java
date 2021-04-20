package uk.gov.hmcts.reform.em.hrs.ingestor.functional;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.HrsApiException;
import uk.gov.hmcts.reform.em.hrs.ingestor.http.HrsApiClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.HrsFileSet;

import java.io.IOException;

@SpringBootTest
@TestPropertySource("classpath:application.yml")
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
public class FunctionalTest {

    private static final String HRS_BLOBSTORE_FOLDER1 = "audiostream999997";
    private static final String HRS_BLOBSTORE_FOLDER2 = "audiostream999998";
    private static final String HRS_BLOBSTORE_FOLDER3 = "audiostream999999";

    @Value("${test.url}")
    private String testUrl;

    @Value("${hrs-api.test.url}")
    private String HrsApiTestUrl;

    @Autowired
    private HrsApiClient hrsApiClient;

    @Test
    public void shouldIngestFilesFromCVPBlobStoreToHrsBlobStore() throws IOException, HrsApiException, InterruptedException {

        SerenityRest
            .given()
            .baseUri(testUrl)
            .when()
            .get("/ingest")
            .then().log().all()
            .assertThat()
            .statusCode(200);

        Thread.sleep(10000);

//        Response response =
//            SerenityRest
//                .given()
//                .baseUri(HrsApiTestUrl)
//                .when()
//                .get("/folders/" + HRS_BLOBSTORE_FOLDER1)
//                .then().log().all()
//                .assertThat()
//                .statusCode(200)
//                .extract().response();

//        System.out.println(response.getBody().prettyPrint());
         HrsFileSet ingestedFilesForFolder1 = hrsApiClient.getIngestedFiles(HRS_BLOBSTORE_FOLDER1);
//        while(ingestedFilesForFolder1.getHrsFiles().size()!=0){
//            ingestedFilesForFolder1 = hrsApiClient.getIngestedFiles(HRS_BLOBSTORE_FOLDER1);
//        }
//        final HrsFileSet ingestedFilesForFolder2 = hrsApiClient.getIngestedFiles(HRS_BLOBSTORE_FOLDER2);
//        final HrsFileSet ingestedFilesForFolder3 = hrsApiClient.getIngestedFiles(HRS_BLOBSTORE_FOLDER3);

        final int folder1FilesSize = ingestedFilesForFolder1.getHrsFiles().size();
//        final int folder2FilesSize = ingestedFilesForFolder2.getHrsFiles().size();
//        final int folder3FilesSize = ingestedFilesForFolder3.getHrsFiles().size();

        System.out.println(folder1FilesSize);
//        System.out.println(folder2FilesSize);
//        System.out.println(folder3FilesSize);
    }
}
