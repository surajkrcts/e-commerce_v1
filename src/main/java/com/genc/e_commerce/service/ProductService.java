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

// @Service annotation marks this class as a Spring service component, containing business logic.
@Service
public class ProductService {

    // Initializes a logger for this class.
    private static final Logger logger = LogManager.getLogger(ProductService.class);

    // @Autowired injects an instance of ProductRepo for database operations related to products.
    @Autowired
    private ProductRepo productRepo;

    // @Autowired injects an instance of CategoryRepo for database operations related to categories.
    @Autowired
    private CategoryRepo categoryRepo;

    /**
     * Adds a new product to the database. If the product's category already exists,
     * it links to the existing category instead of creating a new one.
     * @param product The Product object to be saved.
     * @return The saved Product object, including its generated ID.
     */
    public Product addProduct(Product product) {
        logger.info("Attempting to add new product: {}", product.getName());
        Category incomingCategory = product.getCategory();

        // Checks if the product has a category with a name.
        if (incomingCategory != null && incomingCategory.getCategoryName() != null) {
            // Tries to find an existing category by name (case-insensitive).
            categoryRepo.findByCategoryNameIgnoreCase(incomingCategory.getCategoryName())
                    // If an existing category is found, set it on the product to avoid duplicates.
                    .ifPresent(existingCategory -> {
                        product.setCategory(existingCategory);
                        logger.debug("Category '{}' found and linked to product.", existingCategory);
                    });
        }
        // Saves the product (with either the new or existing category) to the database.
        return productRepo.save(product);
    }

    /**
     * Updates the details of an existing product.
     * @param productId The ID of the product to update.
     * @param product   A Product object containing the new information.
     * @return The updated Product object.
     * @throws RuntimeException if no product is found with the given productId.
     */
    public Product updateProduct(Long productId, Product product) {
        logger.info("Attempting to update product with ID: {}", productId);
        // Fetches the existing product from the database or throws an exception if it doesn't exist.
        Product existingProduct = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Updates the fields of the existing product with the new values.
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());

        // Saves the updated product back to the database.
        return productRepo.save(existingProduct);
    }

    /**
     * Retrieves the details of a single product by its ID.
     * @param productId The ID of the product to retrieve.
     * @return The found Product object.
     * @throws RuntimeException if no product is found with the given productId.
     */
    public Product getProductDetails(Long productId) {
        // Finds the product by its ID or throws an exception if not found.
        return productRepo.findById(productId).
                orElseThrow(() -> new RuntimeException("product not found"));
    }

    /**
     * Deletes a product from the database by its ID.
     * @param productId The ID of the product to delete.
     * @return A success message as a String.
     * @throws RuntimeException if no product is found with the given productId.
     */
    public String deleteProduct(Long productId) {
        // First, ensure the product exists before attempting to delete it.
        Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("no product found"));
        // Deletes the product from the database.
        productRepo.deleteById(productId);
        return "product deleted successfully";
    }

    /**
     * Retrieves a list of all products in the database.
     * @return A List of all Product objects.
     */
    public List<Product> getAllProducts() {
        // Fetches and returns all records from the product table.
        return productRepo.findAll();
    }
}