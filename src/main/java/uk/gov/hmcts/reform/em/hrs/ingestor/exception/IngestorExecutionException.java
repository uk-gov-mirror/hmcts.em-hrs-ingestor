package uk.gov.hmcts.reform.em.hrs.ingestor.exception;

public class IngestorExecutionException extends Exception {
    public IngestorExecutionException(String message) {
        super(message);
    }

    public IngestorExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
