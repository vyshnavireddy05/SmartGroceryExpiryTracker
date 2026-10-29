package com.project.groceryTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportSummaryDTO {
    private double totalSpending;
    private double expiredWaste;
    private double expiringValue;
    private double freshInventoryValue;
    private double wastePercentage;
    private double potentialSavings;
}
