package com.genc.e_commerce.controller;

import com.genc.e_commerce.entity.Product;
import com.genc.e_commerce.service.ProductService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @RestController marks this class as a Spring REST controller, handling web requests and returning JSON.
@RestController
// @RequestMapping sets the base URL for all endpoints in this controller to "/api".
@RequestMapping("/api")
// @CrossOrigin allows requests from any origin, useful for frontend integration.
@CrossOrigin(origins = "*")
public class ProductController {
    // Initializes a logger for this class.
    private static final Logger logger = LogManager.getLogger(ProductController.class);
    // @Autowired injects the ProductService to handle business logic for products.
    @Autowired
    ProductService productService;

    /**
     * Endpoint to add a new product.
     * @param product The product data from the request body. @Valid enables validation.
     * @return A ResponseEntity with a custom message and the created product.
     */
    @PostMapping("/add-data")
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product) {
        // A map is used to create a structured JSON response.
        Map<String,Object> response = new HashMap<>();
        try {
            // Call the service to add the product to the database.
            Product product1 = productService.addProduct(product);
            response.put("message", "data added successfully");
            logger.info("Successfully added product with ID: {}", product1.getProductId());
            response.put("prodcut", product1);
            // Return a 200 OK response on success.
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "product not added successfully");
            // NOTE: HttpStatus.ACCEPTED (202) might not be the most intuitive choice for an error.
            // Consider INTERNAL_SERVER_ERROR (500) or BAD_REQUEST (400) for more clarity.
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
    }

    /**
     * Endpoint to update an existing product.
     * @param productId The ID of the product to update, from the URL path.
     * @param product The new product data from the request body.
     * @return A ResponseEntity with a success message and the updated product.
     */
    @PutMapping("/update-data/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        logger.info("Request received to update product with ID: {}", productId);
        Map<String,Object> response = new HashMap<>();
        try {
            // Call the service to update the product.
            Product product1 = productService.updateProduct(productId, product);
            response.put("message", "product updated successfully");
            logger.info("Successfully updated product with ID: {}", productId);
            response.put("product", product1);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "product not updated");
            logger.warn("Error updating product with ID: {}. It might not exist.", productId, e);
            // Return a 404 NOT_FOUND if the product to update doesn't exist.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Endpoint to get the details of a single product.
     * @param productId The ID of the product to fetch.
     * @return A ResponseEntity containing the product details.
     */
    @GetMapping("product-details/{productId}")
    public ResponseEntity<?> getProductDetails(@PathVariable Long productId){
        logger.info("Request received to get product details for ID: {}", productId);
        Map<String, Object> response = new HashMap<>();
        try {
            // Call the service to retrieve the product.
            Product product = productService.getProductDetails(productId);
            response.put("message", "product details fetched successfully");
            response.put("product", product);
            logger.debug("Successfully fetched details for product ID: {}", productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "not fetched");
            logger.warn("Product details not found for ID: {}", productId, e);
            // Return 404 NOT_FOUND if the service throws an exception (e.g., product not found).
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    /**
     * Endpoint to delete a product by its ID.
     * @param productId The ID of the product to delete.
     * @return A ResponseEntity containing the success message from the service.
     */
    @DeleteMapping("/delete-data/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId){
        logger.info("Request received to delete product with ID: {}", productId);
        // Calls the service to delete the product and gets a confirmation string.
        String product = productService.deleteProduct(productId);
        logger.info("Successfully deleted product with ID: {}", productId);
        // Returns the confirmation string with a 200 OK status.
        return ResponseEntity.ok(product);
    }

    /**
     * Endpoint to get a list of all products.
     * @return A ResponseEntity containing a list of all products.
     */
    @GetMapping("/getall")
    public ResponseEntity<?> getAllProducts(){
        logger.info("Request received to get all products");
        Map<String,Object> response = new HashMap<>();
        try {
            // Call the service to get all products.
            List<Product> product = productService.getAllProducts();
            response.put("message", "all products fetched successfully");
            response.put("product", product);
            logger.debug("Fetched {} products", product.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "product not found");
            logger.error("Error fetching all products", e);
            // Return 404 NOT_FOUND if an error occurs.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}