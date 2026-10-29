package com.project.groceryTracker.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService {

    private final String accountSid =
            System.getenv("TWILIO_ACCOUNT_SID");

    private final String authToken =
            System.getenv("TWILIO_AUTH_TOKEN");

    private final String twilioPhoneNumber =
            System.getenv("TWILIO_PHONE_NUMBER");

    public SmsNotificationService() {

        Twilio.init(accountSid, authToken);
    }

    public void sendSms(String phoneNumber, String message) {

        Message sms = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                "sms_appointment_reminders"
        ).create();
        System.out.println("Message SID: " + sms.getSid());
        System.out.println("Message Status: " + sms.getStatus());
    }
}