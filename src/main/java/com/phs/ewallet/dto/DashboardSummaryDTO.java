package com.phs.ewallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardSummaryDTO {
    private double thisMonthIncome;
    private double thisMonthExpense;
}
