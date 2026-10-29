package com.project.groceryTracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String message;

    private String type;

    private boolean readStatus = false;

    private LocalDateTime createdAt;

    @Column(unique = true)
    private String eventKey;

    private LocalDate expiryDate;

    private String emailDeliveryStatus = "PENDING";

    private String smsDeliveryStatus = "PENDING";

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private InventoryItem inventoryItem;
}
