package uk.gov.companieshouse.authcodenotification.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.authcodenotification.model.SendEmailRequestDto;
import uk.gov.companieshouse.authcodenotification.validation.utils.ValidationUtils;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AuthCodeEmailValidatorTest {

    private static final String CONTEXT = "abc";

    private AuthCodeEmailValidator authCodeEmailValidator;

    @BeforeEach
    void setup() {
        authCodeEmailValidator = new AuthCodeEmailValidator();
    }

    @Test
    void testSuccessfulValidationWhenCompanyNumberStartsWithTwoLetters() {
        Errors errors = authCodeEmailValidator.validate("OE000001", "A1B2C3", new Errors(), CONTEXT);
        assertFalse(errors.hasErrors());
    }

    @Test
    void testErrorsReportedWhenNullCompanyNumber() {
        Errors errors = authCodeEmailValidator.validate(null, "A1B2C3", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.NOT_NULL_ERROR_MESSAGE, AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER);
        assertError(AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenEmptyCompanyNumber() {
        Errors errors = authCodeEmailValidator.validate("", "A1B2C3", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.NOT_EMPTY_ERROR_MESSAGE, AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER);
        assertError(AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenBlankCompanyNumber() {
        Errors errors = authCodeEmailValidator.validate(" ", "A1B2C3", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.NOT_EMPTY_ERROR_MESSAGE, AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER);
        assertError(AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenShortCompanyNumber() {
        Errors errors = authCodeEmailValidator.validate("OE00000", "A1B2C3", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.INCORRECT_LENGTH_ERROR_MESSAGE, AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, 8);
        assertError(AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenLongCompanyNumber() {
        Errors errors = authCodeEmailValidator.validate("OE0000011", "A1B2C3", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.INCORRECT_LENGTH_ERROR_MESSAGE, AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, 8);
        assertError(AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenCompanyNumberIsAllNumbers() {
        Errors errors = authCodeEmailValidator.validate("11000001", "A1B2C3", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.INVALID_CHARACTERS_ERROR_MESSAGE, AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, AuthCodeEmailValidator.COMPANY_NUMBER_REGEX);
        assertError(AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenCompanyNumberIsAllLetters() {
        Errors errors = authCodeEmailValidator.validate("ABCDEFGH", "A1B2C3", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.INVALID_CHARACTERS_ERROR_MESSAGE, AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, AuthCodeEmailValidator.COMPANY_NUMBER_REGEX);
        assertError(AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenInvalidCharcatersInCompanyNumber() {
        Errors errors = authCodeEmailValidator.validate("OE$00001", "A1B2C3", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.INVALID_CHARACTERS_ERROR_MESSAGE, AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, AuthCodeEmailValidator.COMPANY_NUMBER_REGEX);
        assertError(AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenInvalidLettersInCompanyNumber() {
        Errors errors = authCodeEmailValidator.validate("110000EO", "A1B2C3", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.INVALID_CHARACTERS_ERROR_MESSAGE, AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, AuthCodeEmailValidator.COMPANY_NUMBER_REGEX);
        assertError(AuthCodeEmailValidator.COMPANY_NUMBER_PARAMETER, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenNullAuthCode() {
        Errors errors = authCodeEmailValidator.validate("OE000001", null, new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.NOT_NULL_ERROR_MESSAGE, AuthCodeEmailValidator.AUTH_CODE_FIELD);
        assertError(AuthCodeEmailValidator.AUTH_CODE_FIELD, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenEmptyAuthCode() {
        Errors errors = authCodeEmailValidator.validate("OE000001", "", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.NOT_EMPTY_ERROR_MESSAGE, AuthCodeEmailValidator.AUTH_CODE_FIELD);
        assertError(AuthCodeEmailValidator.AUTH_CODE_FIELD, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenBlankAuthCode() {
        Errors errors = authCodeEmailValidator.validate("OE000001", " ", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.NOT_EMPTY_ERROR_MESSAGE, AuthCodeEmailValidator.AUTH_CODE_FIELD);
        assertError(AuthCodeEmailValidator.AUTH_CODE_FIELD, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenShortAuthCode() {
        Errors errors = authCodeEmailValidator.validate("OE000001", "A1B2C", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.INCORRECT_LENGTH_ERROR_MESSAGE, AuthCodeEmailValidator.AUTH_CODE_FIELD, 6);
        assertError(AuthCodeEmailValidator.AUTH_CODE_FIELD, validationMessage, errors);
    }

    @Test
    void testErrorsReportedWhenLongAuthCode() {
        Errors errors = authCodeEmailValidator.validate("OE0000011", "A1B2C3D4", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.INCORRECT_LENGTH_ERROR_MESSAGE, AuthCodeEmailValidator.AUTH_CODE_FIELD, 6);
        assertError(AuthCodeEmailValidator.AUTH_CODE_FIELD, validationMessage, errors);
    }

    @Test
    void testErrorsReportedInvalidInvalidAuthCode() {
        Errors errors = authCodeEmailValidator.validate("OE000001", "a1b2c3", new Errors(), CONTEXT);
        String validationMessage = String.format(ValidationUtils.INVALID_CHARACTERS_ERROR_MESSAGE, AuthCodeEmailValidator.AUTH_CODE_FIELD, AuthCodeEmailValidator.AUTH_CODE_REGEX);
        assertError(AuthCodeEmailValidator.AUTH_CODE_FIELD, validationMessage, errors);
    }

    private void assertError(String fieldName, String message, Errors errors) {
        Err err = Err.invalidBodyBuilderWithLocation(fieldName).withError(message).build();
        assertTrue(errors.containsError(err));
    }
}
