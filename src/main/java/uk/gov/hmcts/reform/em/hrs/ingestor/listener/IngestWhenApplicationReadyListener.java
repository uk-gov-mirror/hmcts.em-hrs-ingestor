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
                LOGGER.info("Application Started {}\n...About to Ingest", event);
                defaultIngestorService.ingest();
                LOGGER.info("Initial Ingestion Complete", event);
            } catch (Exception e) {
                LOGGER.error("Unhandled Exception  during Ingestion - Aborted ... {}");
                e.printStackTrace();
            }


                LOGGER.info("Application Shutting Down");
                shutDownGracefully();

        }
    }

    private void shutDownGracefully() {
        long minutesBeforeShutdownAfterInitialIngestion =
            10;//TODO consider making this configurable/forced during AAT.staging & preview tests
        long msBeforeShutdown = 1000 * 60 * minutesBeforeShutdownAfterInitialIngestion;
        LOGGER.info(
            "Application Finished...Waiting {} mins to shutdown to allow for functional tests",
            minutesBeforeShutdownAfterInitialIngestion
        );

        try {
            Thread.sleep(msBeforeShutdown);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("Application Shutdown Delay completed, now exiting System.");
        System.exit(0);
    }
}
