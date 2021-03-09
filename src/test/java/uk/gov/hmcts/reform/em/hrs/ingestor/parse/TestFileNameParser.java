package uk.gov.hmcts.reform.em.hrs.ingestor.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

@Slf4j
public class TestFileNameParser {

    @ParameterizedTest(name = "Invalid parameter test : {0} --> {1}")
    @CsvSource(value = {"Empty Value,''", "Spaced Value,' '", "Value Value,NIL"}, nullValues = "NIL")
    public void test_negative_invalid_file_name_input(final String inputKey, final String inputValue) {
        try {
            FileNameParser.parseFileName(inputValue);
        } catch (IllegalArgumentException illegalArgumentException) {
            assertEquals("The argument passed is not valid",
                    illegalArgumentException.getLocalizedMessage());
        }

    }

    @ParameterizedTest(name = "Wrong Format File Names : {0} --> {1}")
    @CsvFileSource(resources = "/uk/gov/hmcts/reform/em/hrs/hrs/ingestor/data/invalid-file-format-values.csv", numLinesToSkip = 1)
    public void test_negative_wrong_format_file_name_input(final String inputKey, final String inputValue) {
        Map<String, Object> responseMap = FileNameParser.parseFileName(inputValue);
        assertEquals(1, responseMap.size());
        assertEquals(Arrays.stream(inputValue.split("_")).findFirst().get(),
                responseMap.get("CaseID").toString().trim());
    }

    @ParameterizedTest(name = "Positive tests for Civil and Family")
    @CsvSource(value = {
            "Valid File Name All Capital Case,CV-0150-GN00NT095_2020-07-29-09.03.40.236-UTC_0",
            "Valid File Case Reference Small Case,CV-0291-g20yj687_2020-09-01-09.14.47.314-UTC_0"},
            nullValues = "NIL")
    public void test_positive_location_code_based_input_for_civil_and_family(final String inputKey, final String inputValue) {

        Map<String, Object> responseMap = FileNameParser.parseFileName(inputValue);
        switch (inputKey) {
            case "Valid File Name All Capital Case":
                verifyValuesOfMappedResponse(responseMap, "CV", "150",
                        "GN00NT095", "0", 2020, 7, 29,
                        9, 3, 40, 236000000);
                break;
            case "Valid File Case Reference Small Case":
                verifyValuesOfMappedResponse(responseMap, "CV", "291",
                        "g20yj687", "0", 2020, 9, 1,
                        9, 14, 47, 314000000);
                break;
            default:
                throw new IllegalArgumentException("Invalid key request for the Tests ");
        }
    }


    @Test
    public void test_positive_case_reference_based_input_for_tribunals() {

        Map<String, Object> responseMap = FileNameParser.parseFileName("IA-HU-01234-2018_2020-09-19-10.50.20.150-UTC_0");
        verifyValuesOfMappedResponse(responseMap, "IA", null, "HU-01234-2018",
                "0", 2020, 9, 19, 10,
                50, 20, 150000000);
    }


    @Test
    public void test_positive_location_code_based_input_for_royal_courts_of_justice() {

        Map<String, Object> responseMap = FileNameParser.parseFileName("QB-0164-CO-2020-01425_2020-10-23-13.02.08.818-UTC_0");
        verifyValuesOfMappedResponse(responseMap, "QB", "164",
                "CO-2020-01425", "0", 2020, 10, 23,
                13, 2, 8, 818000000);
    }


    @Test
    public void test_positive_non_location_code_based_input_for_royal_courts_of_justice() {

        Map<String, Object> responseMap = FileNameParser.parseFileName("QB-CO-2020-01430_2020-09-12-15.00.12.765-UTC_0");
        verifyValuesOfMappedResponse(responseMap, "QB", null, "CO-2020-01430",
                "0", 2020, 9, 12, 15,
                0, 12, 765000000);
    }

    private void verifyValuesOfMappedResponse(final Map<String, Object> responseMap,
                                              final String jurisdictionCode,
                                              final String locationCode,
                                              final String caseReference,
                                              final String segment,
                                              final int year, final int month,
                                              final int day, final int hour,
                                              final int minute, final int second, final int nano) {
        assertTrue(responseMap.size() == 5 || responseMap.size() == 4);
        assertEquals(jurisdictionCode, responseMap.get("Jurisdiction").toString().trim());
        if (responseMap.size() == 5 && responseMap.get("LocationCode") != null) {
            assertEquals(locationCode, responseMap.get("LocationCode").toString().trim());
        }
        assertEquals(caseReference, responseMap.get("CaseID").toString().trim());
        assertEquals(segment, responseMap.get("Segment").toString().trim());

        LocalDateTime localDateTime = (LocalDateTime) responseMap.get("RecordingDateTime");
        System.out.println("The value of the Returned Date Time" + localDateTime);
        assertEquals(year, localDateTime.getYear());
        assertEquals(month, localDateTime.getMonth().getValue());
        assertEquals(day, localDateTime.getDayOfMonth());
        assertEquals(hour, localDateTime.getHour());
        assertEquals(minute, localDateTime.getMinute());
        assertEquals(second, localDateTime.getSecond());
        assertEquals(nano, localDateTime.getNano());
    }

    //@Setter(AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    @AllArgsConstructor
    @Accessors(fluent = true)
    @ToString
    @EqualsAndHashCode
    public class ParseDTO {
        String jurisdiction;
        String locationCode;
        String caseID;
        String segment;
        int year;
        int month;
        int hour;
        int minute;
        int second;
        int nano;

    }
}
