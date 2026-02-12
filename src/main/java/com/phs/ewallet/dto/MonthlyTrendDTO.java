package com.phs.ewallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyTrendDTO {
    private String month;
    private double income;
    private double expense;
    private int incomeCount;
    private int expenseCount;
    private double averageIncome;
    private double averageExpense;
}
