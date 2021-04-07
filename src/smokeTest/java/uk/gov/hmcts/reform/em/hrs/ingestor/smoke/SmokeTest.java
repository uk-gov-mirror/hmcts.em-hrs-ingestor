package uk.gov.hmcts.reform.em.hrs.ingestor.smoke;

import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;

import javax.inject.Inject;

@SpringBootTest
@TestPropertySource("classpath:application.yml")
@WithTags({@WithTag("testType:Smoke")})
public class SmokeTest {

    @Inject
    private DefaultIngestorService defaultIngestorService;


    @Value("${test.url}")
    private String testUrl;

    @Test
    void test_ingest() {
        defaultIngestorService.ingest();
    }
}
