package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.Metadata;

public interface MetadataResolver {

    Metadata resolve(CvpItem item);

}
