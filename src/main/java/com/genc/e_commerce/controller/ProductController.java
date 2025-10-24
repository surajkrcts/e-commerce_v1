package com.genc.e_commerce.controller;

import com.genc.e_commerce.entity.Product;
import com.genc.e_commerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("/add-data")
   public ResponseEntity<?> addProduct(@Valid @RequestBody Product product) {
       Map<String,Object> response=new HashMap<>();
       try {

          Product product1=productService.addProduct(product);
          response.put("message","data added successfully");
          log.debug("data added successfully");
          response.put("prodcut",product1);
          return ResponseEntity.ok(response);
       }catch (Exception e){
           response.put("error","product not added successfully");
           log.error("Error tp add the data");
           return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
       }
   }

@PutMapping("/update-data/{productId}")
public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
    Map<String,Object> response=new HashMap<>();
    try {
        Product product1=productService.updateProduct(productId, product);
        response.put("message","product updated successfully");
        log.debug("data updated successfully");
        response.put("product",product1);
        return ResponseEntity.ok(response);
    } catch (Exception e){
        response.put("error","product not updated");
        log.error("product not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
//    Product product1=productService.updateProduct(productId, product);
//    return product1;
}
@GetMapping("product-details/{productId}")
public ResponseEntity<?> getProductDetails(@PathVariable Long productId){
        Map<String, Object> response=new HashMap<>();
        try {
          Product product=productService.getProductDetails(productId);
          response.put("message","product details fetched successfully");
          response.put("product",product);
          return ResponseEntity.ok(response);
        } catch (Exception e){
          response.put("error","not fetched");
          log.error("error fetching data");
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
 String product=productService.deleteProduct(productId);
 return ResponseEntity.ok(product);
}

@GetMapping("/getall")
 public ResponseEntity<?> getAllProducts(){
    Map<String,Object> response=new HashMap<>();
    try {
        List<Product> product= productService.getAllProducts();
        response.put("message","all products fetched successfully");
        response.put("product",product);
        return ResponseEntity.ok(response);
    } catch (Exception e){
        response.put("error","product not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

 }
}
