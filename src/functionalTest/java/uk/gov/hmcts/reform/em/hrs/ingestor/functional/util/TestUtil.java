package uk.gov.hmcts.reform.em.hrs.ingestor.functional.util;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.devskiller.jfairy.Fairy;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TestUtil {

    private final BlobContainerClient hrsBlobContainerClient;
    private final BlobContainerClient cvpBlobContainerClient;
    private static final int BLOB_LIST_TIMEOUT = 5;
    private final Fairy fairy;

    public TestUtil(BlobContainerClient hrsBlobContainerClient,
                    BlobContainerClient cvpBlobContainerClient) {
        this.hrsBlobContainerClient = hrsBlobContainerClient;
        this.cvpBlobContainerClient = cvpBlobContainerClient;
        this.fairy = Fairy.create();
    }

    public Set<String> getHrsBlobsFrom(final String folder) {
        final BlobListDetails blobListDetails = new BlobListDetails()
            .setRetrieveDeletedBlobs(false)
            .setRetrieveSnapshots(false);
        final ListBlobsOptions options = new ListBlobsOptions()
            .setDetails(blobListDetails)
            .setPrefix(folder);
        final Duration duration = Duration.ofMinutes(BLOB_LIST_TIMEOUT);
        final PagedIterable<BlobItem> blobItems = hrsBlobContainerClient.listBlobs(options, duration);

        return blobItems.streamByPage()
            .flatMap(blobItemPagedResponse -> blobItemPagedResponse.getValue().stream().map(BlobItem::getName))
            .collect(Collectors.toUnmodifiableSet());
    }

    public void clearHrsContainer() {
        hrsBlobContainerClient.listBlobs()
            .stream()
            .filter(blobItem -> getListOfTestBlobs().contains(blobItem.getName()))
            .forEach(blobItem -> blobItem.setDeleted(true));
    }

    public void clearCvpContainer() {
        cvpBlobContainerClient.listBlobs()
            .stream()
            .filter(blobItem -> getListOfTestBlobs().contains(blobItem.getName()))
            .forEach(blobItem -> blobItem.setDeleted(true));
    }

    public static List<String> getListOfTestBlobs() {
        return new ArrayList<>() {
            {
                add("audiostream999997/FM-0123-BV20D01_2020-11-04-14.56.32.819-UTC_0.mp4");
                add("audiostream999997/FM-0123-BV20D01_2020-11-04-14.56.32.819-UTC_1.mp4");
                add("audiostream999997/FM-0123-BV20D01_2020-11-04-14.56.32.819-UTC_2.mp4");
                add("audiostream999997/FM-0123-BV20D01_2020-11-04-14.56.32.819-UTC_3.mp4");
                add("audiostream999997/FM-0123-BV20D01_2020-11-04-14.56.32.819-UTC_4.mp4");
                add("audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_0.mp4");
                add("audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_1.mp4");
                add("audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_2.mp4");
                add("audiostream999999/FM-0789-EF31D01_2020-11-06-16.26.12.419-UTC_0.mp4");
            }
        };
    }

    public void uploadToContainer(final Enum<Container> container, final String filePath) {
        final String content = fairy.textProducer().sentence();
        final InputStream data = new ByteArrayInputStream(content.getBytes());

        final BlobClient blobClient = container.equals(Container.CVP)
            ? cvpBlobContainerClient.getBlobClient(filePath)
            : hrsBlobContainerClient.getBlobClient(filePath);
        blobClient.upload(new BufferedInputStream(data), content.length());
    }

    public BlobContainerClient getHrsBlobContainerClient() {
        return hrsBlobContainerClient;
    }

    public BlobContainerClient getCvpBlobContainerClient() {
        return cvpBlobContainerClient;
    }

    private enum Container {
        CVP,
        HRS
    }
}
