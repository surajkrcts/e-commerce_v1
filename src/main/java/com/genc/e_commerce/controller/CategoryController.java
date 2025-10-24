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
        List<Category> categories = categoryService.getAllCategory();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping("/add-category")
    public ResponseEntity<Object> addCatgory(@Valid @RequestBody Category category){
        try {
            Category newCategory=categoryService.addCategory(category);
            return ResponseEntity.ok(newCategory);
        } catch (Exception e){
            return new ResponseEntity<>("Duplicate category found "+category.getCategoryId(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/get-category-byId/{categoryId}")
    public ResponseEntity<Object> getCategoryById(@Valid @PathVariable int categoryId){
        try{
            Category categoryById=categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(categoryById);
        } catch (Exception e){
            return new ResponseEntity<>("category not found",HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/delete-category-byId/{categoryId}")
    public ResponseEntity<Object> deleteCategoryById(@PathVariable int categoryId){
        try {
            Optional<Category> deleteCategoryById=categoryService.deleteCategoryById(categoryId);
            return ResponseEntity.ok(deleteCategoryById);
        } catch (Exception e) {
            return new ResponseEntity<>("product not found",HttpStatus.NOT_FOUND);
        }
    }
}
