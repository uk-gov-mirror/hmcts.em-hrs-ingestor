package uk.gov.hmcts.reform.em.hrs.ingestor.config;

import fi.solita.clamav.ClamAVClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClamAvConfig {

    @Value("${clamav.host}")
    private String clamAvHost;

    @Value("${clamav.port}")
    private int clamAvPort;

    @Value("${clamav.timeout}")
    private int clamAvTimeout;

    @Bean
    public ClamAVClient provideClamAvClient() {
        return new ClamAVClient(clamAvHost, clamAvPort, clamAvTimeout);
    }

}
