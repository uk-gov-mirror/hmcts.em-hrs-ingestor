package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.util.function.Tuple4;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.FilenameParsingException;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.Metadata;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static uk.gov.hmcts.reform.em.hrs.ingestor.service.MetadataResolverImpl.FRAGMENT;

class MetadataResolverImplTest {
    private static final String FILENAME_VALID =
        "audiostream12/bp-0266-hu-02785-2020_2020-07-16-10.07.31.680-UTC_0.mp4";
    private static final String FILENAME_NO_SEGMENT =
        "audiostream12/bp-0266-hu-02785-2020_2020-07-16-10.07.31.680-UTC.mp4";
    private static final String FILENAME_INVALID_SEGMENT =
        "audiostream12/bp-0266-hu-02785-2020_2020-07-16-10.07.31.680-UTC_X.mp4";
    private static final String FILENAME_NO_FOLDER = "bp-0266-hu-02785-2020_2020-07-16-10.07.31.680-UTC_X.mp4";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSS");
    private static final CvpItem CVP_ITEM = createCvpItem(FILENAME_VALID);
    private static final CvpItem CVP_ITEM_NO_SEGMENT = createCvpItem(FILENAME_NO_SEGMENT);
    private static final CvpItem CVP_ITEM_INVALID_SEGMENT = createCvpItem(FILENAME_INVALID_SEGMENT);
    private static final CvpItem CVP_ITEM_NO_FOLDER = createCvpItem(FILENAME_NO_FOLDER);

    @NotNull
    private static CvpItem createCvpItem(String fileName) {
        return new CvpItem(
            fileName,
            "file-uri",
            "a2B4==",
            123L
        );
    }


    private final MetadataResolver underTest = new MetadataResolverImpl();


    @Test
    void testFilenameWithoutFolderInPathThrowsFileParsingException() {
        assertThatExceptionOfType(FilenameParsingException.class)
            .isThrownBy(() -> underTest.resolve(CVP_ITEM_NO_FOLDER));
    }

    @Test
    void testFilenameWithoutSegmentThrowsFileParsingException() {
        assertThatExceptionOfType(FilenameParsingException.class)
            .isThrownBy(() -> underTest.resolve(CVP_ITEM_NO_SEGMENT));
    }

    @Test
    void testFilenameWithInvalidSegmentThrowsFileParsingException() {
        assertThatExceptionOfType(FilenameParsingException.class)
            .isThrownBy(() -> underTest.resolve(CVP_ITEM_INVALID_SEGMENT));
    }

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
        final Tuple4<String, Integer, String, String> fragments = FRAGMENT.apply(FILENAME_VALID);

        assertThat(fragments.getT2()).isEqualTo(12);
    }

    @Test
    void testShouldReturnFolder() {
        final Tuple4<String, Integer, String, String> fragments = FRAGMENT.apply(FILENAME_VALID);

        assertThat(fragments.getT1()).isEqualTo("audiostream12");
    }

    @Test
    void testShouldReturnRecordingReference() {
        final String expectedString = "bp-0266-hu-02785-2020_2020-07-16-10.07.31.680-UTC_0";

        final Tuple4<String, Integer, String, String> fragments = FRAGMENT.apply(FILENAME_VALID);

        assertThat(fragments.getT3()).isEqualTo(expectedString);
    }

    @Test
    void testShouldReturnFilenameExtension() {
        final String expectedString = "mp4";

        final Tuple4<String, Integer, String, String> fragments = FRAGMENT.apply(FILENAME_VALID);

        assertThat(fragments.getT4()).isEqualTo(expectedString);
    }
}
