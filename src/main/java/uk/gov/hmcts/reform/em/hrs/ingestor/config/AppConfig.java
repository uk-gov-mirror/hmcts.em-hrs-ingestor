package uk.gov.hmcts.reform.em.hrs.ingestor.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import uk.gov.hmcts.reform.em.hrs.ingestor.http.HrsHttpClient;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public HrsHttpClient provideHrsHttpClient(final Retrofit retrofit) {
        return retrofit.create(HrsHttpClient.class);
    }

}
