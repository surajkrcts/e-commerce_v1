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

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CategoryController {
    private static final Logger logger = LogManager.getLogger(CategoryController.class);
    @Autowired
    CategoryService categoryService;



    @GetMapping("/get-all-category")
    public ResponseEntity<List<Category>> getAllCategory(){
        logger.info("Request received to get all categories");
        List<Category> categories = categoryService.getAllCategory();
        logger.debug("Fetched {} categories", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping("/add-category")
    public ResponseEntity<Object> addCatgory(@Valid @RequestBody Category category){
        logger.info("Request received to add new category: {}", category.getCategoryName());
        try {
            Category newCategory=categoryService.addCategory(category);
            logger.info("Successfully added new category with ID: {}", newCategory.getCategoryId());
            return ResponseEntity.ok(newCategory);
        } catch (Exception e){
            logger.error("Error adding category, possible duplicate: {}", category.getCategoryName(), e);
            return new ResponseEntity<>("Duplicate category found "+category.getCategoryId(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/get-category-byId/{categoryId}")
    public ResponseEntity<Object> getCategoryById(@Valid @PathVariable int categoryId){
        logger.info("Request received to get category by ID: {}", categoryId);
        try{
            Category categoryById=categoryService.getCategoryById(categoryId);
            logger.debug("Successfully fetched category with ID: {}", categoryId);
            return ResponseEntity.ok(categoryById);
        } catch (Exception e){
            logger.warn("Category not found for ID: {}", categoryId);
            return new ResponseEntity<>("category not found",HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/delete-category-byId/{categoryId}")
    public ResponseEntity<Object> deleteCategoryById(@PathVariable int categoryId){
        logger.info("Request received to delete category by ID: {}", categoryId);
        try {
            Optional<Category> deleteCategoryById=categoryService.deleteCategoryById(categoryId);
            logger.info("Successfully deleted category with ID: {}", categoryId);
            return ResponseEntity.ok(deleteCategoryById);
        } catch (Exception e) {
            logger.warn("Error deleting category with ID: {}. It might not exist.", categoryId, e);
            return new ResponseEntity<>("product not found",HttpStatus.NOT_FOUND);
        }
    }
}

