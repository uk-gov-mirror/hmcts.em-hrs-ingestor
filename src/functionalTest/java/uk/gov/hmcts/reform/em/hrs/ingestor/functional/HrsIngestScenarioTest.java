package uk.gov.hmcts.reform.em.hrs.ingestor.functional;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.hrs.ingestor.functional.config.AzureClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.functional.util.TestUtil;

import java.time.Duration;
import java.util.Set;

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
    private static final Duration THIRTY_SECONDS = Duration.ofSeconds(30);

    private static final Logger LOGGER = LoggerFactory.getLogger(HrsIngestScenarioTest.class);


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
        Set<String> currentContents = testUtil.getHrsBlobsFrom("/");

        LOGGER.info("Current Contents of HRS Folder: {}",String.join(",", currentContents));

        LOGGER.info("Waiting for folder to be empty");
        await()
            .atMost(THIRTY_SECONDS)
            .untilAsserted(() -> assertThat(testUtil.getHrsBlobsFrom(HRS_BLOBSTORE_FOLDER1))
                .size()
                .isEqualTo(0));


        currentContents = testUtil.getHrsBlobsFrom("/");
        LOGGER.info("Current Contents of HRS Folder: {}",String.join(",", currentContents));


        LOGGER.info("Ingesting");
        SerenityRest
            .given()
            .baseUri(testUrl)
            .get("/ingest")
            .then()
            .assertThat()
            .statusCode(200);


        LOGGER.info("Waiting 10 seconds"));
        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        currentContents = testUtil.getHrsBlobsFrom("/");
        LOGGER.info("Current Contents of HRS Folder: {}",String.join(",", currentContents));


        LOGGER.info("Checking for folder 1 contents");
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
