package com.project.groceryTracker.Service;

public interface NotificationService {
    void sendEmail(String email, String message);
    void sendSms(String phoneNumber, String message);
}
