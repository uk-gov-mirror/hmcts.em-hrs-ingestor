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
    private final String caseRef;  // CaseID
    private final LocalDateTime recordingDate; // RecordingDateTime
    private final String jurisdictionCode; // Jurisdiction
    private final String courtLocationCode; // LocationCode
    private String recordingReference;  // missing
    private final int recordingSegment;  // Segment
}
