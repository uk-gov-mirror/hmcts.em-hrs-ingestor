package uk.gov.hmcts.reform.em.hrs.ingestor.http;

import uk.gov.hmcts.reform.em.hrs.ingestor.domain.HrsFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.Metadata;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.HrsApiException;

import java.io.IOException;

public interface HrsApiClient {
    HrsFileSet getIngestedFiles(String folderName) throws HrsApiException, IOException;

    void postFile(String folder, Metadata metadata);
}
