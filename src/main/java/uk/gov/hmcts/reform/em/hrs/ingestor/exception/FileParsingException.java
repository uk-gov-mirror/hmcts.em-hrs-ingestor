package uk.gov.hmcts.reform.em.hrs.ingestor.exception;

public class FileParsingException extends Exception {
    public FileParsingException(String message) {
        super(message);
    }

    public FileParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
