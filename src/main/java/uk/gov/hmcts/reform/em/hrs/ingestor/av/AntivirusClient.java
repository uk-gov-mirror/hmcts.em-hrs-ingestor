package uk.gov.hmcts.reform.em.hrs.ingestor.av;

import java.io.IOException;
import java.io.InputStream;

public interface AntivirusClient {

    AvScanResult scan(InputStream input) throws IOException;

}
