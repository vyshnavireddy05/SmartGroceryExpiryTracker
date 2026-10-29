package com.project.groceryTracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="inventory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemName;
    private String category;
    private Integer price;
    private Integer quantity;
    private String unit;
    private LocalDate purchasedDate;
    private LocalDate expiryDate;
    private String storageLocation;

    @Column(length=1000)
    private String notes;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

}
