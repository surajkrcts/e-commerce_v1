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


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductController {
    private static final Logger logger = LogManager.getLogger(ProductController.class);
    @Autowired
    ProductService productService;

    @PostMapping("/add-data")
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product) {
        Map<String,Object> response=new HashMap<>();
        try {

            Product product1=productService.addProduct(product);
            response.put("message","data added successfully");
            logger.info("Successfully added product with ID: {}", product1.getProductId());
            response.put("prodcut",product1);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("error","product not added successfully");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
    }

    @PutMapping("/update-data/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        logger.info("Request received to update product with ID: {}", productId);
        Map<String,Object> response=new HashMap<>();
        try {
            Product product1=productService.updateProduct(productId, product);
            response.put("message","product updated successfully");

            logger.info("Successfully updated product with ID: {}", productId);
            response.put("product",product1);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("error","product not updated");

            logger.warn("Error updating product with ID: {}. It might not exist.", productId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }
    @GetMapping("product-details/{productId}")
    public ResponseEntity<?> getProductDetails(@PathVariable Long productId){
        logger.info("Request received to get product details for ID: {}", productId);
        Map<String, Object> response=new HashMap<>();
        try {
            Product product=productService.getProductDetails(productId);
            response.put("message","product details fetched successfully");
            response.put("product",product);
            logger.debug("Successfully fetched details for product ID: {}", productId);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("error","not fetched");

            logger.warn("Product details not found for ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
//@DeleteMapping("/delete-data/{productId}")
//public ResponseEntity<?> deleteProduct(@PathVariable Long productId){
//        Map<String,Object> response=new HashMap<>();
//        try {
//            Product product=productService.deleteProduct(productId);
//            response.put("message","deleted successfully");
//            response.put("product",product);
//            return ResponseEntity.ok(response);
//        } catch (Exception e){
//            response.put("error","product not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
    ////       return productService.deleteProduct(productId);
//}

    @DeleteMapping("/delete-data/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId){
        logger.info("Request received to delete product with ID: {}", productId);
        String product=productService.deleteProduct(productId);
        logger.info("Successfully deleted product with ID: {}", productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/getall")
    public ResponseEntity<?> getAllProducts(){
        logger.info("Request received to get all products");
        Map<String,Object> response=new HashMap<>();
        try {
            List<Product> product= productService.getAllProducts();
            response.put("message","all products fetched successfully");
            response.put("product",product);
            logger.debug("Fetched {} products", product.size());
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("error","product not found");
            logger.error("Error fetching all products", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }
}

