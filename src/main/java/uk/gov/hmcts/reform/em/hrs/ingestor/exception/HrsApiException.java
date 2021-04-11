package uk.gov.hmcts.reform.em.hrs.ingestor.exception;

import lombok.Getter;
import okhttp3.ResponseBody;

@Getter
public class HrsApiException extends Exception {
    private int code;
    private String message;
    private String body;

    public HrsApiException(String message) {
        super(message);
    }

    public HrsApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public HrsApiException(int code, String message, ResponseBody body) {
        this.code = code;
        this.message = message;
        this.body = body.toString();
    }
}
