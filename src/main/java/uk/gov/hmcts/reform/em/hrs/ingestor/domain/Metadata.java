package uk.gov.hmcts.reform.em.hrs.ingestor.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Metadata {
    private final String recordingFileUri;
    private final String checkSum;
    private final String hearingSource = "CVP";
    private String hearingLocation;  // folderName (possibly parsed)
    private final String caseRef;
    private final LocalDateTime recordingDateTime;
    private final String jurisdictionCode;
    private final String courtLocationCode;
    private final String recordingReference;
    private final int recordingSegment;
}
