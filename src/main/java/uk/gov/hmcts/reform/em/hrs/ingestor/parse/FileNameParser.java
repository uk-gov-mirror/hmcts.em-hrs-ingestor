package uk.gov.hmcts.reform.em.hrs.ingestor.parse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.em.hrs.ingestor.dto.HrsFilenameParsedDataDto;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.FileParsingException;

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

    public static final HrsFilenameParsedDataDto parseFileName(final String fileName) throws FileParsingException {

        log.debug("This input fileName : " + fileName);
        if (Objects.isNull(fileName) || fileName.isBlank() || fileName.isEmpty()) {
            throw new FileParsingException("Invalid Filename",new IllegalArgumentException("The argument passed is not valid"));
        }
        Matcher royalCourtsOfJusticeWithLocationMatcher
            = Pattern.compile(
            ROYAL_COURTS_OF_JUSTICE_FILE_WITH_LOCATION_FORMAT_REGEX,
            Pattern.CASE_INSENSITIVE
        ).matcher(fileName);
        Matcher civilAndFamilyMatcher
            = Pattern.compile(
            CIVIL_AND_FAMILY_FILE_FORMAT_REGEX,
            Pattern.CASE_INSENSITIVE
        ).matcher(fileName);
        Matcher tribunalsMatcher
            = Pattern.compile(TRIBUNALS_FILE_FORMAT_REGEX, Pattern.CASE_INSENSITIVE).matcher(fileName);
        Matcher royalCourtsOfJusticeWithoutLocationMatcher
            = Pattern.compile(
            ROYAL_COURTS_OF_JUSTICE_FILE_WITHOUT_LOCATION_FORMAT_REGEX,
            Pattern.CASE_INSENSITIVE
        ).matcher(fileName);
        return processMatcher(
            fileName,
            civilAndFamilyMatcher,
            tribunalsMatcher,
            royalCourtsOfJusticeWithLocationMatcher,
            royalCourtsOfJusticeWithoutLocationMatcher
        );
    }

    private static HrsFilenameParsedDataDto processMatcher(final String fileName,
                                                           final Matcher civilAndFamilyMatcher,
                                                           final Matcher tribunalsMatcher,
                                                           final Matcher royalCourtsOfJusticeWithLocationMatcher,
                                                           final Matcher royalCourtsOfJusticeWithoutLocationMatcher)
         {

        if (royalCourtsOfJusticeWithLocationMatcher.matches()) {
            log.debug("This is a Royal Courts of Justice Locations based match");
            return processLocationMatcherForRoyalCourtsOfJustice(
                royalCourtsOfJusticeWithLocationMatcher);
        } else if (civilAndFamilyMatcher.matches()) {
            log.debug("This is a Civil and Family based match");
            return processLocationMatcherForCivilAndFamilies(civilAndFamilyMatcher);
        } else if (royalCourtsOfJusticeWithoutLocationMatcher.matches()) {
            log.debug("This is a Royal Courts of Justice Without Locations based match");
            return processNonLocationMatcher(royalCourtsOfJusticeWithoutLocationMatcher);
        } else if (tribunalsMatcher.matches()) {
            log.debug("This is a Tribunals based match");
            return processNonLocationMatcher(tribunalsMatcher);
        } else {
            String[] values = fileName.split("_");
            return HrsFilenameParsedDataDto
                .builder().caseID(values[0]).build();
        }
    }

    private static final HrsFilenameParsedDataDto processLocationMatcherForCivilAndFamilies(
        final Matcher matcher)
         {

        return HrsFilenameParsedDataDto
            .builder()
            .jurisdiction(matcher.group(1))
            .locationCode(
                matcher.group(2).trim().length() == 4 ?
                matcher.group(2).replaceFirst("^0*", "") :
                matcher.group(2))
            .caseID(matcher.group(3))
            .recordingDateTime(processRawDatePart(matcher.group(4), matcher.group(5)))
            .segment(matcher.group(6))
            .recordingUniquIdentifier(matcher.group(1)
                                          + "-" + matcher.group(2)
                                          + "-" + matcher.group(3)
                                          + "_" + matcher.group(4)
                                          + "-" + matcher.group(5)).build();
    }

    private static final HrsFilenameParsedDataDto processLocationMatcherForRoyalCourtsOfJustice(
        final Matcher matcher) {

        return HrsFilenameParsedDataDto
            .builder()
            .jurisdiction(matcher.group(1))
            .locationCode(matcher.group(2).trim().length() == 4 ?
                          matcher.group(2).replaceFirst("^0*", "") :
                          matcher.group(2))
            .caseID(matcher.group(3))
            .recordingDateTime(processRawDatePart(matcher.group(4), matcher.group(5)))
            .segment(matcher.group(6))
            .recordingUniquIdentifier(matcher.group(1)
                                          + "-" + matcher.group(2)
                                          + "-" + matcher.group(3)
                                          + "_" + matcher.group(4)
                                          + "-" + matcher.group(5)).build();

    }

    private static final HrsFilenameParsedDataDto processNonLocationMatcher(
        final Matcher matcher) {
        return HrsFilenameParsedDataDto
            .builder()
            .jurisdiction(matcher.group(1))
            .caseID(matcher.group(2))
            .recordingDateTime(processRawDatePart(matcher.group(3), matcher.group(4)))
            .segment(matcher.group(5))
            .recordingUniquIdentifier(matcher.group(1) + "-"
                                          + matcher.group(2)
                                          + "_" + matcher.group(3)
                                          + "-" + matcher.group(4)).build();

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
