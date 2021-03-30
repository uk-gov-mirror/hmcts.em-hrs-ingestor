package uk.gov.hmcts.reform.em.hrs.ingestor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class HrsFileSet {
    private final Set<String> hrsFiles;
}
