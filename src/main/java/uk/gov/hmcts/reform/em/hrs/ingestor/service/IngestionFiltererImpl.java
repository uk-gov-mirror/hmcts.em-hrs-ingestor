package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.HrsFileSet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Named;

@Named
public class IngestionFiltererImpl implements IngestionFilterer {
    private static final Function<Set<String>, Function<Set<String>, Set<String>>> FILTER = x -> y -> {
        Set<String> a = new HashSet<>(x);
        Set<String> b = new HashSet<>(y);

        return a.removeAll(b) ? Collections.unmodifiableSet(a) : x;
    };

    @Override
    public Set<String> filter(final CvpFileSet cvpFileSet, final HrsFileSet hrsFileSet) {
        return FILTER.apply(cvpFileSet.getCvpFiles()).apply(hrsFileSet.getHrsFiles());
    }
}
