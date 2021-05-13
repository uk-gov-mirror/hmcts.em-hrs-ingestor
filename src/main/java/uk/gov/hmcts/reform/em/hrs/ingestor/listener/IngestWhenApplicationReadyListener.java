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
    boolean shouldShutDownAfterInitialIngestion = enableCronjob;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        LOGGER.info("Enable Cronjob is set to {}", enableCronjob);

        if (enableCronjob) {
            try {
                LOGGER.info("Application Started {}\n...About to Ingest", event.toString());
                boolean isServiceNull = defaultIngestorService == null;
                LOGGER.info("isServiceNull {}", isServiceNull);
                defaultIngestorService.ingest();
                Thread.sleep(30 *   // minutes to sleep
                    60 *   // seconds to a minute
                    1000); // milliseconds to a second
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
//    @Override
//    public void onApplicationEvent(ApplicationReadyEvent event) {
//        try {
//            LOGGER.info("Application Started {}\n...About to Ingest", event.toString());
//            boolean isServiceNull = defaultIngestorService == null;
//            LOGGER.info("isServiceNull {}", isServiceNull);
//            defaultIngestorService.ingest();
//
//        } catch (Exception e) {
//            LOGGER.error("Unhandled Exception  during Ingestion - Aborted ... {}", e.getMessage());
//            e.printStackTrace();
//        }
//
//        if (shouldShutDownAfterInitialIngestion) {
//            LOGGER.info("Application Shutting Down");
//            shutDownGracefully();
//        }
//    }
//
//    private void shutDownGracefully() {
//        long minutesBeforeShutdownAfterInitialIngestion = 10;
//        long msBeforeShutdown = 1000 * 60 * minutesBeforeShutdownAfterInitialIngestion;
//        LOGGER.info("Application Finished...Waiting {} mins to shutdown to allow for functional tests",
//                    minutesBeforeShutdownAfterInitialIngestion);
//
//        try {
//            Thread.sleep(msBeforeShutdown);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        LOGGER.info("Application Shutdown Delay completed, now exiting System.");
//        System.exit(0);
//    }
