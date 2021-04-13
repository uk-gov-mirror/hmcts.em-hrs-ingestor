package uk.gov.hmcts.reform.em.hrs.ingestor.config;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureStorageConfiguration {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.cvp-blob-container-reference}")
    private String containerReference;
    private BlobContainerClientBuilder client;

    @Bean
    BlobContainerClient provideBlobContainerClient() {


        BlobContainerClientBuilder clientBuilder = new BlobContainerClientBuilder()
            .connectionString(connectionString)
            .containerName(containerReference);

        //only local simulation storage uses http
        if (connectionString.contains("https")) {
            DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();
            clientBuilder.credential(credential);
        }

        return clientBuilder.buildClient();
    }
}
