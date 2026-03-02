package uk.gov.companieshouse.authcodenotification.email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailData {

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("to")
    private String to;

    @JsonProperty("auth_code")
    private String authCode;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    public EmailData(String subject, String to, String authCode, String companyName, String companyNumber) {
        this.subject = subject;
        this.to = to;
        this.authCode = authCode;
        this.companyName = companyName;
        this.companyNumber = companyNumber;
    }

    public String getSubject() {
        return subject;
    }

    public String getTo() {
        return to;
    }

    public String getAuthCode() {
        return authCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

}
