package uk.gov.companieshouse.authcodenotification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.authcodenotification.exception.ServiceException;

@Service
public class AuthCodeNotificationService {

    @Autowired
    private PrivateDataRetrievalService privateDataRetrievalService;

    public void sendAuthCodeEmail(String companyNumber) throws ServiceException {
        // todo add some logging
        String email = getOverseasEntityEmail(companyNumber);

        // 2. send email
    }

    private String getOverseasEntityEmail(String companyNumber) throws ServiceException {
        return privateDataRetrievalService.getOverseasEntityData(companyNumber).getEmail();
    }

}
