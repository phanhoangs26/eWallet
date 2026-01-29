package com.phs.ewallet.service;

import com.phs.ewallet.dto.IncomeDTO;
import com.phs.ewallet.entity.Category;
import com.phs.ewallet.entity.Income;
import com.phs.ewallet.entity.Profile;
import com.phs.ewallet.repository.CategoryRepo;
import com.phs.ewallet.repository.IncomeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepo categoryRepo;

    private final IncomeRepo incomeRepo;

    private final ProfileService profileService;

    //add
    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepo.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Income newIcome = toEntity(incomeDTO, profile, category);
        newIcome = incomeRepo.save(newIcome);
        return toDTO(newIcome);
    }

    //retrieves all incomes for current month/base on start date and end date
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentProfile() {
        Profile profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Income> incomes = incomeRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return incomes.stream().map(this::toDTO).toList();
    }

    //delete income by id for current profile
    public void deleteIncome(Long incomeId) {
        Profile profile = profileService.getCurrentProfile();
        Income income = incomeRepo.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if (!income.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete this income");
        }
    }

    //get latest 5 incomes for current profile
    public List<IncomeDTO> getLatest5IncomesForCurrentProfile() {
        Profile profile = profileService.getCurrentProfile();
        List<Income> expenses = incomeRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return expenses.stream().map(this::toDTO).toList();
    }

    //get total incomes for current profile
    public BigDecimal getTotalIncomesForCurrentProfile() {
        Profile profile = profileService.getCurrentProfile();
        BigDecimal totalIncomes = incomeRepo.findTotalIncomeByProfileId(profile.getId());
        return totalIncomes != null ? totalIncomes : BigDecimal.ZERO;
    }

    //filter incomes
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        Profile profile = profileService.getCurrentProfile();
        List<Income> expenses = incomeRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return expenses.stream().map(this::toDTO).toList();
    }

    //notifications
    public List<IncomeDTO> getIncomesForProfileOnDate(Long profileId, LocalDate date) {
        List<Income> expenses = incomeRepo.findByProfileIdAndDate(profileId, date);
        return expenses.stream().map(this::toDTO).toList();
    }

    //=====================================================//
    //helper method
    public Income toEntity(IncomeDTO incomeDTO, Profile profile, Category category) {
        return Income.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    public IncomeDTO toDTO(Income income) {
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .amount(income.getAmount())
                .date(income.getDate())
                .categoryId(income.getCategory() != null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory().getName() != null ? income.getCategory().getName() : "N/A")
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }

}
