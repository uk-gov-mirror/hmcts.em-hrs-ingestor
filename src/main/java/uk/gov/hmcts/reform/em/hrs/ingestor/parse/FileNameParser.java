package uk.gov.hmcts.reform.em.hrs.ingestor.parse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileNameParser {

    private static final String CIVIL_AND_FAMILY_FILE_FORMAT_REGEX
            = "^(CV|FM|CP)-(([0-9]){3}|([0-9]){4})-([A-Z0-9-]*)_([0-9-.]*)-([A-Z]{3})_([0-9])$";

    private static final String TRIBUNALS_FILE_FORMAT_REGEX
            = "^(EE|ES|GR|HE|IA|PC|SE|TC|WP|EA|AU|IU|LU|TU)-([A-Z0-9-]*)_([0-9-.]*)-([A-Z]{3})_([0-9])$";

    private static final String ROYAL_COURTS_OF_JUSTICE_FILE_WITH_LOCATION_FORMAT_REGEX
            = "^(CI|QB|HF|CF|BP|SC|CR|CV-0372|CV-0266)-(([0-9]){3}|([0-9]){4})-([A-Z0-9-]*)_([0-9-.]*)-([A-Z]{3})_([0-9])$";

    private static final String ROYAL_COURTS_OF_JUSTICE_FILE_WITHOUT_LOCATION_FORMAT_REGEX
            = "^(CI|QB|HF|CF|BP|SC|CR|CV-0372|CV-0266)-([A-Z0-9-]*)_([0-9-.]*)-([A-Z]{3})_([0-9])$";

    public static final Map<String, Object> parseFileName(final String fileName) {

        log.debug("This input fileName : " + fileName);
        if (Objects.isNull(fileName) || fileName.isBlank() || fileName.isEmpty()) {
            throw new IllegalArgumentException("The argument passed is not valid");
        }
        Matcher civilAndFamilyBasedMatcher
                = Pattern.compile(CIVIL_AND_FAMILY_FILE_FORMAT_REGEX, Pattern.CASE_INSENSITIVE).matcher(fileName);
        Matcher tribunalsBasedMatcher
                = Pattern.compile(TRIBUNALS_FILE_FORMAT_REGEX,Pattern.CASE_INSENSITIVE).matcher(fileName);
        Matcher royalCourtsOfJusticeWithLocationBasedMatcher
                = Pattern.compile(ROYAL_COURTS_OF_JUSTICE_FILE_WITH_LOCATION_FORMAT_REGEX,Pattern.CASE_INSENSITIVE).matcher(fileName);
        Matcher royalCourtsOfJusticeWithoutLocationBasedMatcher
                = Pattern.compile(ROYAL_COURTS_OF_JUSTICE_FILE_WITHOUT_LOCATION_FORMAT_REGEX,Pattern.CASE_INSENSITIVE).matcher(fileName);
        return processMatcher(fileName,
                civilAndFamilyBasedMatcher,
                tribunalsBasedMatcher,
                royalCourtsOfJusticeWithLocationBasedMatcher, royalCourtsOfJusticeWithoutLocationBasedMatcher);
    }

    private static Map<String, Object> processMatcher(final String fileName, final Matcher civilAndFamilyBasedMatcher,
                                                      final Matcher tribunalsBasedMatcher, final Matcher royalCourtsOfJusticeWithLocationBasedMatcher,
                                                      final Matcher royalCourtsOfJusticeWithoutLocationBasedMatcher) {

        if (civilAndFamilyBasedMatcher.matches()) {
            log.debug("This is a Civil and Family based match");
            return Collections.unmodifiableMap(processLocationBasedMatcher(civilAndFamilyBasedMatcher));
        } else if (royalCourtsOfJusticeWithLocationBasedMatcher.matches()) {
            log.debug("This is a Royal Courts of Justice Locations based match");
            return Collections.unmodifiableMap(processLocationBasedMatcher(royalCourtsOfJusticeWithLocationBasedMatcher));
        } else if (royalCourtsOfJusticeWithoutLocationBasedMatcher.matches()) {
            log.debug("This is a Royal Courts of Justice Without Locations based match");
            return Collections.unmodifiableMap(processNonLocationBasedMatcher(royalCourtsOfJusticeWithoutLocationBasedMatcher));
        } else if (tribunalsBasedMatcher.matches()) {
            log.debug("This is a Tribunals based match");
            return Collections.unmodifiableMap(processNonLocationBasedMatcher(tribunalsBasedMatcher));
        } else {
            String[] values = fileName.split("_");
            return Map.of("CaseID", values[0]);
        }
    }

    private static final Map<String, Object> processLocationBasedMatcher(final Matcher firstLocationBasedMatcher) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("Jurisdiction", firstLocationBasedMatcher.group(1));
        final String locationCode = firstLocationBasedMatcher.group(2);
        responseMap.put("LocationCode", locationCode.trim().length() == 4 ?
                locationCode.replaceFirst("^0*", "") : locationCode);
        responseMap.put("CaseID", firstLocationBasedMatcher.group(5));
        responseMap.put("RecordingDateTime", processRawDatePart(firstLocationBasedMatcher.group(6), firstLocationBasedMatcher.group(7)));
        responseMap.put("Segment", firstLocationBasedMatcher.group(8));
        return responseMap;
    }

    private static final Map<String, Object> processNonLocationBasedMatcher(final Matcher nonLocationBasedMatcher) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("Jurisdiction", nonLocationBasedMatcher.group(1));
        responseMap.put("CaseID", nonLocationBasedMatcher.group(2));
        responseMap.put("RecordingDateTime", processRawDatePart(nonLocationBasedMatcher.group(3), nonLocationBasedMatcher.group(4)));
        responseMap.put("Segment", nonLocationBasedMatcher.group(5));
        return responseMap;
    }


    private static LocalDateTime processRawDatePart(final String rawDatePart, final String timeZone) {

        log.debug("The Later File Part " + rawDatePart);
        log.debug("The Time Zone Part " + timeZone);

        DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSS").withZone(ZoneId.of(timeZone));
        LocalDateTime dateTimeObject = LocalDateTime.parse(rawDatePart, datePattern);

        log.debug("The value of the Formatted Date Time Object" + dateTimeObject);
        return dateTimeObject;
    }
}
