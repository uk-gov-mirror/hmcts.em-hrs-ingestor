package uk.gov.hmcts.reform.em.hrs.ingestor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class ParsedFilenameDto {

    public final String jurisdiction;

    public final String locationCode;

    public final String caseID;

    public final LocalDateTime recordingDateTime;

    public final String segment;

    public final String uniqueIdentifier;

}
