package uk.gov.hmcts.reform.em.hrs.ingestor.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.TestAzureStorageConfiguration;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.helper.AzureOperations;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = {
    TestAzureStorageConfiguration.class,
    CvpBlobstoreClientImpl.class,
    AzureOperations.class
})
class CvpBlobstoreClientImplTest {
    @Inject
    private AzureOperations azureOperations;
    @Inject
    private CvpBlobstoreClientImpl underTest;

    private static final String FOLDER_ONE = "folder-1";
    private static final String FOLDER_TWO = "folder-2";
    private static final String FOLDER_THREE = "folder-3";
    private static final String EMPTY_FOLDER = "folder-0";
    private static final String ONE_ITEM_FOLDER = "one-item-folder";
    private static final String MANY_ITEMS_FOLDER = "many-items-folder";
    private static final String TEST_DATA = "Hello World!";

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
        final CvpFileSet cvpFileSet = underTest.findByFolder(EMPTY_FOLDER);

        assertThat(cvpFileSet.getCvpFiles()).isEmpty();
    }

    @Test
    void testShouldReturnASetContainingOneWhenFolderContainsOneItem() {
        final String filePath = ONE_ITEM_FOLDER + "/" + UUID.randomUUID().toString() + ".txt";
        azureOperations.uploadToContainer(filePath);

        final CvpFileSet cvpFileSet = underTest.findByFolder(ONE_ITEM_FOLDER);

        assertThat(cvpFileSet.getCvpFiles()).singleElement().isEqualTo(filePath);
    }

    @Test
    void testShouldReturnSetContainingMultipleFilenamesWhenFolderContainsMultipleItems() {
        final Set<String> filePaths = generateFilePaths();
        azureOperations.uploadToContainer(filePaths);

        final CvpFileSet cvpFileSet = underTest.findByFolder(MANY_ITEMS_FOLDER);

        assertThat(cvpFileSet.getCvpFiles()).hasSameElementsAs(filePaths);
    }

    @Test
    void testShouldDownloadFile() throws Exception {
        final String filePath = ONE_ITEM_FOLDER + "/" + UUID.randomUUID().toString() + ".txt";
        azureOperations.uploadToContainer(filePath, TEST_DATA);

        try (final PipedInputStream pipedInput = new PipedInputStream();
             final PipedOutputStream output = new PipedOutputStream(pipedInput)) {

            underTest.downloadFile(filePath, output);

            assertThat(pipedInput).satisfies(this::assertStreamContent);
        }
    }

    void assertStreamContent(final InputStream input) {
        final StringBuilder sb = new StringBuilder();
        try {
            await().atMost(Duration.ofSeconds(10)).until(() -> {
                while (true) {
                    sb.append((char) input.read());
                    final String s = sb.toString();
                    if (s.contains(TEST_DATA)) {
                        break;
                    }
                }
                return true;
            });
        } finally {
            assertThat(sb.toString()).isEqualTo(TEST_DATA);
        }
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
