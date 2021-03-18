package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.Metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class MetadataResolverImplTest {
    private final MetadataResolver underTest = new MetadataResolverImpl();

    @Test
    void testShouldResolveMetadataFromFilename() {
        final Metadata metadata = underTest.resolve(
            new CvpItem(
            "bp-0266-hu-02785-2020_2020-07-16-10.07.31.680-UTC_0",
            "file-uri",
            "a2B4==")
        );

        assertThat(metadata).isInstanceOf(Metadata.class);
    }

    @Test
    void testShouldResolveMetadataFromFilenameWhenFilenameIsEmpty() {
        assertThatIllegalArgumentException().isThrownBy(() -> underTest.resolve(
            new CvpItem("", null, null)
        ));
    }

    @Test
    void testShouldResolveMetadataFromFilenameWhenFilenameIsNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> underTest.resolve(
            new CvpItem(null, null, null)
        ));
    }
}
