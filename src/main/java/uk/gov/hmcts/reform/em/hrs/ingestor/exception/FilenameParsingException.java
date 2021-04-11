package uk.gov.hmcts.reform.em.hrs.ingestor.exception;

public class FilenameParsingException extends Exception {
    public FilenameParsingException(String message) {
        super(message);
    }

    public FilenameParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
