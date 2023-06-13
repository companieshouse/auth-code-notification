package uk.gov.companieshouse.authcodenotification.controller;

import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.authcodenotification.exception.EntityNotFoundException;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.service.AuthCodeNotificationService;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.authcodenotification.model.SendEmailRequestDto;
import uk.gov.companieshouse.authcodenotification.utils.DataSanitiser;
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

    @Autowired
    public AuthCodeNotificationController(AuthCodeNotificationService authCodeNotificationService,
                                          AuthCodeEmailValidator authCodeEmailValidator,
                                          DataSanitiser dataSanitiser) {
        this.authCodeNotificationService = authCodeNotificationService;
        this.authCodeEmailValidator = authCodeEmailValidator;
        this.dataSanitiser = dataSanitiser;
    }

    @PostMapping("/send-email")
    public ResponseEntity<Object> sendEmail(@RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId,
                                            @RequestBody SendEmailRequestDto sendEmailRequestDto,
                                            @PathVariable String companyNumber) {

        companyNumber = dataSanitiser.makeStringSafe(companyNumber);
        var authCode = dataSanitiser.makeStringSafe(sendEmailRequestDto.getAuthCode());

        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        ApiLogger.infoContext(requestId,"Request received for auth code email", logDataMap.getLogMap());

        var validationErrors =  authCodeEmailValidator.validate(companyNumber, authCode, new Errors(), requestId);
        if (validationErrors.hasErrors()) {
            ApiLogger.errorContext(requestId, String.format(VALIDATION_ERRORS_MESSAGE,
                    convertErrorsToJsonString(validationErrors)), null, logDataMap.getLogMap());
            var responseBody = ChResponseBody.createErrorsBody(validationErrors);
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        try {
            authCodeNotificationService.sendAuthCodeEmail(requestId, authCode, companyNumber);
        } catch (EntityNotFoundException nfe) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
