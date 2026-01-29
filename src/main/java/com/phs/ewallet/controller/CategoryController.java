package com.phs.ewallet.controller;

import com.phs.ewallet.dto.CategoryDTO;
import com.phs.ewallet.service.CategoryService;
import com.phs.ewallet.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProfileService profileService;


    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryDTO);
    }

    //get Cate for current user
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategoriesForCurrentUser() {
        List<CategoryDTO> categories = categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    //update
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updateCategory = categoryService.updaCategory(id, categoryDTO);
        return ResponseEntity.ok(updateCategory);
    }


    //get Cate by Type for current user
    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByTpeForCurrentUser(@PathVariable String type) {
        List<CategoryDTO> categories = categoryService.getCategoriesByTpeForCurrentUser(type);
        return ResponseEntity.status(HttpStatus.OK).body(categories);

    }
}
