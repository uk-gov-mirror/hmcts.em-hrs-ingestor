package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItemSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.HrsFileSet;

import java.util.Set;

public interface IngestionFilterer {

    Set<CvpItem> filter(final CvpItemSet cvpItemSet, final HrsFileSet hrsFileSet);

}
