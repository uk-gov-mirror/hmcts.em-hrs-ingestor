package uk.gov.hmcts.reform.em.hrs.ingestor.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;

@Component
public class IngestWhenApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IngestWhenApplicationReadyListener.class);

    @Autowired
    private DefaultIngestorService defaultIngestorService;

    @Value("${toggle.cronjob}")
    private boolean enableCronjob;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        LOGGER.info("Enable Cronjob is set to {}", enableCronjob);

        if (enableCronjob) {
            try {
                LOGGER.info("Application Started {}\n...About to Ingest", event.toString());
                boolean isServiceNull = defaultIngestorService == null;
                LOGGER.info("isServiceNull {}", isServiceNull);
                defaultIngestorService.ingest();
                LOGGER.info("Application Shutting Down");//DON'T EXIT UNTIL THIS IS RUNNING AS A CRON JOB
            } catch (Exception e) {
                LOGGER.error("FATAL Error {}", e.getLocalizedMessage());
                System.exit(1); //DON'T EXIT UNTIL THIS IS RUNNING AS A CRON JOB
            }
        }
        LOGGER.info("Application Finished...About to shutdown");
        System.exit(0); //DON'T EXIT UNTIL THIS IS RUNNING AS A CRON JOB
    }
}
