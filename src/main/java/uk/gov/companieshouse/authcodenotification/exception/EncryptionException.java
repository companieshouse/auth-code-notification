package uk.gov.companieshouse.authcodenotification.exception;

public class EncryptionException extends Exception {
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
