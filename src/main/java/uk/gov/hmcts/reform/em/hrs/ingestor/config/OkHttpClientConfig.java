package uk.gov.hmcts.reform.em.hrs.ingestor.config;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkHttpClientConfig {

    @Bean
    public OkHttpClient provideOkHttpClient() {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC);

        return new OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build();
    }

}
