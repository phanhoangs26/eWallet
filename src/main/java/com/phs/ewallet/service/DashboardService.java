package com.phs.ewallet.service;

import com.phs.ewallet.dto.ExpenseDTO;
import com.phs.ewallet.dto.IncomeDTO;
import com.phs.ewallet.dto.RecentTransactionDTO;
import com.phs.ewallet.entity.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
}
