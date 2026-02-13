package uk.gov.companieshouse.authcodenotification.exception;

public class EmailClientException extends RuntimeException {

    public EmailClientException(String message, Throwable cause) {
        super(message, cause);
    }

}