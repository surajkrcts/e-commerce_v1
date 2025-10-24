package com.genc.e_commerce.controller;

import com.genc.e_commerce.entity.Product;
import com.genc.e_commerce.service.ProductService;
import jakarta.validation.Valid;
// Removed lombok.extern.slf4j.Slf4j to use the explicitly defined logger for consistency.
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException; // Assuming service might throw this

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductController {
    private static final Logger logger = LogManager.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @PostMapping("/add-product") // Renamed endpoint for clarity
    public ResponseEntity<Map<String, Object>> addProduct(@Valid @RequestBody Product product) {
        logger.debug("Request payload: {}", product);
        Map<String, Object> response = new HashMap<>();
        try {
            Product newProduct = productService.addProduct(product);
            response.put("message", "Product added successfully");
            response.put("data", newProduct);
            logger.info("Successfully added product with ID: {}", newProduct.getProductId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("error", "Failed to add the product due to a server error.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-product/{productId}") // Renamed endpoint for clarity
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        logger.info("Request received to update product with ID: {}", productId);
        logger.debug("Update payload for product ID {}: {}", productId, product);
        Map<String, Object> response = new HashMap<>();
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            response.put("message", "Product updated successfully");
            response.put("data", updatedProduct);
            logger.info("Successfully updated product with ID: {}", productId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) { // Example of a more specific exception
            logger.warn("Attempted to update a non-existent product with ID: {}", productId);
            response.put("error", "Product with ID " + productId + " not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating product with ID: {}", productId, e);
            response.put("error", "Failed to update product.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/product-details/{productId}")
    public ResponseEntity<?> getProductDetails(@PathVariable Long productId) {
        logger.info("Request received to fetch details for product ID: {}", productId);
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = productService.getProductDetails(productId);
            response.put("message", "Product details fetched successfully");
            response.put("data", product);
            logger.debug("Successfully fetched details for product ID: {}", productId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) { // Example of a more specific exception
            logger.warn("Product with ID: {} not found.", productId);
            response.put("error", "Product with ID " + productId + " not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-product/{productId}") // Renamed endpoint for clarity
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long productId) {
        logger.info("Request received to delete product with ID: {}", productId);
        Map<String, Object> response = new HashMap<>();
        try {
            productService.deleteProduct(productId);
            response.put("message", "Product with ID " + productId + " deleted successfully.");
            logger.info("Successfully deleted product with ID: {}", productId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) { // Example of a more specific exception
            logger.warn("Attempted to delete a non-existent product with ID: {}", productId);
            response.put("error", "Product with ID " + productId + " not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-all-products") // Renamed endpoint for clarity
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        logger.info("Request received to fetch all products.");
        Map<String, Object> response = new HashMap<>();
        try {
            List<Product> products = productService.getAllProducts();
            response.put("message", "All products fetched successfully");
            response.put("data", products);
            logger.debug("Fetched {} products.", products.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("An error occurred while fetching all products.", e);
            response.put("error", "Could not fetch products due to a server error.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}