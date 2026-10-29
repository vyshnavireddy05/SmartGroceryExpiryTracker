package com.project.groceryTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyReportDTO {

    private String month;
    private double spending;
    private double waste;
}