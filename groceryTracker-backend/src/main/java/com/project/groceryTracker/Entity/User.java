package com.project.groceryTracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNum;
    @JsonIgnore
    private String password;
    @Column(nullable = false)
    private boolean notificationsEnabled = true;

    @Column(nullable = false)
    private int expiryRemainderDays = 7;
}