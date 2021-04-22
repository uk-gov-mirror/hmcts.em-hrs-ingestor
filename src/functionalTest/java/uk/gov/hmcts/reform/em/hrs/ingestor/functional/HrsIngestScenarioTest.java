package uk.gov.hmcts.reform.em.hrs.ingestor.functional;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class HrsIngestScenarioTest {

    private static final String HRS_BLOBSTORE_FOLDER1 = "audiostream999997";
    private static final String HRS_BLOBSTORE_FOLDER2 = "audiostream999998";
    private static final String HRS_BLOBSTORE_FOLDER3 = "audiostream999999";
    private static final Duration TEN_SECONDS = Duration.ofSeconds(10);

    @Value("${test.url}")
    private String testUrl;

    @Autowired
    private TestUtil testUtil;

    @BeforeEach
    public void setup() {
        testUtil.clearHrsContainer();
    }

    @AfterEach
    public void clear() {
        testUtil.clearHrsContainer();
    }

    @Test
    public void shouldIngestFilesFromCvpBlobStoreToHrsBlobStore() {

        SerenityRest
            .given()
            .baseUri(testUrl)
            .get("/ingest")
            .then()
            .assertThat()
            .statusCode(200);

        await()
            .atMost(TEN_SECONDS)
            .untilAsserted(() -> assertThat(testUtil.getHrsBlobsFrom(HRS_BLOBSTORE_FOLDER1))
                .containsAll(testUtil.getFilesForFolder(HRS_BLOBSTORE_FOLDER1))
                .size()
                .isEqualTo(5));

        await()
            .atMost(TEN_SECONDS)
            .untilAsserted(() -> assertThat(testUtil.getHrsBlobsFrom(HRS_BLOBSTORE_FOLDER2))
                .containsAll(testUtil.getFilesForFolder(HRS_BLOBSTORE_FOLDER2))
                .size()
                .isEqualTo(3));

        await()
            .atMost(TEN_SECONDS)
            .untilAsserted(() -> assertThat(testUtil.getHrsBlobsFrom(HRS_BLOBSTORE_FOLDER3))
                .containsAll(testUtil.getFilesForFolder(HRS_BLOBSTORE_FOLDER3))
                .size()
                .isEqualTo(1));
    }
}
