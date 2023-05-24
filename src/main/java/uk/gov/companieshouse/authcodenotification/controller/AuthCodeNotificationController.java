package uk.gov.companieshouse.authcodenotification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import uk.gov.companieshouse.authcodenotification.utils.DataSanitisation;
import uk.gov.companieshouse.authcodenotification.utils.Encrypter;
import uk.gov.companieshouse.logging.util.DataMap;

import static uk.gov.companieshouse.authcodenotification.utils.Constants.ERIC_REQUEST_ID_KEY;

@RestController
@RequestMapping("/company/{companyNumber}/auth-code")
public class AuthCodeNotificationController {

    private final AuthCodeNotificationService authCodeNotificationService;
    private final DataSanitisation dataSanitisation;
    
    @Value("${AUTH_CODE_ENCRYPT_KEY}")
    private String aesKeyString;

    @Autowired
    public AuthCodeNotificationController(AuthCodeNotificationService authCodeNotificationService,
                                          DataSanitisation dataSanitisation) {
        this.authCodeNotificationService = authCodeNotificationService;
        this.dataSanitisation = dataSanitisation;
    }

    @PostMapping("/send-email")
    public ResponseEntity<Object> sendEmail(@RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId,
                                            @RequestBody SendEmailRequestDto sendEmailRequestDto,
                                            @PathVariable String companyNumber) {

        companyNumber = dataSanitisation.makeStringSafeForLogging(companyNumber);
        var logDataMap = new DataMap.Builder().companyNumber(companyNumber).build();
        ApiLogger.infoContext(requestId,"Request received for auth code email", logDataMap.getLogMap());

       
       byte[] key = null;
       String authCodeEcrypted = null;

       try{
            key = aesKeyString.getBytes("UTF-8");
            authCodeEcrypted = Encrypter.encrypt(sendEmailRequestDto.getAuthCode(), key);
       }
       catch(Exception e){
        //todo deal with this
       }

       ApiLogger.infoContext(requestId, "Succesfully encrypted auth code: " + authCodeEcrypted , logDataMap.getLogMap());

        try {
            authCodeNotificationService.sendAuthCodeEmail(requestId, authCodeEcrypted, companyNumber);
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    
   
}
