package uk.gov.hmcts.reform.em.hrs.ingestor.config;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureStorageConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureStorageConfiguration.class);


    @Value("${azure.storage.cvp-storage-connection-string}")
    private String connectionString;

    @Value("${azure.storage.cvp-storage-container-name}")
    private String containerReference;

    @Bean
    BlobContainerClient provideBlobContainerClient() {
        LOGGER.info("****************************");
        LOGGER.info("Starting Up");
        LOGGER.info("****************************");
        LOGGER.info("connection string: {}", connectionString);



        //debugging connection string for cvp storage
        if (connectionString.contains("cvprecordings")) {
            LOGGER.info("****************************");
            LOGGER.info("connection string: {}", connectionString);
            LOGGER.info("container name: {}",containerReference);
            LOGGER.info("Building client with default credential builder (will attempt ManagedIdentityCredential");
            LOGGER.info("****************************");
            BlobContainerClientBuilder clientBuilder = new BlobContainerClientBuilder()
                .endpoint(connectionString)
                .containerName(containerReference);

            DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();
            clientBuilder.credential(credential);
            return clientBuilder.buildClient();
        }

        BlobContainerClientBuilder clientBuilder = new BlobContainerClientBuilder()
            .connectionString(connectionString)
            .containerName(containerReference);

        return clientBuilder.buildClient();
    }
}
