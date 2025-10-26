package com.genc.e_commerce.controller;

import com.genc.e_commerce.entity.Category;
import com.genc.e_commerce.service.CategoryService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// @RestController marks this class as a Spring REST controller, ready to handle web requests.
@RestController
// @RequestMapping sets the base URL for all endpoints in this controller to "/api".
@RequestMapping("/api")
// @CrossOrigin allows requests from any origin, useful for frontend integration.
@CrossOrigin(origins = "*")
public class CategoryController {
    // Initializes a logger for this class.
    private static final Logger logger = LogManager.getLogger(CategoryController.class);

    // @Autowired injects the CategoryService to handle business logic.
    @Autowired
    CategoryService categoryService;

    /**
     * Endpoint to retrieve a list of all categories.
     * @return A ResponseEntity containing the list of all categories and an OK status.
     */
    @GetMapping("/get-all-category")
    public ResponseEntity<List<Category>> getAllCategory(){
        logger.info("Request received to get all categories");
        // Calls the service layer to fetch all categories.
        List<Category> categories = categoryService.getAllCategory();
        logger.debug("Fetched {} categories", categories.size());
        // Wraps the list in a ResponseEntity with a 200 OK status.
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Endpoint to add a new category.
     * @param category The category object from the request body. @Valid triggers validation.
     * @return A ResponseEntity containing the newly created category or an error message.
     */
    @PostMapping("/add-category")
    public ResponseEntity<Object> addCatgory(@Valid @RequestBody Category category){
        logger.info("Request received to add new category: {}", category.getCategoryName());
        try {
            // Calls the service layer to add the category.
            Category newCategory = categoryService.addCategory(category);
            logger.info("Successfully added new category with ID: {}", newCategory.getCategoryId());
            // Returns the new category with a 200 OK status.
            return ResponseEntity.ok(newCategory);
        } catch (Exception e){
            // Catches exceptions, typically for duplicate category names.
            logger.error("Error adding category, possible duplicate: {}", category.getCategoryName(), e);
            // Returns a 500 INTERNAL_SERVER_ERROR status with an error message.
            return new ResponseEntity<>("Duplicate category found " + category.getCategoryId(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to retrieve a single category by its ID.
     * @param categoryId The ID of the category, passed as a path variable.
     * @return A ResponseEntity containing the found category or an error message.
     */
    @GetMapping("/get-category-byId/{categoryId}")
    public ResponseEntity<Object> getCategoryById(@Valid @PathVariable int categoryId){
        logger.info("Request received to get category by ID: {}", categoryId);
        try{
            // Calls the service layer to find the category by its ID.
            Category categoryById = categoryService.getCategoryById(categoryId);
            logger.debug("Successfully fetched category with ID: {}", categoryId);
            // Returns the found category with a 200 OK status.
            return ResponseEntity.ok(categoryById);
        } catch (Exception e){
            // Catches the exception thrown by the service if the category is not found.
            logger.warn("Category not found for ID: {}", categoryId);
            // Returns a 404 NOT_FOUND status with an error message.
            return new ResponseEntity<>("category not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint to delete a category by its ID.
     * @param categoryId The ID of the category to be deleted.
     * @return A ResponseEntity containing the deleted category or an error message.
     */
    @DeleteMapping("/delete-category-byId/{categoryId}")
    public ResponseEntity<Object> deleteCategoryById(@PathVariable int categoryId){
        logger.info("Request received to delete category by ID: {}", categoryId);
        try {
            // Calls the service layer to delete the category.
            Optional<Category> deleteCategoryById = categoryService.deleteCategoryById(categoryId);
            logger.info("Successfully deleted category with ID: {}", categoryId);
            // Returns the deleted category object with a 200 OK status.
            return ResponseEntity.ok(deleteCategoryById);
        } catch (Exception e) {
            // This catch block might be redundant if the service handles the not-found case gracefully.
            logger.warn("Error deleting category with ID: {}. It might not exist.", categoryId, e);
            // Returns a 404 NOT_FOUND status.
            return new ResponseEntity<>("product not found", HttpStatus.NOT_FOUND);
        }
    }
}