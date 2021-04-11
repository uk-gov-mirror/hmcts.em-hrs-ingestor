package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItemSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.HrsFileSet;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IngestionFiltererImplTest {

    private static final CvpItem ITEM_1 = new CvpItem("f1.mp4", "uri1", "hash1", 1L);
    private static final Set<CvpItem> CVP_ITEMS = Set.of(
        ITEM_1,
        new CvpItem("f2.mp4", "uri2", "hash2", 1L),
        new CvpItem("f3.mp4", "uri3", "hash3", 1L)
    );
    private static final CvpItemSet CVP_FILE_SET = new CvpItemSet(CVP_ITEMS);
    private final IngestionFilterer underTest = new IngestionFiltererImpl();

    @Test
    void testShouldReturnCvpFileSetWhenNoHrsFiles() {
        final HrsFileSet hrsFileSet = new HrsFileSet(Collections.emptySet());

        final Set<CvpItem> filtered = underTest.filter(CVP_FILE_SET, hrsFileSet);

        assertThat(filtered).hasSameElementsAs(CVP_ITEMS);
    }

    @Test
    void testShouldReturnCvpFileSetWithoutTheElementsInHrsFileSet() {
        final HrsFileSet hrsFileSet = new HrsFileSet(Set.of("f1.mp4", "f2.mp4"));

        final Set<CvpItem> filtered = underTest.filter(CVP_FILE_SET, hrsFileSet);

        assertThat(filtered).singleElement()
            .satisfies(x -> assertThat(x.getFilename()).isEqualTo("f3.mp4"));
    }

    @Test
    void testShouldReturnCvpFileSetWhenNoElementsInHrsFilesFoundInCvpFiles() {
        final HrsFileSet hrsFileSet = new HrsFileSet(Set.of("f1-0.mp4"));

        final Set<CvpItem> filtered = underTest.filter(CVP_FILE_SET, hrsFileSet);

        assertThat(filtered).hasSameElementsAs(CVP_ITEMS);
    }

    @Test
    void testShouldReturnEmptySetWhenHrsHasEveryFileInCvp() {
        final HrsFileSet hrsFileSet = new HrsFileSet(Set.of("f1.mp4", "f2.mp4", "f3.mp4"));

        final Set<CvpItem> filtered = underTest.filter(CVP_FILE_SET, hrsFileSet);

        assertThat(filtered).isEmpty();
    }

}
