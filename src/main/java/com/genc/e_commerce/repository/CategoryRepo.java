package com.genc.e_commerce.repository;

import com.genc.e_commerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    @Query("SELECT COUNT(c) FROM Category c WHERE UPPER(c.categoryName) = UPPER(:name)")
    long countByCategoryNameIgnoreCase(@Param("name") String categoryName);
}
