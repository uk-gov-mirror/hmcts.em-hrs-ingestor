package uk.gov.hmcts.reform.em.hrs.ingestor.functional.util;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TestUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtil.class);

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

        LOGGER.warn("Getting blobs from hrs blob client, for folder: {} ", folder);

        final PagedIterable<BlobItem> blobItems = hrsBlobContainerClient.listBlobs(options, duration);

        return blobItems.streamByPage()
            .flatMap(blobItemPagedResponse -> blobItemPagedResponse.getValue().stream().map(BlobItem::getName))
            .collect(Collectors.toUnmodifiableSet());
    }

    public void clearHrsContainer() {
        LOGGER.warn("Clearing HRS Container of all blobs");
        hrsBlobContainerClient.listBlobs()
            .stream()
            .filter(blobItem -> getListOfTestBlobs().contains(blobItem.getName()))
            .forEach(blobItem -> {
                final BlobClient blobClient = hrsBlobContainerClient.getBlobClient(blobItem.getName());
                blobClient.delete();
            });
    }

    public List<String> getFilesForFolder(final String folderName) {
        return blobsMap().get(folderName);
    }

    public List<String> getListOfTestBlobs() {
        return new ArrayList<>() {
            {
                add("audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_0.mp4");
                add("audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_1.mp4");
                add("audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_2.mp4");
                add("audiostream999999/FM-0789-EF31D01_2020-11-06-16.26.12.419-UTC_0.mp4");
            }
        };
    }

    public Map<String, List<String>> blobsMap() {
        return new HashMap<>() {{
            put("audiostream999998", List.of(
                "audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_0.mp4",
                "audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_1.mp4",
                "audiostream999998/FM-0456-CD30D01_2020-11-05-15.36.42.619-UTC_2.mp4"
            ));
            put("audiostream999999", List.of("audiostream999999/FM-0789-EF31D01_2020-11-06-16.26.12.419-UTC_0.mp4"));
        }};
    }
}
