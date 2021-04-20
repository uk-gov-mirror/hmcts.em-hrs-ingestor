package uk.gov.hmcts.reform.em.hrs.ingestor.functional;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource("classpath:application.yml")
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
public class FunctionalTest {

    private static final String MESSAGE = "Welcome to Hearing Recordings Ingestor";

    @Value("${test.url}")
    private String testUrl;


    @Test
    public void testHealthEndpoint() {

        assertTrue(true);

    }
}
