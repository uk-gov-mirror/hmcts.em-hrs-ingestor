package uk.gov.hmcts.reform.em.hrs.ingestor.http;

import uk.gov.hmcts.reform.em.hrs.ingestor.exception.HrsApiException;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.HrsFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.Metadata;

import java.io.IOException;

public interface HrsApiClient {
    HrsFileSet getIngestedFiles(String folderName) throws HrsApiException, IOException;

    void postFile(Metadata metadata) throws IOException, HrsApiException;
}
