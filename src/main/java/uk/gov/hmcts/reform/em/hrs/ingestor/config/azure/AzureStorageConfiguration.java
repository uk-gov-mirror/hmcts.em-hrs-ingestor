package uk.gov.hmcts.reform.em.hrs.ingestor.config.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.net.InetAddress;
import java.net.UnknownHostException;

//@Configuration
public class AzureStorageConfiguration {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.blob-container-reference}")
    private String containerReference;

    @Bean
    BlobContainerClient cloudBlobContainer() throws UnknownHostException {
        String blobAddress = connectionString.contains("azure-storage-emulator-azurite")
            ? connectionString.replace(
                "azure-storage-emulator-azurite",
                InetAddress.getByName("azure-storage-emulator-azurite").getHostAddress())
            : connectionString;

        final BlobContainerClient client = new BlobContainerClientBuilder()
            .connectionString(blobAddress)
            .containerName(containerReference)
            .buildClient();

        try {
            client.create();
            return client;
        } catch (BlobStorageException e) {
            return client;
        }
    }

}
