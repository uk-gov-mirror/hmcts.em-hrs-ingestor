package uk.gov.hmcts.reform.em.hrs.ingestor.parse;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class TestFileNameParserWithFullDataExtract {


    @ParameterizedTest(name = "Self Evaluation File : {0} --> {1}")
    @CsvFileSource(resources = "/uk/gov/hmcts/reform/em/hrs/hrs/ingestor/data/All_report_size_2020-11-06T16_32_41+0000- Analysis_With_Filename.csv",
        numLinesToSkip = 1)
    public void test_input_file_input(final String fileName,
                                      final String jurisdiction,
                                      final String jurisdictionMatch,
                                      final String locationCode,
                                      final String locationMatch,
                                      final String caseID) throws Exception {
        String inputValue = fileName.substring(0, fileName.lastIndexOf('.'));
        Map<String, Object> responseMap = FileNameParser.parseFileName(inputValue);
        verifyValuesOfMappedResponse(responseMap, jurisdiction, jurisdictionMatch, locationCode, locationMatch, caseID);
    }


    private void verifyValuesOfMappedResponse(final Map<String, Object> responseMap,
                                              final String jurisdictionCode,
                                              final String jurisdictionMatch,
                                              final String locationCode,
                                              final String locationMatch,
                                              final String caseReference) {

        log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        log.debug("Input Jurisdiction Code " + jurisdictionCode);
        log.debug("Input Location Code " + locationCode);
        log.debug("Input Case Reference " + caseReference);

        if ((Objects.nonNull(jurisdictionMatch) && !jurisdictionMatch.isEmpty() && !jurisdictionMatch.isBlank() &&
            jurisdictionMatch.equalsIgnoreCase("Yes"))
            && (Objects.nonNull(locationMatch) && !locationMatch.isEmpty() && !locationMatch.isBlank() &&
            locationMatch.equalsIgnoreCase("Yes"))) {

            log.debug("The Complete Scenario");
            log.debug("The value of the Mapped Jurisdiction Code" + responseMap.get("Jurisdiction").toString());
            log.debug("The value of the Mapped Location Code" + responseMap.get("LocationCode").toString());
            log.debug("The value of the Mapped Case ID" + responseMap.get("CaseID").toString());

            assertEquals(5, responseMap.size());
            assertTrue(jurisdictionCode.equalsIgnoreCase(responseMap.get("Jurisdiction").toString().trim()));
            assertTrue(locationCode.equalsIgnoreCase(responseMap.get("LocationCode").toString().trim()));

            assertTrue((caseReference == null ? "" : caseReference)
                           .equalsIgnoreCase(responseMap.get("CaseID").toString().trim()));

        } else if (
            (Objects.nonNull(jurisdictionMatch) && !jurisdictionMatch.isEmpty() && !jurisdictionMatch.isBlank() &&
                jurisdictionMatch.equalsIgnoreCase("Yes"))
                && (Objects.nonNull(locationMatch) && !locationMatch.isEmpty() && !locationMatch.isBlank() &&
                !locationMatch.equalsIgnoreCase("Yes"))) {

            log.debug("The No Location Code Scenario");
            log.debug("The value of the Mapped Jurisdiction Code" + responseMap.get("Jurisdiction").toString());
            log.debug("The value of the Mapped Case ID" + responseMap.get("CaseID").toString());

            assertEquals(4, responseMap.size());
            assertTrue(jurisdictionCode.equalsIgnoreCase(responseMap.get("Jurisdiction").toString().trim()));
            assertTrue(caseReference.equalsIgnoreCase(responseMap.get("CaseID").toString().trim()));

        } else {
            log.debug("The Only Case ID Scenario");
            assertEquals(1, responseMap.size());
            assertTrue(caseReference.equalsIgnoreCase(responseMap.get("CaseID").toString().trim()));
        }
        log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
