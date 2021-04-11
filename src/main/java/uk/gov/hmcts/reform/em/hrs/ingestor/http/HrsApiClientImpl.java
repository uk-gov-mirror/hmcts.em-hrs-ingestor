package uk.gov.hmcts.reform.em.hrs.ingestor.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import uk.gov.hmcts.reform.em.hrs.ingestor.dto.RecordingFilenameDto;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.HrsFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.model.Metadata;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class HrsApiClientImpl implements HrsApiClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HrsApiClientImpl.class);

    private static final TypeReference<RecordingFilenameDto> TYPE_REFERENCE = new TypeReference<>() {
    };

    private final HrsHttpClient hrsHttpClient;
    private final ObjectMapper objectMapper;

    @Inject
    public HrsApiClientImpl(final HrsHttpClient hrsHttpClient, final ObjectMapper objectMapper) {
        this.hrsHttpClient = hrsHttpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public HrsFileSet getIngestedFiles(String folderName) throws IOException {
        final Response<ResponseBody> response = hrsHttpClient.getFiles(folderName)
            .execute();

        if (!response.isSuccessful()) {
            parseErrorBody(response.code(), response.message(), Objects.requireNonNull(response.errorBody()));
            return new HrsFileSet(Collections.emptySet());
        }

        final RecordingFilenameDto body = parseBody(response.body());
        final Set<String> files = body.getFilenames();

        return new HrsFileSet(files);
    }

    @Override
    public boolean postFile(final Metadata metadata) throws IOException {
        final Response<ResponseBody> response = hrsHttpClient.postFile(metadata).execute();

        boolean isSuccessful = response.isSuccessful();
        if (!isSuccessful) {
            parseErrorBody(response.code(), response.message(), Objects.requireNonNull(response.errorBody()));
        }
        return isSuccessful;
    }

    private RecordingFilenameDto parseBody(final ResponseBody body) throws IOException {
        return objectMapper.readValue(Objects.requireNonNull(body).string(), TYPE_REFERENCE);
    }

    private void parseErrorBody(final int code,
                                final String message,
                                final ResponseBody body) throws IOException {
        final String errorBodyMessage = body.string();
        LOGGER.warn("Response error: {} => {} => {}", code, message, errorBodyMessage);
    }
}
