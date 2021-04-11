package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import org.junit.jupiter.api.Test;
import reactor.util.function.Tuple3;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.FilenameParsingException;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.Metadata;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.em.hrs.ingestor.service.MetadataResolverImpl.FRAGMENT;

class MetadataResolverImplTest {
    private static final String FILENAME = "audiostream12/bp-0266-hu-02785-2020_2020-07-16-10.07.31.680-UTC_0.mp4";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSS");
    private static final CvpItem CVP_ITEM = new CvpItem(
        FILENAME,
        "file-uri",
        "a2B4==",
        123L
    );
    private final MetadataResolver underTest = new MetadataResolverImpl();

    @Test
    void testShouldResolveMetadataFromFilename() throws FilenameParsingException {
        final Metadata metadata = underTest.resolve(CVP_ITEM);

        assertThat(metadata).satisfies(x -> {
            assertThat(x.getRecordingDateTime()).isEqualTo(LocalDateTime.parse("2020-07-16-10.07.31.680", FORMATTER));
            assertThat(x.getSegment()).isZero();
        });
    }

    @Test
    void testShouldReturnRoomReference() {
        final Tuple3<Integer, String, String> fragments = FRAGMENT.apply(FILENAME);

        assertThat(fragments.getT1()).isEqualTo(12);
    }

    @Test
    void testShouldReturnRecordingReference() {
        final String expectedString = "bp-0266-hu-02785-2020_2020-07-16-10.07.31.680-UTC_0";

        final Tuple3<Integer, String, String> fragments = FRAGMENT.apply(FILENAME);

        assertThat(fragments.getT2()).isEqualTo(expectedString);
    }

    @Test
    void testShouldReturnFilenameExtension() {
        final String expectedString = "mp4";

        final Tuple3<Integer, String, String> fragments = FRAGMENT.apply(FILENAME);

        assertThat(fragments.getT3()).isEqualTo(expectedString);
    }
}
