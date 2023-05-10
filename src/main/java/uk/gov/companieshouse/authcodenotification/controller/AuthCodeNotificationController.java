package uk.gov.companieshouse.authcodenotification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;
import uk.gov.companieshouse.authcodenotification.service.AuthCodeNotificationService;
import uk.gov.companieshouse.authcodenotification.utils.ApiLogger;
import uk.gov.companieshouse.authcodenotification.utils.DataSanitisation;
import uk.gov.companieshouse.logging.util.DataMap;

import static uk.gov.companieshouse.authcodenotification.utils.Constants.ERIC_REQUEST_ID_KEY;

@RestController
@RequestMapping("/company/{companyNumber}/auth-code")
public class AuthCodeNotificationController {

    private AuthCodeNotificationService authCodeNotificationService;
    private DataSanitisation dataSanitisation;

    @Autowired
    public AuthCodeNotificationController(AuthCodeNotificationService authCodeNotificationService,
                                          DataSanitisation dataSanitisation) {
        this.authCodeNotificationService = authCodeNotificationService;
        this.dataSanitisation = dataSanitisation;
    }

    @PostMapping("/send-email")
    public ResponseEntity<Object> sendEmail(@RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId,
                                            @PathVariable String companyNumber) {

        companyNumber = dataSanitisation.makeStringSafeForLogging(companyNumber);
        DataMap dataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        ApiLogger.infoContext(requestId,"Request received for auth code email", dataMap.getLogMap());

        try {
            authCodeNotificationService.sendAuthCodeEmail(requestId, companyNumber);
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
