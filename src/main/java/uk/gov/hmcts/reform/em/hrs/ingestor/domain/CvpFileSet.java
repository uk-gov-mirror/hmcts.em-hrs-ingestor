package uk.gov.hmcts.reform.em.hrs.ingestor.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class CvpFileSet {
    private final Set<String> cvpFiles;
}
