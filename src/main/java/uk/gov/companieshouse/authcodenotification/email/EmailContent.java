package uk.gov.companieshouse.authcodenotification.email;

import java.time.LocalDateTime;
import java.util.Map;

public class EmailContent {
    public static final class Builder {
        private String originatingAppId;
        private String messageId;
        private String messageType;
        private Map<String, Object> data;
        private String emailAddress;
        private LocalDateTime createdAt;

        public Builder withOriginatingAppId(String val) {
            originatingAppId = val;
            return this;
        }

        public Builder withMessageId(String val) {
            messageId = val;
            return this;
        }

        public Builder withMessageType(String val) {
            messageType = val;
            return this;
        }

        public Builder withData(Map<String, Object> val) {
            data = val;
            return this;
        }

        public Builder withEmailAddress(String val) {
            emailAddress = val;
            return this;
        }

        public Builder withCreatedAt(LocalDateTime val) {
            createdAt = val;
            return this;
        }

        public EmailContent build() {
            return new EmailContent(this);
        }
    }

    private final String originatingAppId;
    private final String messageId;
    private final String messageType;
    private final Map<String, Object> data;
    private final String emailAddress;
    private final LocalDateTime createdAt;

    private EmailContent(Builder builder) {
        this.originatingAppId = builder.originatingAppId;
        this.messageId = builder.messageId;
        this.messageType = builder.messageType;
        this.data = builder.data;
        this.emailAddress = builder.emailAddress;
        this.createdAt = builder.createdAt;
    }

    public String getOriginatingAppId() {
        return originatingAppId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
