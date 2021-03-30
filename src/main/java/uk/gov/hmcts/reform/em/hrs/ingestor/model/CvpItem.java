package uk.gov.hmcts.reform.em.hrs.ingestor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
@AllArgsConstructor
public class CvpItem {
    @Nonnull
    private final String filename;
    private final String fileUri;
    private final String md5Hash;
    private final long contentLength;
}
