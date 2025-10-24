package com.genc.e_commerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Data
public class Category {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int categoryId;
    @Pattern(regexp = "[a-zA-Z\\s]+$", message = "Category name can only contain letters, numbers, and spaces.")
    private String categoryName;

    @OneToMany (mappedBy = "category",cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    List<Product> product;
}
