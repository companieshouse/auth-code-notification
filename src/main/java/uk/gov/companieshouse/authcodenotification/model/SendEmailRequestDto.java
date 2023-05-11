package uk.gov.companieshouse.authcodenotification.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendEmailRequestDto {

    @JsonProperty("auth_code")
    private String authCode;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

}
