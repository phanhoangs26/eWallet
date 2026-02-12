package com.phs.ewallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsDTO {
    private double totalIncome;
    private double totalExpense;
    private double balance;
}
