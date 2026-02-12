package com.phs.ewallet.service;

import com.phs.ewallet.dto.CategoryDTO;
import com.phs.ewallet.entity.Category;
import com.phs.ewallet.entity.Profile;
import com.phs.ewallet.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepo categoryRepo;

    //save
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        Profile profile = profileService.getCurrentProfile();
        if (categoryRepo.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");

        }
        // Chỉ chạy đến đây khi không có trùng lặp
        Category newCategory = toEntity(categoryDTO, profile);
        newCategory = categoryRepo.save(newCategory);
        return toDTO(newCategory);
    }

    //get cate for current user
    public List<CategoryDTO> getCategoriesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        List<Category> categories = categoryRepo.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    //get Cate by Type for current user
    public List<CategoryDTO> getCategoriesByTpeForCurrentUser(String type) {
        Profile profile = profileService.getCurrentProfile();
        List<Category> categories = categoryRepo.findByTypeAndProfileId(type, profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    //update category
    public CategoryDTO updaCategory(Long categoryId, CategoryDTO categoryDTO) {
        Profile profile = profileService.getCurrentProfile();
        Category exsCategory = categoryRepo.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        exsCategory.setName(categoryDTO.getName());
        exsCategory.setIcon(categoryDTO.getIcon());
        exsCategory.setType(categoryDTO.getType());
        exsCategory = categoryRepo.save(exsCategory);
        return toDTO(exsCategory);
    }

    //helper method
    private Category toEntity(CategoryDTO categoryDTO, Profile profile) {
        return Category.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .profileId(category.getProfile().getId())
                .name(category.getName())
                .icon(category.getIcon())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .type(category.getType())
                .build();
    }
}
