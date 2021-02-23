package uk.gov.hmcts.reform.em.hrs.ingestor.exception;

public class AppConfigurationException extends RuntimeException {
    static final long serialVersionUID = 1L;


    public AppConfigurationException(String message) {
        super(message);
    }

    public AppConfigurationException(String message, Throwable exception) {
        super(message, exception);
    }
}
