package com.example.service;

import com.example.model.Category;
import com.example.repository.CategoryRepository;
import com.example.repository.impl.CategoryRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/** Application service for the category-management screen. */
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService() {
        this(new CategoryRepositoryImpl());
    }

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = Objects.requireNonNull(categoryRepository, "categoryRepository");
    }

    public List<Category> getCategories() throws SQLException {
        return categoryRepository.findAll();
    }

    public List<Category> searchCategories(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return categoryRepository.findAll();
        }
        return categoryRepository.findByName(name.trim());
    }

    /** Inserts or updates the category after validating the name. */
    public Category saveCategory(Category category) throws SQLException {
        if (category == null) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        category.setName(category.getName().trim());
        categoryRepository.findByName(category.getName()).stream()
                .filter(other -> other.getCategoryId() != category.getCategoryId()
                        && other.getName().equalsIgnoreCase(category.getName()))
                .findFirst()
                .ifPresent(other -> {
                    throw new IllegalArgumentException("A category with this name already exists.");
                });
        return categoryRepository.save(category);
    }
}
