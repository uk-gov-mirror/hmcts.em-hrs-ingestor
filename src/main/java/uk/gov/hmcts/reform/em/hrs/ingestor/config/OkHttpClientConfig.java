package uk.gov.hmcts.reform.em.hrs.ingestor.config;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

@Configuration
public class OkHttpClientConfig {

    @Bean
    public OkHttpClient provideOkHttpClient(final AuthTokenGenerator authTokenGenerator) {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC);

        return new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                final Request original = chain.request();

                final Request request = original.newBuilder()
                    .header("ServiceAuthorization", authTokenGenerator.generate())
                    .method(original.method(), original.body())
                    .build();

                return chain.proceed(request);
            })
            .addInterceptor(loggingInterceptor)
            .build();
    }

}
