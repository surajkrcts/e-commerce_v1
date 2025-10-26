package com.genc.e_commerce.service;

import com.genc.e_commerce.entity.Category;
import com.genc.e_commerce.entity.Product;
import com.genc.e_commerce.repository.CategoryRepo;
import com.genc.e_commerce.repository.ProductRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LogManager.getLogger(ProductService.class);
    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CategoryRepo categoryRepo;


    public Product addProduct(Product product) {
        logger.info("Attempting to add new product: {}", product.getName());
        Category incomingCategory = product.getCategory();
        if (incomingCategory != null && incomingCategory.getCategoryName() != null) {
            categoryRepo.findByCategoryNameIgnoreCase(incomingCategory.getCategoryName())
                    .ifPresent(existingCategory -> {
                        product.setCategory(existingCategory);
                        logger.debug("Category '{}' found and linked to product.", existingCategory);
                    });
        }
        return productRepo.save(product);
    }

    public Product updateProduct(Long productId, Product product) {
        logger.info("Attempting to update product with ID: {}", productId);
        Product existingProduct = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));


        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());


        return productRepo.save(existingProduct);
    }

    public Product getProductDetails(Long productId) {
         return productRepo.findById(productId).
                orElseThrow(() -> new RuntimeException("product not found"));
    }


    public String deleteProduct(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("no product found"));
        productRepo.deleteById(productId);
        return "product deleted successfully";
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

}
