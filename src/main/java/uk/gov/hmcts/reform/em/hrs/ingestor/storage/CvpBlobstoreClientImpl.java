package uk.gov.hmcts.reform.em.hrs.ingestor.storage;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.specialized.BlockBlobClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpFileSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class CvpBlobstoreClientImpl implements CvpBlobstoreClient {
    private final BlobContainerClient blobContainerClient;
    private static final int BLOB_LIST_TIMEOUT = 30;

    @Inject
    public CvpBlobstoreClientImpl(final BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }

    @Override
    public Set<String> getFolders() {
        final BlobListDetails blobListDetails = new BlobListDetails()
            .setRetrieveDeletedBlobs(false)
            .setRetrieveSnapshots(false);
        final ListBlobsOptions options = new ListBlobsOptions()
            .setDetails(blobListDetails);
        final Duration duration = Duration.ofMinutes(BLOB_LIST_TIMEOUT);

        final PagedIterable<BlobItem> blobItems = blobContainerClient.listBlobs(options, duration);

        return blobItems.streamByPage()
            .flatMap(x -> x.getValue().stream()
                .map(y -> {
                    final int separatorIndex = y.getName().indexOf("/");
                    return y.getName().substring(0, separatorIndex);
                }))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public CvpFileSet findByFolder(String folderName) {
        final BlobListDetails blobListDetails = new BlobListDetails()
            .setRetrieveDeletedBlobs(false)
            .setRetrieveSnapshots(false);
        final ListBlobsOptions options = new ListBlobsOptions()
            .setDetails(blobListDetails)
            .setPrefix(folderName);
        final Duration duration = Duration.ofMinutes(BLOB_LIST_TIMEOUT);

        final PagedIterable<BlobItem> blobItems = blobContainerClient.listBlobs(options, duration);

        final Set<String> files = blobItems.streamByPage()
            .flatMap(x -> x.getValue().stream().map(BlobItem::getName))
            .collect(Collectors.toUnmodifiableSet());

        return new CvpFileSet(files);
    }

    @Override
    public void downloadFile(final String filename, final ByteArrayOutputStream output) throws IOException {
        final BlockBlobClient blobClient = blobContainerClient.getBlobClient(filename).getBlockBlobClient();
        final int dataSize = (int) blobClient.getProperties().getBlobSize();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(dataSize);
        blobClient.download(outputStream);
        outputStream.writeTo(output);
        outputStream.close();
    }
}
