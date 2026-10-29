
package com.project.groceryTracker.Service;

import com.project.groceryTracker.DTO.*;
import com.project.groceryTracker.Entity.InventoryItem;
import com.project.groceryTracker.Entity.User;
import com.project.groceryTracker.Repository.InventoryRepo;
import com.project.groceryTracker.Repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReportService {

    private final InventoryRepo inventoryRepo;
    private final UserRepo userRepo;
    private final RecipeService recipeService;

    public ReportService(
            InventoryRepo inventoryRepo,
            UserRepo userRepo,
            RecipeService recipeService) {
        this.inventoryRepo = inventoryRepo;
        this.userRepo = userRepo;
        this.recipeService = recipeService;
    }

    private User getUserByEmail(String email) {
        return userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));
    }

    public List<ReportInventoryDTO> getInventoryReport(String email) {

        User user = getUserByEmail(email);

        List<InventoryItem> items =
                inventoryRepo.findAllByUser_Id(user.getId());

        return items.stream()
                .map(item -> new ReportInventoryDTO(
                        item.getId(),
                        item.getItemName(),
                        item.getCategory(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getPurchasedDate(),
                        item.getExpiryDate(),
                        item.getStorageLocation(),
                        item.getNotes(),
                        recipeService.calculateStatus(item.getExpiryDate())
                ))
                .toList();
    }

    public ReportSummaryDTO getReportSummary(String email) {

        List<ReportInventoryDTO> items = getInventoryReport(email);

        double totalSpending = 0;
        double expiredWaste = 0;
        double expiringValue = 0;
        double freshInventoryValue = 0;

        for (ReportInventoryDTO item : items) {
            double itemValue =
                    (double) item.getPrice() * item.getQuantity();

            totalSpending += itemValue;

            if ("EXPIRED".equals(item.getStatus())) {
                expiredWaste += itemValue;
            } else if ("EXPIRING".equals(item.getStatus())) {
                expiringValue += itemValue;
            } else if ("FRESH".equals(item.getStatus())) {
                freshInventoryValue += itemValue;
            }
        }

        double wastePercentage = totalSpending > 0
                ? (expiredWaste / totalSpending) * 100
                : 0;

        return new ReportSummaryDTO(
                totalSpending,
                expiredWaste,
                expiringValue,
                freshInventoryValue,
                wastePercentage,
                expiringValue
        );
    }

    public List<CategoryWasteDTO> getWasteByCategory(String email) {

        List<ReportInventoryDTO> items = getInventoryReport(email);

        return items.stream()
                .filter(item -> "EXPIRED".equals(item.getStatus()))
                .collect(
                        java.util.stream.Collectors.groupingBy(
                                ReportInventoryDTO::getCategory,
                                java.util.stream.Collectors.summingDouble(
                                        item -> (double) item.getPrice()
                                                * item.getQuantity()
                                )
                        )
                )
                .entrySet()
                .stream()
                .map(entry -> new CategoryWasteDTO(
                        entry.getKey(),
                        entry.getValue()
                ))
                .sorted(
                        java.util.Comparator.comparingDouble(
                                CategoryWasteDTO::getWastedValue
                        ).reversed()
                )
                .toList();
    }

    public List<WastedItemDTO> getMostWastedItems(String email) {

        List<ReportInventoryDTO> items = getInventoryReport(email);

        return items.stream()
                .filter(item -> "EXPIRED".equals(item.getStatus()))
                .map(item -> new WastedItemDTO(
                        item.getId(),
                        item.getItemName(),
                        item.getCategory(),
                        item.getQuantity(),
                        (double) item.getPrice() * item.getQuantity()
                ))
                .sorted(
                        java.util.Comparator.comparingDouble(
                                WastedItemDTO::getWastedValue
                        ).reversed()
                )
                .limit(5)
                .toList();
    }

    public List<MonthlyReportDTO> getMonthlyReport(String email) {

        List<ReportInventoryDTO> items = getInventoryReport(email);

        return items.stream()
                .filter(item -> item.getPurchasedDate() != null)
                .collect(
                        java.util.stream.Collectors.groupingBy(
                                item -> item.getPurchasedDate()
                                        .withDayOfMonth(1)
                        )
                )
                .entrySet()
                .stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(entry -> {

                    double spending = 0;
                    double waste = 0;

                    for (ReportInventoryDTO item : entry.getValue()) {
                        double itemValue =
                                (double) item.getPrice() * item.getQuantity();

                        spending += itemValue;

                        if ("EXPIRED".equals(item.getStatus())) {
                            waste += itemValue;
                        }
                    }

                    String month = entry.getKey().getMonth()
                            .toString().charAt(0)
                            + entry.getKey().getMonth()
                            .toString().substring(1).toLowerCase()
                            + " "
                            + entry.getKey().getYear();

                    return new MonthlyReportDTO(
                            month,
                            spending,
                            waste
                    );
                })
                .toList();
    }

    public List<InventoryStatusDTO> getInventoryStatus(String email) {

        List<ReportInventoryDTO> items = getInventoryReport(email);

        double freshValue = 0;
        double expiringValue = 0;
        double expiredValue = 0;

        for (ReportInventoryDTO item : items) {

            double itemValue =
                    (double) item.getPrice() * item.getQuantity();

            if ("FRESH".equals(item.getStatus())) {
                freshValue += itemValue;
            } else if ("EXPIRING".equals(item.getStatus())) {
                expiringValue += itemValue;
            } else if ("EXPIRED".equals(item.getStatus())) {
                expiredValue += itemValue;
            }
        }

        return List.of(
                new InventoryStatusDTO("Fresh", freshValue),
                new InventoryStatusDTO("Expiring Soon", expiringValue),
                new InventoryStatusDTO("Expired", expiredValue)
        );
    }
}