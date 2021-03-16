package uk.gov.hmcts.reform.em.hrs.ingestor.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface HrsHttpClient {
    @GET
    Call<ResponseBody> getFiles(@Url String path);
}
