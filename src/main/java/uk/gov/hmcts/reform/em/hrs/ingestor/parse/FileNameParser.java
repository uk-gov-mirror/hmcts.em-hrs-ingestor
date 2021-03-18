package uk.gov.hmcts.reform.em.hrs.ingestor.parse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.em.hrs.ingestor.dto.HRSFilenameParsedDataDTO;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileNameParser {

    private static final String ROYAL_COURTS_OF_JUSTICE_FILE_WITH_LOCATION_FORMAT_REGEX
        = "^(CV)-(0372|0266)-([A-Z0-9-]*)_([0-9-.]*)-([A-Z]{3})_([0-9]+)$";

    private static final String CIVIL_AND_FAMILY_FILE_FORMAT_REGEX
        = "^(CV|FM|CP)-([0-9]{3,4})-([A-Z0-9-]*)_([0-9-.]*)-([A-Z]{3})_([0-9]+)$";

    private static final String TRIBUNALS_FILE_FORMAT_REGEX
        = "^(EE|ES|GR|HE|IA|PC|SE|TC|WP|EA|AU|IU|LU|TU)-([A-Z0-9-]*)_([0-9-.]*)-([A-Z]{3})_([0-9]+)$";

    private static final String ROYAL_COURTS_OF_JUSTICE_FILE_WITHOUT_LOCATION_FORMAT_REGEX
        = "^(CI|QB|HF|CF|BP|SC|CR|CV)-([A-Z0-9-]*)_([0-9-.]*)-([A-Z]{3})_([0-9]+)$";

    public static final HRSFilenameParsedDataDTO parseFileName(final String fileName) throws Exception {

        log.debug("This input fileName : " + fileName);
        if (Objects.isNull(fileName) || fileName.isBlank() || fileName.isEmpty()) {
            throw new IllegalArgumentException("The argument passed is not valid");
        }
        Matcher royalCourtsOfJusticeWithLocationBasedMatcher
            = Pattern.compile(
            ROYAL_COURTS_OF_JUSTICE_FILE_WITH_LOCATION_FORMAT_REGEX,
            Pattern.CASE_INSENSITIVE
        ).matcher(fileName);
        Matcher civilAndFamilyBasedMatcher
            = Pattern.compile(
            CIVIL_AND_FAMILY_FILE_FORMAT_REGEX,
            Pattern.CASE_INSENSITIVE
        ).matcher(fileName);
        Matcher tribunalsBasedMatcher
            = Pattern.compile(TRIBUNALS_FILE_FORMAT_REGEX, Pattern.CASE_INSENSITIVE).matcher(fileName);
        Matcher royalCourtsOfJusticeWithoutLocationBasedMatcher
            = Pattern.compile(
            ROYAL_COURTS_OF_JUSTICE_FILE_WITHOUT_LOCATION_FORMAT_REGEX,
            Pattern.CASE_INSENSITIVE
        ).matcher(fileName);
        return processMatcher(
            fileName,
            civilAndFamilyBasedMatcher,
            tribunalsBasedMatcher,
            royalCourtsOfJusticeWithLocationBasedMatcher,
            royalCourtsOfJusticeWithoutLocationBasedMatcher
        );
    }

    private static HRSFilenameParsedDataDTO processMatcher(final String fileName,
                                                           final Matcher civilAndFamilyBasedMatcher,
                                                           final Matcher tribunalsBasedMatcher,
                                                           final Matcher royalCourtsOfJusticeWithLocationBasedMatcher,
                                                           final Matcher royalCourtsOfJusticeWithoutLocationBasedMatcher)
        throws Exception {

        if (royalCourtsOfJusticeWithLocationBasedMatcher.matches()) {
            log.debug("This is a Royal Courts of Justice Locations based match");
            return processLocationBasedMatcherForRoyalCourtsOfJustice(
                royalCourtsOfJusticeWithLocationBasedMatcher);
        } else if (civilAndFamilyBasedMatcher.matches()) {
            log.debug("This is a Civil and Family based match");
            return processLocationBasedMatcherForCivilAndFamilies(civilAndFamilyBasedMatcher);
        } else if (royalCourtsOfJusticeWithoutLocationBasedMatcher.matches()) {
            log.debug("This is a Royal Courts of Justice Without Locations based match");
            return processNonLocationBasedMatcher(royalCourtsOfJusticeWithoutLocationBasedMatcher);
        } else if (tribunalsBasedMatcher.matches()) {
            log.debug("This is a Tribunals based match");
            return processNonLocationBasedMatcher(tribunalsBasedMatcher);
        } else {
            String[] values = fileName.split("_");
            return HRSFilenameParsedDataDTO
                .builder().caseID(values[0]).build();
        }
    }

    private static final HRSFilenameParsedDataDTO processLocationBasedMatcherForCivilAndFamilies(
        final Matcher locationBasedMatcher)
        throws Exception {

        return HRSFilenameParsedDataDTO
            .builder()
            .jurisdiction(locationBasedMatcher.group(1))
            .locationCode(locationBasedMatcher.group(2).trim().length() == 4 ?
                              locationBasedMatcher.group(2).replaceFirst("^0*", "") : locationBasedMatcher.group(2))
            .caseID(locationBasedMatcher.group(3))
            .recordingDateTime(processRawDatePart(locationBasedMatcher.group(4), locationBasedMatcher.group(5)))
            .segment(locationBasedMatcher.group(6))
            .recordingUniquIdentifier(locationBasedMatcher.group(1) + "-" +
                                          locationBasedMatcher.group(2) + "-" +
                                          locationBasedMatcher.group(3) + "_" +
                                          locationBasedMatcher.group(4) + "-" +
                                          locationBasedMatcher.group(5)).build();
    }

    private static final HRSFilenameParsedDataDTO processLocationBasedMatcherForRoyalCourtsOfJustice(
        final Matcher locationBasedMatcher) {

        return HRSFilenameParsedDataDTO
            .builder()
            .jurisdiction(locationBasedMatcher.group(1))
            .locationCode(locationBasedMatcher.group(2).trim().length() == 4 ?
                              locationBasedMatcher.group(2).replaceFirst("^0*", "") : locationBasedMatcher.group(2))
            .caseID(locationBasedMatcher.group(3))
            .recordingDateTime(processRawDatePart(locationBasedMatcher.group(4), locationBasedMatcher.group(5)))
            .segment(locationBasedMatcher.group(6))
            .recordingUniquIdentifier(locationBasedMatcher.group(1) + "-" +
                                          locationBasedMatcher.group(2) + "-" +
                                          locationBasedMatcher.group(3) + "_" +
                                          locationBasedMatcher.group(4) + "-" +
                                          locationBasedMatcher.group(5)).build();

    }

    private static final HRSFilenameParsedDataDTO processNonLocationBasedMatcher(
        final Matcher nonLocationBasedMatcher) {
        return HRSFilenameParsedDataDTO
            .builder()
            .jurisdiction(nonLocationBasedMatcher.group(1))
            .caseID(nonLocationBasedMatcher.group(2))
            .recordingDateTime(processRawDatePart(nonLocationBasedMatcher.group(3), nonLocationBasedMatcher.group(4)))
            .segment(nonLocationBasedMatcher.group(5))
            .recordingUniquIdentifier(nonLocationBasedMatcher.group(1) + "-" +
                                          nonLocationBasedMatcher.group(2) + "_" +
                                          nonLocationBasedMatcher.group(3) + "-" +
                                          nonLocationBasedMatcher.group(4)).build();

    }

    private static LocalDateTime processRawDatePart(final String rawDatePart, final String timeZone) {

        log.debug("The Later File Part " + rawDatePart);
        log.debug("The Time Zone Part " + timeZone);

        DateTimeFormatter datePattern =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSS").withZone(ZoneId.of(timeZone));
        LocalDateTime dateTimeObject = LocalDateTime.parse(rawDatePart, datePattern);

        log.debug("The value of the Formatted Date Time Object" + dateTimeObject);
        return dateTimeObject;
    }

    private static File getFile(final String path) throws IOException {

        File file = ResourceUtils.getFile("classpath:" + path);
        //File is found
        log.debug("File Found : " + file.exists());
        return file;
    }
}
