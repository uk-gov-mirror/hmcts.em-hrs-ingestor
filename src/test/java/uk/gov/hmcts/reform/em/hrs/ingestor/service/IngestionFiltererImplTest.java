package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.HrsFileSet;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IngestionFiltererImplTest {

    private final IngestionFilterer underTest = new IngestionFiltererImpl();

    private static final CvpFileSet CVP_FILE_SET = new CvpFileSet(Set.of("f1.mp4", "f2.mp4", "f3.mp4"));

    @Test
    void testShouldReturnCvpFileSetWhenNoHrsFiles() {
        final HrsFileSet hrsFileSet = new HrsFileSet(Collections.emptySet());

        final Set<String> filtered = underTest.filter(CVP_FILE_SET, hrsFileSet);

        assertThat(filtered).hasSameElementsAs(CVP_FILE_SET.getCvpFiles());
    }

    @Test
    void testShouldReturnCvpFileSetWithoutTheElementsInHrsFileSet() {
        final HrsFileSet hrsFileSet = new HrsFileSet(Set.of("f1.mp4", "f2.mp4"));

        final Set<String> filtered = underTest.filter(CVP_FILE_SET, hrsFileSet);

        assertThat(filtered).singleElement().isEqualTo("f3.mp4");
    }

    @Test
    void testShouldReturnCvpFileSetWhenNoElementsInHrsFilesFoundInCvpFiles() {
        final HrsFileSet hrsFileSet = new HrsFileSet(Set.of("f1-0.mp4"));

        final Set<String> filtered = underTest.filter(CVP_FILE_SET, hrsFileSet);

        assertThat(filtered).hasSameElementsAs(CVP_FILE_SET.getCvpFiles());
    }

    @Test
    void testShouldReturnEmptySetWhenHrsHasEveryFileInCvp() {
        final HrsFileSet hrsFileSet = new HrsFileSet(Set.of("f1.mp4", "f2.mp4", "f3.mp4"));

        final Set<String> filtered = underTest.filter(CVP_FILE_SET, hrsFileSet);

        assertThat(filtered).isEmpty();
    }

}
