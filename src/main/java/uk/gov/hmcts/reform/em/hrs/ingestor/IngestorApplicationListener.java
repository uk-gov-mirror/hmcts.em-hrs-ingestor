package uk.gov.hmcts.reform.em.hrs.ingestor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;

import javax.inject.Inject;


@Component
@Slf4j
public class IngestorApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    @Inject
    DefaultIngestorService service;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Application Started, about to Ingest");
        service.ingest();
    }
}
