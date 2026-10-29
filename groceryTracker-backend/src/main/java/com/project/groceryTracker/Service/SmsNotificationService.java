package com.project.groceryTracker.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SmsNotificationService {

    private final String accountSid;
    private final String authToken;
    private final String twilioPhoneNumber;

    public SmsNotificationService(
            @Value("${twilio.account-sid:}") String accountSid,
            @Value("${twilio.auth-token:}") String authToken,
            @Value("${twilio.phone-number:}") String twilioPhoneNumber) {

        this.accountSid = accountSid;
        this.authToken = authToken;
        this.twilioPhoneNumber = twilioPhoneNumber;

        if (isConfigured()) {
            Twilio.init(accountSid, authToken);
        }
    }

    public boolean isConfigured() {
        return StringUtils.hasText(accountSid)
                && StringUtils.hasText(authToken)
                && isValidPhoneNumber(twilioPhoneNumber);
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null
                && phoneNumber.matches("^\\+[1-9]\\d{7,14}$");
    }

    public void sendSms(String phoneNumber, String message) {
        if (!isConfigured()) {
            throw new IllegalStateException(
                    "Twilio SMS is not configured"
            );
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException(
                    "Phone number must use international E.164 format"
            );
        }
        if (!StringUtils.hasText(message)) {
            throw new IllegalArgumentException(
                    "SMS message cannot be empty"
            );
        }

        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                message
        ).create();
    }
}
