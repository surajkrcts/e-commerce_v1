package com.genc.e_commerce.service;

import com.genc.e_commerce.entity.Category;
import com.genc.e_commerce.entity.Product;
import com.genc.e_commerce.repository.CategoryRepo;
import com.genc.e_commerce.repository.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Use the Mockito extension for JUnit 5
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    // 1. Create mocks for the repository dependencies.
    @Mock
    private ProductRepo productRepo;

    @Mock
    private CategoryRepo categoryRepo;

    // 2. Inject the mocks into a real instance of ProductService.
    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;

    // 3. This setup method runs before each test to create fresh test data.
    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryId(1);
        testCategory.setCategoryName("Electronics");

        testProduct = new Product();
        testProduct.setProductId(100L);
        testProduct.setName("Laptop");
        testProduct.setPrice(1200.00);
        testProduct.setCategory(testCategory);
    }

    @Test
    void addProduct_whenCategoryExists_shouldLinkToExistingCategory() {
        // --- ARRANGE ---
        // Tell our mock CategoryRepo what to do when it's called.
        when(categoryRepo.findByCategoryNameIgnoreCase("Electronics")).thenReturn(Optional.of(testCategory));
        // Tell our mock ProductRepo to return the product when save is called.
        when(productRepo.save(any(Product.class))).thenReturn(testProduct);

        // --- ACT ---
        // Call the actual method we want to test.
        Product savedProduct = productService.addProduct(testProduct);

        // --- ASSERT ---
        // Check if the result is what we expect.
        assertNotNull(savedProduct);
        assertEquals("Electronics", savedProduct.getCategory().getCategoryName());
        // Verify that the repository methods were called as expected.
        verify(categoryRepo, times(1)).findByCategoryNameIgnoreCase("Electronics");
        verify(productRepo, times(1)).save(testProduct);
    }

    @Test
    void updateProduct_whenProductExists_shouldReturnUpdatedProduct() {
        // --- ARRANGE ---
        Product updatedInfo = new Product();
        updatedInfo.setName("Gaming Laptop");
        updatedInfo.setPrice(1500.00);

        // Mock the findById to return our existing test product.
        when(productRepo.findById(100L)).thenReturn(Optional.of(testProduct));
        when(productRepo.save(any(Product.class))).thenReturn(testProduct);

        // --- ACT ---
        Product result = productService.updateProduct(100L, updatedInfo);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals("Gaming Laptop", result.getName()); // Check that the name was updated.
        assertEquals(1500.00, result.getPrice());     // Check that the price was updated.
        verify(productRepo, times(1)).findById(100L);
        verify(productRepo, times(1)).save(testProduct);
    }

    @Test
    void updateProduct_whenProductNotFound_shouldThrowException() {
        // --- ARRANGE ---
        // Mock findById to return nothing, simulating a product not found.
        when(productRepo.findById(999L)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        // Assert that calling the method throws the expected exception.
        assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(999L, new Product());
        });
        // Verify that the save method was never called.
        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    void getProductDetails_whenProductExists_shouldReturnProduct() {
        // --- ARRANGE ---
        when(productRepo.findById(100L)).thenReturn(Optional.of(testProduct));

        // --- ACT ---
        Product foundProduct = productService.getProductDetails(100L);

        // --- ASSERT ---
        assertNotNull(foundProduct);
        assertEquals("Laptop", foundProduct.getName());
    }

    @Test
    void deleteProduct_whenProductExists_shouldReturnSuccessMessage() {
        // --- ARRANGE ---
        when(productRepo.findById(100L)).thenReturn(Optional.of(testProduct));
        // Use doNothing() for void methods like deleteById.
        doNothing().when(productRepo).deleteById(100L);

        // --- ACT ---
        String result = productService.deleteProduct(100L);

        // --- ASSERT ---
        assertEquals("product deleted successfully", result);
        verify(productRepo, times(1)).findById(100L);
        verify(productRepo, times(1)).deleteById(100L);
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() {
        // --- ARRANGE ---
        Product anotherProduct = new Product();
        anotherProduct.setProductId(101L);
        anotherProduct.setName("Mouse");
        List<Product> productList = Arrays.asList(testProduct, anotherProduct);

        when(productRepo.findAll()).thenReturn(productList);

        // --- ACT ---
        List<Product> results = productService.getAllProducts();

        // --- ASSERT ---
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Laptop", results.get(0).getName());
    }
}