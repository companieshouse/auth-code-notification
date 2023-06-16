package uk.gov.companieshouse.authcodenotification.validation;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.authcodenotification.validation.utils.ValidationUtils;
import uk.gov.companieshouse.service.rest.err.Errors;

@Component
public class AuthCodeEmailValidator {

    public static final String COMPANY_NUMBER_PARAMETER = "company_number";

    public static final String AUTH_CODE_FIELD = "auth_code";

    public static final String AUTH_CODE_REGEX = "^[A-Z0-9]{6}$";

    public static final String COMPANY_NUMBER_REGEX = "^[^/]([A-Za-z0-9]{6,10})$";

    public Errors validate(String companyNumber, String authCode, Errors errors, String loggingContext) {
        validateCompanyNumber(companyNumber, errors, loggingContext);
        validateAuthCode(authCode, errors, loggingContext);
        return errors;
    }

    private void validateCompanyNumber(String companyNumber, Errors errors, String loggingContext) {
        if (ValidationUtils.isNotBlank(companyNumber, COMPANY_NUMBER_PARAMETER, errors, loggingContext)
           && ValidationUtils.isSpecificLength(companyNumber, 8, COMPANY_NUMBER_PARAMETER, errors, loggingContext)) {
           ValidationUtils.isValidCharacters(companyNumber, COMPANY_NUMBER_REGEX, COMPANY_NUMBER_PARAMETER, errors, loggingContext);
        }
    }

    private void validateAuthCode(String authCode, Errors errors, String loggingContext) {
        if (ValidationUtils.isNotBlank(authCode, AUTH_CODE_FIELD, errors, loggingContext)
           && ValidationUtils.isSpecificLength(authCode, 6, AUTH_CODE_FIELD, errors, loggingContext)) {
           ValidationUtils.isValidCharacters(authCode, AUTH_CODE_REGEX, AUTH_CODE_FIELD, errors, loggingContext);
        }
    }
}
