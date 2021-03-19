package uk.gov.hmcts.reform.em.hrs.ingestor.exception;

public class HrsApiException extends Exception {
    public HrsApiException(String message) {
        super(message);
    }

    public HrsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
