package uk.gov.hmcts.reform.em.hrs.ingestor.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.AppConfig;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.TestOkHttpClientConfig;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.HrsFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.HrsApiException;
import uk.gov.hmcts.reform.em.hrs.ingestor.http.mock.WireMockInitializer;

import javax.inject.Inject;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {TestOkHttpClientConfig.class, AppConfig.class, HrsApiClientImpl.class})
@ContextConfiguration(initializers = {WireMockInitializer.class})
class HrsApiClientImplTest {
    private static final String TEST_FILE = "file.mp4";
    private static final String TEST_FOLDER = "folder-1";
    private static final String PATH = String.format("/folders/%s/hearing-recording-file-names", TEST_FOLDER);

    @Inject
    private WireMockServer wireMockServer;

    @Inject
    private HrsApiClientImpl underTest;

    @AfterEach
    public void afterEach() {
        this.wireMockServer.resetAll();
    }

    @Test
    void testShouldGetDataSuccessfully() throws Exception {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                                .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                .withBody("[\"file.mp4\"]"))
        );

        final HrsFileSet ingestedFiles = underTest.getIngestedFiles(TEST_FOLDER);

        assertThat(ingestedFiles.getHrsFiles()).singleElement().isEqualTo(TEST_FILE);
    }

    @Test
    void testShouldGetErrorCodeWithMessage() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                                .withStatus(403))
        );

        final Throwable thrown = catchThrowable(() -> underTest.getIngestedFiles(TEST_FOLDER));

        assertThat(thrown)
            .isInstanceOf(HrsApiException.class)
            .hasMessageStartingWith("Response error: 403 => ");
    }

    @Test
    void testShouldRaiseExceptionWhenResponseTimesOut() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                                .withFixedDelay(1000)
                                .withStatus(200))
        );

        assertThatIOException().isThrownBy(() -> underTest.getIngestedFiles(TEST_FOLDER));
    }

    @Test
    void testShouldRaiseExceptionWhenResponseBodyIsEmpty() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                                .withStatus(200)
                                .withFault(Fault.EMPTY_RESPONSE))
        );

        assertThatIOException().isThrownBy(() -> underTest.getIngestedFiles(TEST_FOLDER));
    }

    @Test
    void testShouldRaiseExceptionWhenResponseHasFaultyBody() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                                .withChunkedDribbleDelay(20, 1000)
                                .withStatus(200))
        );

        assertThatIOException().isThrownBy(() -> underTest.getIngestedFiles(TEST_FOLDER));
    }

    @Test
    void testShouldRaiseExceptionWhenConnectionResets() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                                .withStatus(200)
                                .withFault(Fault.CONNECTION_RESET_BY_PEER))
        );

        assertThatIOException().isThrownBy(() -> underTest.getIngestedFiles(TEST_FOLDER));
    }

    @Test
    void testShouldRaiseExceptionWhenTimeoutGettingErrorBody() {
        wireMockServer.stubFor(
            WireMock.get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                                .withFixedDelay(1000)
                                .withStatus(403))
        );

        assertThatIOException().isThrownBy(() -> underTest.getIngestedFiles(TEST_FOLDER));
    }
}
