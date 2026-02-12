package com.phs.ewallet.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.phs.ewallet.dto.*;
import org.springframework.stereotype.Service;

import com.phs.ewallet.entity.Profile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        Profile profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();

        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentProfile();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentProfile();

        List<RecentTransactionDTO> recentTransactions =
                Stream.concat(
                                latestIncomes.stream().map(income -> RecentTransactionDTO.builder()
                                        .id(income.getId())
                                        .profileId(profile.getId())
                                        .name(income.getName())
                                        .icon(income.getIcon())
                                        .amount(income.getAmount())
                                        .date(income.getDate())
                                        .createdAt(income.getCreatedAt())
                                        .updatedAt(income.getUpdatedAt())
                                        .type("income")
                                        .build()
                                ),
                                latestExpenses.stream().map(expense -> RecentTransactionDTO.builder()
                                        .id(expense.getId())
                                        .profileId(profile.getId())
                                        .name(expense.getName())
                                        .icon(expense.getIcon())
                                        .amount(expense.getAmount())
                                        .date(expense.getDate())
                                        .createdAt(expense.getCreatedAt())
                                        .updatedAt(expense.getUpdatedAt())
                                        .type("expense")
                                        .build()
                                )
                        )
                        .sorted((a, b) -> {
                            int cmp = b.getDate().compareTo(a.getDate());
                            if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                                return b.getCreatedAt().compareTo(a.getCreatedAt());
                            }
                            return cmp;
                        })
                        .collect(Collectors.toList());

        returnValue.put("totalBalance", incomeService.getTotalIncomesForCurrentProfile().subtract(expenseService.getTotalExpensesForCurrentProfile())
        );

        returnValue.put("totalIncome", incomeService.getTotalIncomesForCurrentProfile());
        returnValue.put("totalExpenses", expenseService.getTotalExpensesForCurrentProfile());
        returnValue.put("recent5Expenses", latestExpenses);
        returnValue.put("recent5Incomes", latestIncomes);
        returnValue.put("recentTransactions", recentTransactions);

        return returnValue;
    }

    public DashboardStatsDTO getDashboardStats() {
        Profile profile = profileService.getCurrentProfile();
        BigDecimal totalIncome = incomeService.getTotalIncomesForCurrentProfile();
        BigDecimal totalExpense = expenseService.getTotalExpensesForCurrentProfile();
        BigDecimal balance = totalIncome.subtract(totalExpense);

        return new DashboardStatsDTO(
            totalIncome.doubleValue(),
            totalExpense.doubleValue(),
            balance.doubleValue()
        );
    }

    public DashboardSummaryDTO getDashboardSummary() {
        BigDecimal thisMonthIncome = incomeService.getCurrentMonthIncomeForCurrentProfile();
        BigDecimal thisMonthExpense = expenseService.getCurrentMonthExpenseForCurrentProfile();

        return new DashboardSummaryDTO(
            thisMonthIncome.doubleValue(),
            thisMonthExpense.doubleValue()
        );
    }

    public List<RecentTransactionDTO> getRecentTransactions() {
        Profile profile = profileService.getCurrentProfile();

        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentProfile();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentProfile();

        return Stream.concat(
            latestIncomes.stream().map(income -> RecentTransactionDTO.builder()
                .id(income.getId())
                .profileId(profile.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .type("income")
                .build()),
            latestExpenses.stream().map(expense -> RecentTransactionDTO.builder()
                .id(expense.getId())
                .profileId(profile.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .type("expense")
                .build())
        )
        .sorted((a, b) -> {
            int cmp = b.getDate().compareTo(a.getDate());
            if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return cmp;
        })
        .collect(Collectors.toList());
    }

    public List<MonthlyTrendDTO> getMonthlyTrends() {
        List<MonthlyTrendDTO> trends = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = currentDate.minusMonths(i);
            String month = monthDate.toString();
            
            BigDecimal monthlyIncome = incomeService.getIncomeByMonthForCurrentProfile(monthDate);
            BigDecimal monthlyExpense = expenseService.getExpenseByMonthForCurrentProfile(monthDate);
            
            int incomeCount = incomeService.getIncomeCountByMonthForCurrentProfile(monthDate);
            int expenseCount = expenseService.getExpenseCountByMonthForCurrentProfile(monthDate);
            
            double averageIncome = incomeCount > 0 ? monthlyIncome.doubleValue() / incomeCount : 0;
            double averageExpense = expenseCount > 0 ? monthlyExpense.doubleValue() / expenseCount : 0;
            
            trends.add(MonthlyTrendDTO.builder()
                .month(month)
                .income(monthlyIncome.doubleValue())
                .expense(monthlyExpense.doubleValue())
                .incomeCount(incomeCount)
                .expenseCount(expenseCount)
                .averageIncome(averageIncome)
                .averageExpense(averageExpense)
                .build());
        }
        
        return trends;
    }
}
