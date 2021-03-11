package uk.gov.hmcts.reform.em.hrs.ingestor.http;

import uk.gov.hmcts.reform.em.hrs.ingestor.exception.HrsApiException;

import java.io.IOException;
import java.util.Set;

public interface HrsApiClient {
    Set<String> getIngestedFiles(String folderName) throws HrsApiException, IOException;
}
