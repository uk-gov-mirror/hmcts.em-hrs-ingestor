package uk.gov.hmcts.reform.em.hrs.ingestor.functional.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class AzureOperations {

    @Value("${azure.storage.hrs.connection-string}")
    private String hrsConnectionString;

    @Value("${azure.storage.hrs.blob-container-reference}")
    private String hrsContainer;

    @Bean
    @Primary
    public BlobContainerClient hrsBlobContainerClient() {
        return new BlobContainerClientBuilder()
            .connectionString(hrsConnectionString)
            .containerName(hrsContainer)
            .buildClient();
    }
}
