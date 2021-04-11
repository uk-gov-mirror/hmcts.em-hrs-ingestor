package uk.gov.hmcts.reform.em.hrs.ingestor.storage;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItemProperties;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItemSet;

import java.io.OutputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CvpBlobstoreClientImpl implements CvpBlobstoreClient {
    private static final int BLOB_LIST_TIMEOUT = 30;
    private final BlobContainerClient blobContainerClient;

    @Autowired
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
    public CvpItemSet findByFolder(String folderName) {
        final BlobListDetails blobListDetails = new BlobListDetails()
            .setRetrieveDeletedBlobs(false)
            .setRetrieveSnapshots(false);
        final ListBlobsOptions options = new ListBlobsOptions()
            .setDetails(blobListDetails)
            .setPrefix(folderName);
        final Duration duration = Duration.ofMinutes(BLOB_LIST_TIMEOUT);

        final PagedIterable<BlobItem> blobItems = blobContainerClient.listBlobs(options, duration);

        return transform(blobItems);
    }

    @Override
    public void downloadFile(final String filename, final OutputStream output) {
        final BlockBlobClient blobClient = blobContainerClient.getBlobClient(filename).getBlockBlobClient();
        blobClient.download(output);
    }

    private CvpItemSet transform(final PagedIterable<BlobItem> blobItems) {
        final Set<CvpItem> files = blobItems.streamByPage()
            .flatMap(x -> x.getValue().stream().map(y -> {
                final BlobItemProperties blobItemProperties = y.getProperties();
                final String md5Hash = getMd5Hash(blobItemProperties.getContentMd5());
                final String filename = y.getName();

                return new CvpItem(filename, getUrl(filename), md5Hash, blobItemProperties.getContentLength());
            }))
            .collect(Collectors.toUnmodifiableSet());

        return new CvpItemSet(files);
    }

    private String getUrl(final String filename) {
        final BlockBlobClient blobClient = blobContainerClient.getBlobClient(filename).getBlockBlobClient();
        return blobClient.getBlobUrl();
    }

    private String getMd5Hash(final byte[] digest) {
        return Base64.getEncoder().encodeToString(digest);
    }

}
