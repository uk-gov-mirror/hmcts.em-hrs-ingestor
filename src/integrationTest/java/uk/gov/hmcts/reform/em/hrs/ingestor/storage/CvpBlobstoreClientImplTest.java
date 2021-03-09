package uk.gov.hmcts.reform.em.hrs.ingestor.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.TestAzureStorageConfiguration;
import uk.gov.hmcts.reform.em.hrs.ingestor.helper.AzureOperations;

import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = {TestAzureStorageConfiguration.class, CvpBlobstoreClientImpl.class, AzureOperations.class}
)
class CvpBlobstoreClientImplTest {
    @Inject
    private AzureOperations azureOperations;
    @Inject
    private CvpBlobstoreClientImpl underTest;

    private static final String FOLDER_ONE = "folder-1";
    private static final String FOLDER_TWO = "folder-2";
    private static final String FOLDER_THREE = "folder-3";
    private static final String MANY_ITEMS_FOLDER = "many-items-folder";

    @BeforeEach
    void setup() {
        azureOperations.clearContainer();
    }

    @Test
    void testShouldGetFolders() {
        populateCvpBlobstore();

        final Set<String> folders = underTest.getFolders();

        assertThat(folders).isNotEmpty().contains(FOLDER_ONE, FOLDER_TWO, FOLDER_THREE);
    }

    @Test
    void testShouldReturnEmptySetWhenFolderDoesNotExist() {
        final Set<String> files = underTest.findByFolder("folder-0");

        assertThat(files).isEmpty();
    }

    @Test
    void testShouldReturnASetContainingOneWhenFolderContainsOneItem() {
        final String oneItemFolder = "one-item-folder";
        final String filePath = oneItemFolder + "/" + UUID.randomUUID().toString() + ".txt";
        azureOperations.uploadToContainer(filePath);

        final Set<String> files = underTest.findByFolder(oneItemFolder);

        assertThat(files).singleElement().isEqualTo(filePath);
    }

    @Test
    void testShouldReturnSetContainingMultipleFilenamesWhenFolderContainsMultipleItems() {
        final Set<String> filePaths = generateFilePaths();
        azureOperations.uploadToContainer(filePaths);

        final Set<String> files = underTest.findByFolder(MANY_ITEMS_FOLDER);

        assertThat(files).hasSameElementsAs(filePaths);
    }

    private void populateCvpBlobstore() {
        final Set<String> filePaths = Set.of(
            FOLDER_ONE + "/" + UUID.randomUUID().toString() + ".txt",
            FOLDER_TWO + "/" + UUID.randomUUID().toString() + ".txt",
            FOLDER_THREE + "/" + UUID.randomUUID().toString() + ".txt"
        );
        azureOperations.uploadToContainer(filePaths);
    }

    private Set<String> generateFilePaths() {
        final Random random = new Random();
        final int number = random.nextInt(8) + 2;

        return IntStream.rangeClosed(1, number)
            .mapToObj(x -> MANY_ITEMS_FOLDER + "/f" + x + ".txt")
            .collect(Collectors.toUnmodifiableSet());
    }
}
