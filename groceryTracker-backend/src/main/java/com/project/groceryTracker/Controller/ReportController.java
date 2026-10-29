
package com.project.groceryTracker.Controller;

import com.project.groceryTracker.DTO.*;
import com.project.groceryTracker.Service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<ReportInventoryDTO>> getReportInventory(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                reportService.getInventoryReport(
                        userDetails.getUsername()
                )
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryDTO> getReportSummary(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                reportService.getReportSummary(
                        userDetails.getUsername()
                )
        );
    }

    @GetMapping("/waste-by-category")
    public ResponseEntity<List<CategoryWasteDTO>> getWasteByCategory(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                reportService.getWasteByCategory(
                        userDetails.getUsername()
                )
        );
    }

    @GetMapping("/most-wasted")
    public ResponseEntity<List<WastedItemDTO>> getMostWastedItems(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                reportService.getMostWastedItems(
                        userDetails.getUsername()
                )
        );
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyReportDTO>> getMonthlyReport(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                reportService.getMonthlyReport(
                        userDetails.getUsername()
                )
        );
    }

    @GetMapping("/inventory-status")
    public ResponseEntity<List<InventoryStatusDTO>> getInventoryStatus(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                reportService.getInventoryStatus(
                        userDetails.getUsername()
                )
        );
    }
}