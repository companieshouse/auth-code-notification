package uk.gov.companieshouse.authcodenotification.exception;

public class EntityNotFoundException extends ServiceException {
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
