package uk.gov.companieshouse.authcodenotification.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendEmailRequestDto {
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    @JsonProperty("auth_code")
    private String authCode;
}
