package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.HrsFileSet;

import java.util.Set;

public interface IngestionFilterer {

    Set<String> filter(final CvpFileSet cvpFileSet, final HrsFileSet hrsFileSet);

}
