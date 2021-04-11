package uk.gov.hmcts.reform.em.hrs.ingestor.av;

import fi.solita.clamav.ClamAVClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class AntivirusClientImpl implements AntivirusClient {
    private final ClamAVClient clamavClient;

    @Autowired
    public AntivirusClientImpl(final ClamAVClient clamavClient) {
        this.clamavClient = clamavClient;
    }

    @Override
    public AvScanResult scan(final InputStream input) throws IOException {
        final byte[] result = clamavClient.scan(input);
        return ClamAVClient.isCleanReply(result) ? AvScanResult.CLEAN : AvScanResult.INFECTED;
    }

}
