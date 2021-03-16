package uk.gov.hmcts.reform.em.hrs.ingestor.av;

import fi.solita.clamav.ClamAVClient;

import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class AntivirusClientImpl implements AntivirusClient {
    private final ClamAVClient clamavClient;

    @Inject
    public AntivirusClientImpl(final ClamAVClient clamavClient) {
        this.clamavClient = clamavClient;
    }

    @Override
    public AvScanResult scan(final InputStream input) throws IOException {
        final byte[] result = clamavClient.scan(input);

        return ClamAVClient.isCleanReply(result) ? AvScanResult.CLEAN : AvScanResult.INFECTED;
    }

}
