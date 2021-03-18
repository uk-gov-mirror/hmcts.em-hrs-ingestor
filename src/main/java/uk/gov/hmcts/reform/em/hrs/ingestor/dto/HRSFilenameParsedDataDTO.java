package uk.gov.hmcts.reform.em.hrs.ingestor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class HRSFilenameParsedDataDTO {

    public String jurisdiction;

    public String locationCode;

    public String caseID;

    public LocalDateTime recordingDateTime;

    public String segment;

    public String recordingUniquIdentifier;

}
