package uk.gov.hmcts.reform.em.hrs.ingestor.av.mock;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class ClamAvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClamAvInitializer.class);

    private static final String CLAMAV_IMAGE = "citizensadvice/clamav-mock";
    private static final int MAPPER_PORT = 3310;

    //docker run --rm -p 3310:3310 citizensadvice/clamav-mock
    private final GenericContainer<?> clamAvContainer = new GenericContainer<>(CLAMAV_IMAGE)
        .withExposedPorts(MAPPER_PORT)
        .withLogConsumer(new Slf4jLogConsumer(LOGGER))
        .waitingFor(Wait.forListeningPort());

    @Override
    public void initialize(@NotNull final ConfigurableApplicationContext applicationContext) {
        if (!clamAvContainer.isRunning()) {
            clamAvContainer.start();
        }

        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                if (clamAvContainer.isRunning()) {
                    clamAvContainer.stop();
                }
            }
        });

        TestPropertyValues
            .of("clamav.port=" + clamAvContainer.getFirstMappedPort())
            .applyTo(applicationContext);
    }
}
