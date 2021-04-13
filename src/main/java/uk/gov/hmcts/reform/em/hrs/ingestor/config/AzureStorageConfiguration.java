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
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;

@Configuration
public class AzureStorageConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureStorageConfiguration.class);


    @Value("${azure.storage.cvp-storage-connection-string}")
    private String connectionString;

    @Value("${azure.storage.cvp-storage-container-name}")
    private String containerReference;

    @Bean
    BlobContainerClient provideBlobContainerClient() {

        LOGGER.info("connection string starts with"+ StringUtils.left(connectionString, 5));
        LOGGER.info("container name starts with"+StringUtils.left(containerReference,5));

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
