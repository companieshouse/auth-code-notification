package uk.gov.companieshouse.authcodenotification.validation.utils;

import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

import java.util.regex.Pattern;

public class ValidationUtils {

    public static final String NOT_NULL_ERROR_MESSAGE = "%s must not be null";
    public static final String NOT_EMPTY_ERROR_MESSAGE = "%s must not be empty and must not only consist of whitespace";
    public static final String INCORRECT_LENGTH_ERROR_MESSAGE = "%s must be %s characters";
    public static final String INVALID_CHARACTERS_ERROR_MESSAGE = "Invalid characters %s must be in the format %s";

    private ValidationUtils(){}

    public static boolean isNotBlank(String toTest, String qualifiedFieldName, Errors errs, String loggingContext) {
        return isNotNull(toTest, qualifiedFieldName, errs, loggingContext)
                && isNotEmpty(toTest, qualifiedFieldName, errs, loggingContext);
    }

    private static boolean isNotNull(Object toTest, String qualifiedFieldName, Errors errs, String loggingContext) {
        if (toTest == null) {
            setErrorMsgToLocation(errs, qualifiedFieldName, NOT_NULL_ERROR_MESSAGE.replace("%s", qualifiedFieldName));
            ApiLogger.infoContext(loggingContext , qualifiedFieldName + " Field is null");
            return false;
        }
        return true;
    }

    private static boolean isNotEmpty(String toTest, String qualifiedFieldName, Errors errs, String loggingContext) {
        if (toTest.trim().isEmpty()) {
            setErrorMsgToLocation(errs, qualifiedFieldName, String.format(NOT_EMPTY_ERROR_MESSAGE, qualifiedFieldName));
            ApiLogger.infoContext(loggingContext, qualifiedFieldName + " Field is empty");
            return false;
        }
        return true;
    }

    public static boolean isSpecificLength(String toTest, Integer length, String qualifiedFieldName, Errors errs, String loggingContext) {
        if (toTest.length() != length) {
            setErrorMsgToLocation(errs, qualifiedFieldName,
                    String.format(INCORRECT_LENGTH_ERROR_MESSAGE, qualifiedFieldName, length.toString()));
            ApiLogger.infoContext(loggingContext, "Invalid length for " + qualifiedFieldName);
            return false;
        }
        return true;
    }

    public static boolean isValidCharacters(String toTest, String regex, String qualifiedFieldName, Errors errs, String loggingContext) {
        var pattern = Pattern.compile(regex);
        if (!pattern.matcher(toTest).matches()) {
            setErrorMsgToLocation(errs, qualifiedFieldName, String.format(INVALID_CHARACTERS_ERROR_MESSAGE, qualifiedFieldName, regex));
            ApiLogger.infoContext(loggingContext, "Invalid characters for " + qualifiedFieldName);
            return false;
        }
        return true;
    }

    public static void setErrorMsgToLocation(Errors errors, String qualifiedFieldName, String msg){
        final var error = Err.invalidBodyBuilderWithLocation(qualifiedFieldName).withError(msg).build();
        errors.addError(error);
    }
}
