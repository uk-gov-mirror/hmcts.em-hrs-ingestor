package uk.gov.hmcts.reform.em.hrs.ingestor.helper;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.devskiller.jfairy.Fairy;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class AzureOperations {
    private final BlobContainerClient blobContainerClient;
    private final Fairy fairy;

    @Inject
    public AzureOperations(BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
        fairy = Fairy.create();
    }

    public void uploadToContainer(final Set<String> filePaths) {
        filePaths.forEach(this::uploadToContainer);
    }

    public void uploadToContainer(final String filePath) {
        final String content = fairy.textProducer().sentence();
        final InputStream data = new ByteArrayInputStream(content.getBytes());

        final BlobClient blobClient = blobContainerClient.getBlobClient(filePath);
        blobClient.upload(new BufferedInputStream(data), content.length());
    }

    public void clearContainer() {
        blobContainerClient.listBlobs()
            .forEach(x -> x.setDeleted(true));
    }
}
