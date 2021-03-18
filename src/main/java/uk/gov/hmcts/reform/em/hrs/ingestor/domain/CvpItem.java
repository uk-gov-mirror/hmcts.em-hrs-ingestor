package uk.gov.hmcts.reform.em.hrs.ingestor.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CvpItem {
    private final String filename;
    private final String fileUri;
    private final String md5Hash;
}
