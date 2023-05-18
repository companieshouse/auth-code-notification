package uk.gov.companieshouse.authcodenotification.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendEmailRequestDto {

    public static final String AUTH_CODE_FIELD = "auth_code";

    @JsonProperty(AUTH_CODE_FIELD)
    private String authCode;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

}
