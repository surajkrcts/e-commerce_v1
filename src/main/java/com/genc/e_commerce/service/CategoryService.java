package com.genc.e_commerce.service;

import com.genc.e_commerce.entity.Category;
import com.genc.e_commerce.repository.CategoryRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// @Service annotation marks this class as a Spring service component for business logic.
@Service
public class CategoryService {
    // @Autowired injects an instance of CategoryRepo for database operations.
    @Autowired
    private CategoryRepo categoryRepo;

    /**
     * Adds a new category or updates an existing one.
     * Prevents the creation of categories with duplicate names.
     * @param category The Category object to be saved or updated.
     * @return The saved Category object.
     * @throws RuntimeException if a new category with an existing name is added, or if data integrity issues are found.
     */
    public Category addCategory(Category category) {
        // Gets the ID from the incoming category object, which will be null for a new category.
        final Integer incomingId = category.getCategoryId();
        // Counts how many categories already exist with the same name (ignoring case).
        long duplicateCount = categoryRepo.countByCategoryNameIgnoreCase(category.getCategoryName());

        // Checks if any duplicates were found.
        if (duplicateCount > 0) {
            // If it's a new category (no ID) and the name already exists, throw an error.
            if (incomingId == null) {
                throw new RuntimeException("Error: Category name '" + category.getCategoryName() + "' already exists.");
            }
            // If an ID was provided, it's an update. Fetch the original category to ensure it exists.
            Category existingCategory = categoryRepo.findById(incomingId)
                    .orElseThrow(() -> new RuntimeException("Category ID not found for update."));
            // This is a data integrity check. Throws an error if the database somehow contains more than one category with the same name.
            if (duplicateCount > 1) {
                throw new RuntimeException("Database has multiple categories with the same name. Please fix data.");
            }
        }
        // If no duplicates are found (or if it's a valid update), save the category to the database.
        return categoryRepo.save(category);
    }

    /**
     * Retrieves a single category by its ID.
     * @param categoryId The ID of the category to find.
     * @return The found Category object.
     * @throws RuntimeException if no category is found with the given ID.
     */
    public Category getCategoryById(int categoryId) {
        // This line finds the category or throws an exception if not found, but the result isn't used.
        Category findCategoryById = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("No product found " + categoryId));
        // This second call to the database is redundant. It finds the category again and returns it.
        return categoryRepo.findById(categoryId).get();
    }

    /**
     * Retrieves a list of all categories from the database.
     * @return A List of all Category objects.
     */
    public List<Category> getAllCategory() {
        // Fetches and returns all records from the category table.
        return categoryRepo.findAll();
    }

    /**
     * Deletes a category by its ID.
     * @param categoryId The ID of the category to delete.
     * @return An Optional containing the deleted Category if it was found, otherwise an empty Optional.
     */
    public Optional<Category> deleteCategoryById(int categoryId) {
        // First, check if a category with the given ID exists.
        Optional<Category> existingCategory = categoryRepo.findById(categoryId);
        // If the category is present...
        if (existingCategory.isPresent()) {
            // ...delete it from the database.
            categoryRepo.deleteById(categoryId);
            // Return the category that was just deleted.
            return existingCategory;
        }
        // If no category was found, return an empty Optional.
        return Optional.empty();
    }
}