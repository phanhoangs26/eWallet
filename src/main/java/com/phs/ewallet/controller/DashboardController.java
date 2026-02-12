package com.phs.ewallet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phs.ewallet.dto.DashboardStatsDTO;
import com.phs.ewallet.dto.DashboardSummaryDTO;
import com.phs.ewallet.dto.RecentTransactionDTO;
import com.phs.ewallet.dto.MonthlyTrendDTO;
import com.phs.ewallet.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/recent-transactions")
    public ResponseEntity<List<RecentTransactionDTO>> getRecentTransactions() {
        List<RecentTransactionDTO> transactions = dashboardService.getRecentTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<List<MonthlyTrendDTO>> getMonthlyTrends() {
        List<MonthlyTrendDTO> trends = dashboardService.getMonthlyTrends();
        return ResponseEntity.ok(trends);
    }
}
