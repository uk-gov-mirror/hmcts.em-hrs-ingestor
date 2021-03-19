package uk.gov.hmcts.reform.em.hrs.ingestor.parse;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import uk.gov.hmcts.reform.em.hrs.ingestor.dto.HrsFilenameParsedDataDto;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNull;
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
        HrsFilenameParsedDataDto hrsFilenameParsedDataDTO = FileNameParser.parseFileName(inputValue);
        verifyValuesOfMappedResponse(
            hrsFilenameParsedDataDTO,
            jurisdiction,
            jurisdictionMatch,
            locationCode,
            locationMatch,
            caseID
        );
    }


    private void verifyValuesOfMappedResponse(final HrsFilenameParsedDataDto hrsFilenameParsedDataDTO,
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
            log.debug("The value of the Mapped Jurisdiction Code" + hrsFilenameParsedDataDTO.getJurisdiction());
            log.debug("The value of the Mapped Location Code" + hrsFilenameParsedDataDTO.getLocationCode());
            log.debug("The value of the Mapped Case ID" + hrsFilenameParsedDataDTO.getCaseID());

            assertTrue(jurisdictionCode.equalsIgnoreCase(hrsFilenameParsedDataDTO.getJurisdiction().trim()));
            assertTrue(locationCode.equalsIgnoreCase(hrsFilenameParsedDataDTO.getLocationCode().trim()));

            assertTrue((caseReference == null ? "" : caseReference)
                           .equalsIgnoreCase(hrsFilenameParsedDataDTO.getCaseID().trim()));

        } else if (
            (Objects.nonNull(jurisdictionMatch) && !jurisdictionMatch.isEmpty() && !jurisdictionMatch.isBlank() &&
                jurisdictionMatch.equalsIgnoreCase("Yes"))
                && (Objects.nonNull(locationMatch) && !locationMatch.isEmpty() && !locationMatch.isBlank() &&
                !locationMatch.equalsIgnoreCase("Yes"))) {

            log.debug("The No Location Code Scenario");
            log.debug("The value of the Mapped Jurisdiction Code" + hrsFilenameParsedDataDTO.getJurisdiction());
            log.debug("The value of the Mapped Case ID" + hrsFilenameParsedDataDTO.getCaseID());

            assertNull(hrsFilenameParsedDataDTO.getLocationCode());
            assertTrue(jurisdictionCode.equalsIgnoreCase(hrsFilenameParsedDataDTO.getJurisdiction().trim()));
            assertTrue(caseReference.equalsIgnoreCase(hrsFilenameParsedDataDTO.getCaseID().trim()));

        } else {

            log.debug("The Only Case ID Scenario");
            assertNull(hrsFilenameParsedDataDTO.getJurisdiction());
            assertNull(hrsFilenameParsedDataDTO.getLocationCode());
            assertNull(hrsFilenameParsedDataDTO.getRecordingDateTime());
            assertNull(hrsFilenameParsedDataDTO.getSegment());
            assertTrue(caseReference.equalsIgnoreCase(hrsFilenameParsedDataDTO.getCaseID()));
        }
        log.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
