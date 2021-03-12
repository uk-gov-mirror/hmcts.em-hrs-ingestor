package uk.gov.hmcts.reform.em.hrs.ingestor.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import uk.gov.hmcts.reform.em.hrs.ingestor.http.HrsHttpClient;

@Configuration
public class AppConfig {

    @Value("${s2s.hrs.api.url}")
    private String hrsApiBaseUrl;

    @Bean
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public Retrofit provideRetrofit(final OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
            .baseUrl(hrsApiBaseUrl)
            .addConverterFactory(JacksonConverterFactory.create())
            .client(okHttpClient)
            .build();
    }

    @Bean
    public HrsHttpClient provideHrsHttpClient(final Retrofit retrofit) {
        return retrofit.create(HrsHttpClient.class);
    }

}
