package uk.gov.hmcts.reform.em.hrs.ingestor.dto;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CourtDTO {


    @CsvBindByPosition(position = 0)
    public String courtName;

    @CsvBindByPosition(position = 1)
    public String courtCode;

    @CsvBindByPosition(position = 2)
    public String courtID;
}
