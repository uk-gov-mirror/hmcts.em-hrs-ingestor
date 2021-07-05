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


    static Function<String, Tuple4<String, Integer, String, String>> FULLPATH_FILENAME_PARSER = filenameWithPath -> {
        LOGGER.info("resolving filenameWithPath {}", filenameWithPath);
        final String folderPrefix = "^audiostream";
        final String[] splitOnForwardSlash = filenameWithPath.split("/");
        int fileNamePartsCount = splitOnForwardSlash.length;
        LOGGER.info("fileNamePartsCount length: {}", fileNamePartsCount);
        if (fileNamePartsCount!=2)
        {
            LOGGER.warn("not valid filename (req folder/filename)");
            return null;
        }
        final String folder = splitOnForwardSlash[0];
        LOGGER.info("folder: {}",folder);
        final String filenameWithExtension = splitOnForwardSlash[1];
        LOGGER.info("filenameWithExtension {}",filenameWithExtension);
        final int lastIndexOfPeriodCharacter = filenameWithExtension.lastIndexOf(".");

        String fileNamePart = filenameWithExtension.substring(0, lastIndexOfPeriodCharacter);
        String fileExtensionPart = filenameWithExtension.substring(lastIndexOfPeriodCharacter + 1);
        LOGGER.info("fileNamePart {}",fileNamePart);
        LOGGER.info("fileExtensionPart {}",fileExtensionPart);

        return Tuples.of(
            folder,
            Integer.parseInt(folder.replaceFirst(folderPrefix, "")),
            fileNamePart,
            fileExtensionPart
        );
    };

    @Override
    public Metadata resolve(final CvpItem item) throws FilenameParsingException {

        try {
            final Tuple4<String, Integer, String, String> fragments =
                FULLPATH_FILENAME_PARSER.apply(item.getFilename());
            final ParsedFilenameDto parsedDataDto = FilenameParser.parseFileName(fragments.getT3());

            String parsedSegmentNumber = parsedDataDto.getSegment();

            if (!NumberUtils.isParsable(parsedSegmentNumber)) {


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
            LOGGER.error("Unhandled Exception", e);
            throw new FilenameParsingException(
                "Unexpected Error parsing cvpItem: " + String.valueOf(e.getMessage()),
                e
            );
        }
    }
}
