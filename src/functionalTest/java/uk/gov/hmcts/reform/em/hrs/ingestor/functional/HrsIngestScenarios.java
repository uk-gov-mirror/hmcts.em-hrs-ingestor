package uk.gov.hmcts.reform.em.hrs.ingestor.functional;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.hrs.ingestor.functional.config.AzureClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.functional.util.TestUtil;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = {AzureClient.class, TestUtil.class})
@TestPropertySource("classpath:application.yml")
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
public class HrsIngestScenarios {

    private static final String HRS_BLOBSTORE_FOLDER1 = "audiostream999997";
    private static final String HRS_BLOBSTORE_FOLDER2 = "audiostream999998";
    private static final String HRS_BLOBSTORE_FOLDER3 = "audiostream999999";
    private static final Duration THIRTY_SECONDS = Duration.ofSeconds(30);

    @Value("${test.url}")
    private String testUrl;

    @Autowired
    private TestUtil testUtil;

    @Before
    public void setup() {
        testUtil.clearHrsContainer();
    }

    @After
    public void clear() {
        testUtil.clearHrsContainer();
    }

    @Test
    public void shouldIngestFilesFromCvpBlobStoreToHrsBlobStore() throws InterruptedException {
        SerenityRest
            .given()
            .relaxedHTTPSValidation()
            .baseUri(testUrl)
            .get("/ingest")
            .then().log().all()
            .assertThat()
            .statusCode(200);


        await()
            .atMost(THIRTY_SECONDS)
            .untilAsserted(() -> assertThat(testUtil.getHrsBlobsFrom(HRS_BLOBSTORE_FOLDER1))
                .size()
                .isEqualTo(5));

        await()
            .atMost(THIRTY_SECONDS)
            .untilAsserted(() -> assertThat(testUtil.getHrsBlobsFrom(HRS_BLOBSTORE_FOLDER2))
                .size()
                .isEqualTo(3));

        await()
            .atMost(THIRTY_SECONDS)
            .untilAsserted(() -> assertThat(testUtil.getHrsBlobsFrom(HRS_BLOBSTORE_FOLDER3))
                .size()
                .isEqualTo(1));
    }
}
