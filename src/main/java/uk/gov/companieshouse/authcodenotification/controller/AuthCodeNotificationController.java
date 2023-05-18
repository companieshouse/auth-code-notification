package uk.gov.companieshouse.authcodenotification.controller;

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
import uk.gov.companieshouse.logging.util.DataMap;

import static uk.gov.companieshouse.authcodenotification.utils.Constants.ERIC_REQUEST_ID_KEY;

@RestController
@RequestMapping("/company/{companyNumber}/auth-code")
public class AuthCodeNotificationController {

    private final AuthCodeNotificationService authCodeNotificationService;
    private final DataSanitiser dataSanitiser;

    @Autowired
    public AuthCodeNotificationController(AuthCodeNotificationService authCodeNotificationService,
                                          DataSanitiser dataSanitiser) {
        this.authCodeNotificationService = authCodeNotificationService;
        this.dataSanitiser = dataSanitiser;
    }

    @PostMapping("/send-email")
    public ResponseEntity<Object> sendEmail(@RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId,
                                            @RequestBody SendEmailRequestDto sendEmailRequestDto,
                                            @PathVariable String companyNumber) {

        companyNumber = dataSanitiser.makeStringSafe(companyNumber);
        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        ApiLogger.infoContext(requestId,"Request received for auth code email", logDataMap.getLogMap());

        try {
            var authCode = dataSanitiser.makeStringSafe(sendEmailRequestDto.getAuthCode());
            authCodeNotificationService.sendAuthCodeEmail(requestId, authCode, companyNumber);
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
