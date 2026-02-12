package com.phs.ewallet.service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.phs.ewallet.dto.ExpenseDTO;
import com.phs.ewallet.entity.Category;
import com.phs.ewallet.entity.Expense;
import com.phs.ewallet.entity.Profile;
import com.phs.ewallet.repository.CategoryRepo;
import com.phs.ewallet.repository.ExpenseRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepo categoryRepo;

    private final ExpenseRepo expenseRepo;

    private final ProfileService profileService;

    //add
    public ExpenseDTO addExpenseDTO(ExpenseDTO expenseDTO) {
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepo.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Expense newExpense = toEntity(expenseDTO, profile, category);
        newExpense = expenseRepo.save(newExpense);
        return toDTO(newExpense);
    }

    //retrieves all expenses for current month/base on start date and end date
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentProfile() {
        Profile profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Expense> expenses = expenseRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return expenses.stream().map(this::toDTO).toList();
    }

    //delete expense by id for current profile
    public void deleteExpense(Long expenseId) {
        Profile profile = profileService.getCurrentProfile();
        Expense expense = expenseRepo.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expense.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepo.delete(expense);
    }

    //get latest 5 expenses for current profile
    public List<ExpenseDTO> getLatest5ExpensesForCurrentProfile() {
        Profile profile = profileService.getCurrentProfile();
        List<Expense> expenses = expenseRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return expenses.stream().map(this::toDTO).toList();
    }

    //get total expenses for current profile
    public BigDecimal getTotalExpensesForCurrentProfile() {
        Profile profile = profileService.getCurrentProfile();
        BigDecimal totalExpenses = expenseRepo.findTotalExpenseByProfileId(profile.getId());
        return totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
    }

    public BigDecimal getCurrentMonthExpenseForCurrentProfile() {
        Profile profile = profileService.getCurrentProfile();
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        BigDecimal currentMonthExpense = expenseRepo.findTotalExpenseByProfileIdAndDateAfter(profile.getId(), startOfMonth);
        return currentMonthExpense != null ? currentMonthExpense : BigDecimal.ZERO;
    }

    public BigDecimal getExpenseByMonthForCurrentProfile(LocalDate monthDate) {
        Profile profile = profileService.getCurrentProfile();
        LocalDate startOfMonth = monthDate.withDayOfMonth(1);
        LocalDate endOfMonth = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
        BigDecimal monthlyExpense = expenseRepo.findTotalExpenseByProfileIdAndDateBetween(profile.getId(), startOfMonth, endOfMonth);
        return monthlyExpense != null ? monthlyExpense : BigDecimal.ZERO;
    }

    public int getExpenseCountByMonthForCurrentProfile(LocalDate monthDate) {
        Profile profile = profileService.getCurrentProfile();
        LocalDate startOfMonth = monthDate.withDayOfMonth(1);
        LocalDate endOfMonth = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
        Long count = expenseRepo.countExpenseByProfileIdAndDateBetween(profile.getId(), startOfMonth, endOfMonth);
        return count != null ? count.intValue() : 0;
    }

    //filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        Profile profile = profileService.getCurrentProfile();
        List<Expense> expenses = expenseRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return expenses.stream().map(this::toDTO).toList();
    }

    //notifications
    public List<ExpenseDTO> getExpensesForProfileOnDate(Long profileId, LocalDate date) {
        List<Expense> expenses = expenseRepo.findByProfileIdAndDate(profileId, date);
        return expenses.stream().map(this::toDTO).toList();
    }

    //=====================================================//
    //helper method
    private Expense toEntity(ExpenseDTO expenseDTO, Profile profile, Category category) {
        return Expense.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : "N/A")
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
