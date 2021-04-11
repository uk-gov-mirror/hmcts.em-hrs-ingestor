package uk.gov.hmcts.reform.em.hrs.ingestor.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;

import javax.inject.Inject;
import javax.inject.Named;


@Slf4j
public class IngestWhenApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Inject
    DefaultIngestorService defaultIngestorService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            log.info("Application Started {}\n...About to Ingest", event.toString());
            boolean isServiceNull = defaultIngestorService == null;
            log.info("isServiceNull {}", isServiceNull);
//            defaultIngestorService.ingest();
//            log.info("Application Shutting Down");
        } catch (Exception e) {
            log.error("FATAL Error {}", e.getLocalizedMessage());
            System.exit(1);
        }
//        System.exit(0);
    }
}
