package uk.gov.hmcts.reform.em.hrs.ingestor.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.Metadata;

public interface HrsHttpClient {
    @GET("/folders/{folder}/hearing-recording-file-names")
    Call<ResponseBody> getFiles(@Path("folder") String folderName);

    @POST("/folders/{folder}/hearing-recording")
    Call<ResponseBody> postFile(@Path("folder") String folderName, @Body Metadata metadata);
}
