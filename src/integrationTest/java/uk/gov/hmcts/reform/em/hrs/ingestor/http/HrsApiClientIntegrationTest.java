package uk.gov.hmcts.reform.em.hrs.ingestor.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.AppConfig;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.TestOkHttpClientConfig;
import uk.gov.hmcts.reform.em.hrs.ingestor.http.mock.WireMockInitializer;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.HrsFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.Metadata;

import java.time.LocalDateTime;
import javax.inject.Inject;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {TestOkHttpClientConfig.class, AppConfig.class, HrsApiClientImpl.class})
@ContextConfiguration(initializers = {WireMockInitializer.class})
class HrsApiClientIntegrationTest {
    private static final String TEST_FILE = "file.mp4";
    private static final String TEST_FOLDER = "folder-1";
    private static final String GET_PATH = String.format("/folders/%s", TEST_FOLDER);
    private static final String POST_PATH = "/segments";
    private static final Metadata METADATA = new Metadata(
        "recording-file-name",
        "recording-cvp-uri",
        1L,
        "I2foA30B==",
        null,
        0,
        "mp4",
        LocalDateTime.now(),
        "xyz",
        222,
        "AB",
        null
    );

    @Inject
    private WireMockServer wireMockServer;

    @Inject
    private HrsApiClientImpl underTest;

    @BeforeEach
    public void prepare() {
        wireMockServer.resetAll();
    }

    @Test
    void testShouldGetDataSuccessfully() throws Exception {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(GET_PATH))
                .willReturn(aResponse()
                                .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                .withBody("[\"file.mp4\"]"))
        );

        final HrsFileSet ingestedFiles = underTest.getIngestedFiles(TEST_FOLDER);

        assertThat(ingestedFiles.getHrsFiles()).singleElement().isEqualTo(TEST_FILE);
        wireMockServer.verify(exactly(1), getRequestedFor(urlEqualTo(String.format(GET_PATH, TEST_FOLDER))));
    }

    @Test
    void testShouldGetEmptyFileSetWhenNonSuccessStatusCodeIsReceived() throws Exception {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(GET_PATH))
                .willReturn(aResponse()
                                .withStatus(403))
        );

        final HrsFileSet ingestedFiles = underTest.getIngestedFiles(TEST_FOLDER);

        assertThat(ingestedFiles.getHrsFiles()).isEmpty();
        wireMockServer.verify(exactly(1), getRequestedFor(urlEqualTo(String.format(GET_PATH, TEST_FOLDER))));
    }

    @Test
    void testShouldRaiseExceptionWhenResponseTimesOut() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(GET_PATH))
                .willReturn(aResponse()
                                .withFixedDelay(1000)
                                .withStatus(200))
        );

        assertThatIOException().isThrownBy(() -> underTest.getIngestedFiles(TEST_FOLDER));
    }

    @Test
    void testShouldRaiseExceptionWhenResponseBodyIsEmpty() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(GET_PATH))
                .willReturn(aResponse()
                                .withStatus(200)
                                .withFault(Fault.EMPTY_RESPONSE))
        );

        assertThatIOException().isThrownBy(() -> underTest.getIngestedFiles(TEST_FOLDER));
    }

    @Test
    void testShouldRaiseExceptionWhenResponseHasFaultyBody() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(GET_PATH))
                .willReturn(aResponse()
                                .withChunkedDribbleDelay(20, 1000)
                                .withStatus(200))
        );

        assertThatIOException().isThrownBy(() -> underTest.getIngestedFiles(TEST_FOLDER));
    }

    @Test
    void testShouldRaiseExceptionWhenConnectionResets() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(GET_PATH))
                .willReturn(aResponse()
                                .withFault(Fault.CONNECTION_RESET_BY_PEER))
        );

        assertThatIOException().isThrownBy(() -> underTest.getIngestedFiles(TEST_FOLDER));
    }

    @Test
    void testShouldRaiseExceptionWhenTimeoutGettingErrorBody() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(GET_PATH))
                .willReturn(aResponse()
                                .withFixedDelay(1000)
                                .withStatus(403))
        );

        assertThatIOException().isThrownBy(() -> underTest.getIngestedFiles(TEST_FOLDER));
    }

    @Test
    void testShouldPostDataSuccessfully() throws Exception {
        wireMockServer.stubFor(
            WireMock.post(urlPathEqualTo(POST_PATH))
                .willReturn(aResponse()
                                .withStatus(202))
        );

        underTest.postFile(METADATA);

        wireMockServer.verify(exactly(1), postRequestedFor(urlEqualTo(POST_PATH)));
    }

    @Test
    void testShouldWhenPostReceivesNonSuccessStatusCode() throws Exception {
        wireMockServer.stubFor(
            WireMock.post(urlPathEqualTo(POST_PATH))
                .willReturn(aResponse()
                                .withStatus(404))
        );

        underTest.postFile(METADATA);

        wireMockServer.verify(exactly(1), postRequestedFor(urlEqualTo(POST_PATH)));
    }

    @Test
    void testShouldRaiseExceptionWhenPostTimesOut() {
        wireMockServer.stubFor(
            WireMock.post(urlPathEqualTo(POST_PATH))
                .willReturn(aResponse()
                                .withFixedDelay(1000)
                                .withStatus(202))
        );

        assertThatIOException().isThrownBy(() -> underTest.postFile(METADATA));
    }

    @Test
    void testShouldRaiseExceptionWhenConnectionResetsOnPost() {
        wireMockServer.stubFor(
            WireMock.post(urlPathEqualTo(POST_PATH))
                .willReturn(aResponse()
                                .withFault(Fault.CONNECTION_RESET_BY_PEER))
        );

        assertThatIOException().isThrownBy(() -> underTest.postFile(METADATA));
    }

    @Test
    void testShouldRaiseExceptionWhenTimeoutGettingErrorBodyOnPost() {
        wireMockServer.stubFor(
            WireMock.post(urlPathEqualTo(POST_PATH))
                .willReturn(aResponse()
                                .withFixedDelay(1000)
                                .withStatus(403))
        );

        assertThatIOException().isThrownBy(() -> underTest.postFile(METADATA));
    }

}
