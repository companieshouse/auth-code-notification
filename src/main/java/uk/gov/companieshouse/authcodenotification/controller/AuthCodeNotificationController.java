package uk.gov.companieshouse.authcodenotification.controller;

import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.service.AuthCodeNotificationService;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.authcodenotification.model.SendEmailRequestDto;
import uk.gov.companieshouse.authcodenotification.utils.DataSanitiser;
import uk.gov.companieshouse.authcodenotification.utils.Encrypter;
import uk.gov.companieshouse.authcodenotification.validation.AuthCodeEmailValidator;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.service.rest.err.Errors;
import uk.gov.companieshouse.service.rest.response.ChResponseBody;

import static uk.gov.companieshouse.authcodenotification.utils.Constants.ERIC_REQUEST_ID_KEY;

@RestController
@RequestMapping("/internal/company/{companyNumber}/auth-code")
public class AuthCodeNotificationController {

    private static final String VALIDATION_ERRORS_MESSAGE = "Validation errors : %s";

    private final AuthCodeNotificationService authCodeNotificationService;

    private final AuthCodeEmailValidator authCodeEmailValidator;
    private final DataSanitiser dataSanitiser;

    private final Encrypter encrypter;

    @Autowired
    public AuthCodeNotificationController(AuthCodeNotificationService authCodeNotificationService,
                                          AuthCodeEmailValidator authCodeEmailValidator,
                                          DataSanitiser dataSanitiser,
                                          Encrypter encrypter) {
        this.authCodeNotificationService = authCodeNotificationService;
        this.authCodeEmailValidator = authCodeEmailValidator;
        this.dataSanitiser = dataSanitiser;
        this.encrypter = encrypter;
    }

    @PostMapping("/send-email")
    public ResponseEntity<Object> sendEmail(@RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId,
                                            @RequestBody SendEmailRequestDto sendEmailRequestDto,
                                            @PathVariable String companyNumber) {

        companyNumber = dataSanitiser.makeStringSafe(companyNumber);
        var authCode = dataSanitiser.makeStringSafe(sendEmailRequestDto.getAuthCode());

        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        var logMap = logDataMap.getLogMap();
        ApiLogger.infoContext(requestId, "Request received for auth code email", logMap);

        // validate request data
        var validationErrors =  authCodeEmailValidator.validate(companyNumber, authCode, new Errors(), requestId);
        if (validationErrors.hasErrors()) {
            ApiLogger.errorContext(requestId, String.format(VALIDATION_ERRORS_MESSAGE,
                    convertErrorsToJsonString(validationErrors)), null, logMap);
            var responseBody = ChResponseBody.createErrorsBody(validationErrors);
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        // encrypt auth code
        String encryptedAuthCode;
        try {
            encryptedAuthCode = encrypter.encrypt(authCode);
            if (StringUtils.isBlank(encryptedAuthCode)) {
                ApiLogger.errorContext(requestId, "Encrypted auth code is blank", null, logMap);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            ApiLogger.infoContext(requestId, "Successfully encrypted auth code to: " + encryptedAuthCode, logMap);
        } catch (Exception e) {
            ApiLogger.errorContext(requestId, "Failed to encrypt auth code", e, logMap);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // send email
        try {
            authCodeNotificationService.sendAuthCodeEmail(requestId, encryptedAuthCode, companyNumber);
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String convertErrorsToJsonString(Errors validationErrors) {
        var gson = new GsonBuilder().create();
        return gson.toJson(validationErrors);
    }
}
