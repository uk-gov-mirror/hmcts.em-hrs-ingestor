package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.AppConfig;
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
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.em.hrs.ingestor.helper.TestUtil.TEST_FILE;
import static uk.gov.hmcts.reform.em.hrs.ingestor.helper.TestUtil.TEST_FOLDER;

@SpringBootTest(classes = {
    TestOkHttpClientConfig.class,
    TestAzureStorageConfiguration.class,
    AppConfig.class,
    CvpBlobstoreClientImpl.class,
    HrsApiClientImpl.class,
    IngestionFiltererImpl.class,
    MetadataResolverImpl.class,
    DefaultIngestorService.class,
    AzureOperations.class
})
@ContextConfiguration(initializers = {WireMockInitializer.class})
class IngestorServiceIntegrationTest {
    private static final String GET_FOLDERS_PATH = "/folders/([a-zA-Z0-9_.-]*)";
    private static final String POST_PATH = "/segments";
    private static final String DUMMY_FOLDER = "dummy-folder";
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
    void testShouldIngestFiles() throws Exception {
        setupCvpBlobstore();

        underTest.ingest();

        wireMockServer.verify(
            exactly(1),
            postRequestedFor(urlEqualTo(POST_PATH))
        );
    }

    private void setupCvpBlobstore() throws Exception {
        final byte[] data = TestUtil.getFileContent(TEST_FILE);
        azureOperations.uploadToContainer(TEST_FOLDER + "/" + TEST_FILE, data);
    }

}
