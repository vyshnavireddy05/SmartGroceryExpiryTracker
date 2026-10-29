package com.project.groceryTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNum;
    private boolean notificationsEnabled;
    private int expiryRemainderDays;
}
