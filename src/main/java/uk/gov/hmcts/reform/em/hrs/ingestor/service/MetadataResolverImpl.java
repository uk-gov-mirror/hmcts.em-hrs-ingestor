package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;
import uk.gov.hmcts.reform.em.hrs.ingestor.dto.ParsedFilenameDto;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.FilenameParsingException;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.Metadata;
import uk.gov.hmcts.reform.em.hrs.ingestor.parse.FilenameParser;

import java.util.function.Function;

@Component
public class MetadataResolverImpl implements MetadataResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataResolverImpl.class);


    static Function<String, Tuple4<String, Integer, String, String>> FRAGMENT = x -> {
        final String folderPrefix = "^audiostream";
        final String[] split = x.split("/");
        final String folder = split[0];
        final String postfix = split[1];
        final int index = postfix.lastIndexOf(".");

        return Tuples.of(
            folder,
            Integer.parseInt(folder.replaceFirst(folderPrefix, "")),
            postfix.substring(0, index),
            postfix.substring(index + 1)
        );
    };

    @Override
    public Metadata resolve(final CvpItem item) throws FilenameParsingException {

        try {
            final Tuple4<String, Integer, String, String> fragments = FRAGMENT.apply(item.getFilename());
            final ParsedFilenameDto parsedDataDto = FilenameParser.parseFileName(fragments.getT3());

            String parsedSegmentNumber = parsedDataDto.getSegment();

            if (!NumberUtils.isParsable(parsedSegmentNumber)) {
                //TODO consider guarding against other required fields
                LOGGER.warn("Filepath does not contain a valid segment number: {}", String.valueOf(item.getFilename()));
                throw new FilenameParsingException("Filepath does not contain a valid segment number");
            }

            Metadata metadata = new Metadata(
                fragments.getT1(),
                item.getFilename(),
                item.getFileUri(),
                item.getContentLength(),
                item.getMd5Hash(),
                parsedDataDto.getUniqueIdentifier(),
                Integer.parseInt(parsedSegmentNumber),
                fragments.getT4(),
                parsedDataDto.getRecordingDateTime(),
                parsedDataDto.getCaseID(),
                fragments.getT2(),
                parsedDataDto.getJurisdiction(),
                parsedDataDto.getLocationCode()
            );
            return metadata;


        } catch (FilenameParsingException e) {
            LOGGER.warn("Error parsing Filename {}", String.valueOf(item.getFilename()));
            throw new FilenameParsingException(e.getMessage(), e);

        } catch (Exception e) {
            LOGGER.warn("Error parsing Filename {}", String.valueOf(item.getFilename()));
            LOGGER.error("Unhandled Exception",e);
            throw new FilenameParsingException(
                "Unexpected Error parsing cvpItem: " + String.valueOf(e.getMessage()),
                e
            );
        }
    }
}
