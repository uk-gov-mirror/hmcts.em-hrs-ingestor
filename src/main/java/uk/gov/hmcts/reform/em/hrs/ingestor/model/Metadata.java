package uk.gov.hmcts.reform.em.hrs.ingestor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Metadata {
    private final String filename;
    private final String cvpFileUrl;
    private final long fileSize;
    private final String checkSum;
    private final String recordingRef;
    private final int segment;
    private final String filenameExtension;
    @JsonFormat(pattern = "yyyy-MM-dd-HH.mm.ss.SSS")
    private final LocalDateTime recordingDateTime;
    private final String caseRef;
    private final String recordingSource = "CVP";
    private final int hearingRoomRef;
    private final String jurisdictionCode;
    private final String courtLocationCode;
}
