package uk.gov.hmcts.reform.em.hrs.ingestor.av;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.mock.ClamAvInitializer;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.ClamAvConfig;
import uk.gov.hmcts.reform.em.hrs.ingestor.helper.TestUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.em.hrs.ingestor.helper.TestUtil.CLEAN_FILE;
import static uk.gov.hmcts.reform.em.hrs.ingestor.helper.TestUtil.INFECTED_FILE;

@SpringBootTest(classes = {ClamAvConfig.class, AntivirusClientImpl.class})
@ContextConfiguration(initializers = {ClamAvInitializer.class})
class AntivirusClientIntegrationTest {

    @Autowired
    private AntivirusClientImpl underTest;

    @Test
    void testShouldDetectVirusInFile() throws Exception {
        final InputStream input = getFileAsInputStream(INFECTED_FILE);

        final AvScanResult result = underTest.scan(input);
        input.close();

        assertThat(result).isEqualTo(AvScanResult.INFECTED);
    }

    @Test
    void testShouldReportFileIsClean() throws Exception {
        final InputStream input = getFileAsInputStream(CLEAN_FILE);

        final AvScanResult result = underTest.scan(input);
        input.close();

        assertThat(result).isEqualTo(AvScanResult.CLEAN);
    }

    private InputStream getFileAsInputStream(final String filename) throws IOException, URISyntaxException {
        final byte[] data = TestUtil.getFileContent(filename);
        return new ByteArrayInputStream(data);
    }

}
