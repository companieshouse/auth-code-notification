package uk.gov.companieshouse.authcodenotification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AuthCodeNotificationController {

    public ResponseEntity<Object> sendEmail() {

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
