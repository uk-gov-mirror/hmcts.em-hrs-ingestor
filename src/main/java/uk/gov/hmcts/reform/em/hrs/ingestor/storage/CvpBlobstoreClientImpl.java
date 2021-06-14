package uk.gov.hmcts.reform.em.hrs.ingestor.storage;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItemProperties;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItemSet;

import java.io.OutputStream;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CvpBlobstoreClientImpl implements CvpBlobstoreClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CvpBlobstoreClient.class);

    private static final int BLOB_LIST_TIMEOUT = 30;
    private final BlobContainerClient blobContainerClient = null;

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
            .flatMap(pagedResponse -> pagedResponse.getValue().stream()
                .map(blobItem -> {
                    LOGGER.info("Processing blobItem");
                    String filePath = blobItem.getName();
                    LOGGER.info("File Path {}", filePath);
                    String folder = BlobHelper.parseFolderFromPath(filePath);
                    return folder;
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
                final String md5Hash = BlobHelper.getMd5Hash(blobItemProperties.getContentMd5());
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


}
