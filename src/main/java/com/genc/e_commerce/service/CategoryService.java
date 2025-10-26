package com.genc.e_commerce.service;

import com.genc.e_commerce.entity.Category;
import com.genc.e_commerce.repository.CategoryRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    public Category addCategory(Category category) {
        final Integer incomingId = category.getCategoryId();
        long duplicateCount = categoryRepo.countByCategoryNameIgnoreCase(category.getCategoryName());
        if (duplicateCount > 0) {
            if (incomingId == null) {

                throw new RuntimeException("Error: Category name '" + category.getCategoryName() + "' already exists.");
            }
            Category existingCategory = categoryRepo.findById(incomingId)
                    .orElseThrow(() -> new RuntimeException("Category ID not found for update."));
            if (duplicateCount > 1) {
                throw new RuntimeException("Database has multiple categories with the same name. Please fix data.");
            }
        }
        return categoryRepo.save(category);

    }

    public Category getCategoryById(int categoryId) {
        Category findCategoryById = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("No product found " + categoryId));
        return categoryRepo.findById(categoryId).get();

    }


    public List<Category> getAllCategory() {
        return categoryRepo.findAll();
    }

    public Optional<Category> deleteCategoryById(int categoryId) {
        Optional<Category> existingCategory = categoryRepo.findById(categoryId);
        if (existingCategory.isPresent()) {
            categoryRepo.deleteById(categoryId);
            return existingCategory;
        }
        return Optional.empty();
    }
}