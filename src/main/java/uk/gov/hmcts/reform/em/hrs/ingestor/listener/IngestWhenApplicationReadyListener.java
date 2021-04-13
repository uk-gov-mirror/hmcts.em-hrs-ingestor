package uk.gov.hmcts.reform.em.hrs.ingestor.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;



@Component
public class IngestWhenApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(IngestWhenApplicationReadyListener.class);

    @Autowired
    DefaultIngestorService defaultIngestorService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            log.info("Application Started {}\n...About to Ingest", event.toString());
            boolean isServiceNull = defaultIngestorService == null;
            log.info("isServiceNull {}", isServiceNull);
            defaultIngestorService.ingest();
            log.info("Application Shutting Down");//DON'T EXIT UNTIL THIS IS RUNNING AS A CRON JOB
        } catch (Exception e) {
            log.error("FATAL Error {}", e.getLocalizedMessage());
            System.exit(1); //DON'T EXIT UNTIL THIS IS RUNNING AS A CRON JOB
        }
        System.exit(0); //DON'T EXIT UNTIL THIS IS RUNNING AS A CRON JOB
    }
}
