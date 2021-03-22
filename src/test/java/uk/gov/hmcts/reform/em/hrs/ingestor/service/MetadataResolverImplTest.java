package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.Metadata;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.FileParsingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class MetadataResolverImplTest {
    private final MetadataResolver underTest = new MetadataResolverImpl();

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSS");
    private static final CvpItem CVP_ITEM = new CvpItem(
        "folder-1/bp-0266-hu-02785-2020_2020-07-16-10.07.31.680-UTC_0",
        "file-uri",
        "a2B4=="
    );

    @Test
    void testShouldResolveMetadataFromFilename() throws FileParsingException {
        final Metadata metadata = underTest.resolve(CVP_ITEM);

        assertThat(metadata).satisfies(x -> {
            assertThat(x.getRecordingDate()).isEqualTo(LocalDateTime.parse("2020-07-16-10.07.31.680", FORMATTER));
            assertThat(x.getRecordingSegment()).isEqualTo(0);
        });
    }

    @Test
    void testShouldResolveMetadataFromFilenameWhenFilenameIsEmpty() {
        assertThatExceptionOfType(FileParsingException.class).isThrownBy(() -> {
            underTest.resolve(
                new CvpItem("", null, null)
            );

        });
    }

    @Test
    void testShouldResolveMetadataFromFilenameWhenFilenameIsNull() {
        assertThatExceptionOfType(FileParsingException.class).isThrownBy(() -> {
            underTest.resolve(
                new CvpItem(null, null, null)
            );

        });
    }
}
