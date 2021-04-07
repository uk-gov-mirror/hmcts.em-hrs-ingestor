package uk.gov.hmcts.reform.em.hrs.ingestor.smoke;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource("classpath:application.yml")
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Smoke")})
public class SmokeTest {

    @MockBean
    private DefaultIngestorService DIS;

    private static final String MESSAGE = "Welcome to Hearing Recordings Ingestor";

    @Value("${test.url}")
    private String testUrl;

    @Test
    public void testHealthEndpoint() {

        SerenityRest.useRelaxedHTTPSValidation();

        String response =
            SerenityRest
                .given()
                .baseUri(testUrl)
                .when()
                .get("/")
                .then()
                .statusCode(200).extract().body().asString();


        assertEquals(MESSAGE, response, "");
    }

    @Test
    void test_ingest() {
        DIS.ingest();
    }
}
