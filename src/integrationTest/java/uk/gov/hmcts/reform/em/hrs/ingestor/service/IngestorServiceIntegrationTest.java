package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.AntivirusClientImpl;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.mock.ClamAvInitializer;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.AppConfig;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.ClamAvConfig;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.TestAzureStorageConfiguration;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.TestOkHttpClientConfig;
import uk.gov.hmcts.reform.em.hrs.ingestor.helper.AzureOperations;
import uk.gov.hmcts.reform.em.hrs.ingestor.helper.TestUtil;
import uk.gov.hmcts.reform.em.hrs.ingestor.http.HrsApiClientImpl;
import uk.gov.hmcts.reform.em.hrs.ingestor.http.mock.WireMockInitializer;
import uk.gov.hmcts.reform.em.hrs.ingestor.storage.CvpBlobstoreClientImpl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.lessThanOrExactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.em.hrs.ingestor.helper.TestUtil.CLEAN_FILE;
import static uk.gov.hmcts.reform.em.hrs.ingestor.helper.TestUtil.CLEAN_FOLDER;
import static uk.gov.hmcts.reform.em.hrs.ingestor.helper.TestUtil.INFECTED_FILE;
import static uk.gov.hmcts.reform.em.hrs.ingestor.helper.TestUtil.INFECTED_FOLDER;

@SpringBootTest(classes = {
    TestOkHttpClientConfig.class,
    TestAzureStorageConfiguration.class,
    ClamAvConfig.class,
    AppConfig.class,
    CvpBlobstoreClientImpl.class,
    HrsApiClientImpl.class,
    AntivirusClientImpl.class,
    IngestionFiltererImpl.class,
    MetadataResolverImpl.class,
    DefaultIngestorService.class,
    AzureOperations.class
})
@ContextConfiguration(initializers = {WireMockInitializer.class, ClamAvInitializer.class})
class IngestorServiceIntegrationTest {
    private static final String GET_FOLDERS_PATH = "/folders/([a-zA-Z0-9_.-]*)";
    private static final String POST_PATH = "/segments";
    private static String DUMMY_FOLDER = "dummy-folder";
    @Autowired
    private WireMockServer wireMockServer;
    @Autowired
    private AzureOperations azureOperations;
    @Autowired
    private DefaultIngestorService underTest;

    @BeforeEach
    public void prepare() {
        azureOperations.clearContainer();
        wireMockServer.resetAll();
        wireMockServer.stubFor(
            get(urlMatching(GET_FOLDERS_PATH))
                .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                .withBody("{\"folder-name\":\"" + DUMMY_FOLDER + "\",\"filenames\":[]}"))
        );

        wireMockServer.stubFor(
            post(urlMatching(POST_PATH))
                .willReturn(aResponse()
                                .withStatus(202))
        );
    }

    @Test
    void testShouldIngestCleanFiles() throws Exception {
        setupCvpBlobstore(CLEAN_FOLDER, CLEAN_FILE);

        underTest.ingest();

        wireMockServer.verify(
            exactly(1),
            postRequestedFor(urlEqualTo(POST_PATH))
        );
    }

    @Test
    void testShouldNotIngestInfectedFiles() throws Exception {
        setupCvpBlobstore(INFECTED_FOLDER, INFECTED_FILE);

        underTest.ingest();

        // As we're unable to delete, the clean file from the other test may be in the blobstore
        wireMockServer.verify(
            lessThanOrExactly(1),
            postRequestedFor(urlEqualTo(POST_PATH))
        );
    }

    private void setupCvpBlobstore(final String folder, final String file) throws Exception {
        final byte[] data = TestUtil.getFileContent(file);
        azureOperations.uploadToContainer(folder + "/" + file, data);
    }

}
