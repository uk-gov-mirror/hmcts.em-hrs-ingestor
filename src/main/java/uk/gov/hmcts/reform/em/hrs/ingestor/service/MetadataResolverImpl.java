package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.Metadata;
import uk.gov.hmcts.reform.em.hrs.ingestor.dto.HrsFilenameParsedDataDto;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.FileParsingException;
import uk.gov.hmcts.reform.em.hrs.ingestor.parse.FileNameParser;

import java.util.Optional;
import javax.inject.Named;

@Named
public class MetadataResolverImpl implements MetadataResolver {
    @Override
    public Metadata resolve(final CvpItem item) throws FileParsingException {
        final String filename = Optional.ofNullable(item.getFilename())
            .map(this::stripFolder)
            .orElse(item.getFilename());
        HrsFilenameParsedDataDto parsedDataDto = FileNameParser.parseFileName(filename);

        return new Metadata(
            item.getFileUri(),
            item.getMd5Hash(),
            null, //audiofolderparsed ie audiostream1234 becomes 1234
            parsedDataDto.caseID,
            parsedDataDto.recordingDateTime,
            parsedDataDto.jurisdiction,
            parsedDataDto.locationCode,
            parsedDataDto.recordingUniquIdentifier,
            Integer.valueOf(parsedDataDto.segment)
        );
    }

    private String stripFolder(final String input) {
        final int separatorIndex = input.lastIndexOf("/");
        return input.substring(separatorIndex + 1);
    }
}
