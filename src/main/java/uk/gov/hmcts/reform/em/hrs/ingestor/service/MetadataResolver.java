package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import uk.gov.hmcts.reform.em.hrs.ingestor.exception.FilenameParsingException;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.Metadata;

public interface MetadataResolver {

    MetadataResolverImpl.FileLocationAndParts extractFileLocationAndParts(String filenameWithPath);

    Metadata resolve(CvpItem item) throws FilenameParsingException;

}
