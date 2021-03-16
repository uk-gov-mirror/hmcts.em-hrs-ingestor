package uk.gov.hmcts.reform.em.hrs.ingestor.av;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.mock.ClamAvInitializer;
import uk.gov.hmcts.reform.em.hrs.ingestor.config.ClamAvConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ClamAvConfig.class, AntivirusClientImpl.class})
@ContextConfiguration(initializers = {ClamAvInitializer.class})
class AntivirusClientImplTest {
    private static final String VIRUS_FILE_WITH_SIGNATURE = "eicar-standard-av-test-file";
    private static final String CLEAN_FILE = "clean-file";

    @Inject
    private AntivirusClientImpl underTest;

    @Test
    void testShouldDetectVirusInFile() throws Exception {
        final InputStream input = getFileInputStream(VIRUS_FILE_WITH_SIGNATURE);

        final AvScanResult result = underTest.scan(input);
        input.close();

        assertThat(result).isEqualTo(AvScanResult.INFECTED);
    }

    @Test
    void testShouldReportFileIsClean() throws Exception {
        final InputStream input = getFileInputStream(CLEAN_FILE);

        final AvScanResult result = underTest.scan(input);
        input.close();

        assertThat(result).isEqualTo(AvScanResult.CLEAN);
    }

    private InputStream getFileInputStream(final String filename) throws URISyntaxException, FileNotFoundException {
        final URL resource = getClass().getClassLoader().getResource(filename);
        final File file = Paths.get(Objects.requireNonNull(resource).toURI()).toFile();
        return new FileInputStream(file);
    }

}
