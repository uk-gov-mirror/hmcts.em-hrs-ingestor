package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.Metadata;
import uk.gov.hmcts.reform.em.hrs.ingestor.parse.FileNameParser;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import javax.inject.Named;

@Named
public class MetadataResolverImpl implements MetadataResolver {
    @Override
    public Metadata resolve(final CvpItem item) {
        final Map<String, Object> objectMap = FileNameParser.parseFileName(item.getFilename());

        return new Metadata(
            item.getFileUri(),
            item.getMd5Hash(),
            null,
            (String) Optional.ofNullable(objectMap.get("CaseID")).orElse(null),
            (LocalDateTime) objectMap.get("RecordingDateTime"),
            (String) Optional.ofNullable(objectMap.get("Jurisdiction")).orElse(null),
            (String) Optional.ofNullable(objectMap.get("LocationCode")).orElse(null),
            null,
            Integer.parseInt((String) objectMap.get("Segment"))
        );
    }
}
