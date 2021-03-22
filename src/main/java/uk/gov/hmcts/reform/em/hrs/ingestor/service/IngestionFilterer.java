package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpItemSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.HrsFileSet;

import java.util.Set;

public interface IngestionFilterer {

    Set<CvpItem> filter(final CvpItemSet cvpItemSet, final HrsFileSet hrsFileSet);

}
