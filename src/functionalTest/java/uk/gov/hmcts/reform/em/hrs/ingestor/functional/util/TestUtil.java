package uk.gov.hmcts.reform.em.hrs.ingestor.functional.util;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TestUtil {

    private final BlobContainerClient hrsBlobContainerClient;
    private static final int BLOB_LIST_TIMEOUT = 5;

    public TestUtil(BlobContainerClient hrsBlobContainerClient) {
        this.hrsBlobContainerClient = hrsBlobContainerClient;
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
            .flatMap(x -> x.getValue().stream().map(BlobItem::getName))
            .collect(Collectors.toUnmodifiableSet());
    }

    public void clearContainer() {
        hrsBlobContainerClient.listBlobs()
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
}
