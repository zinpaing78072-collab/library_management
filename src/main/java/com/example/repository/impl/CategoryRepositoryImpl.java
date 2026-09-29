package com.example.repository.impl;

import com.example.model.Category;
import com.example.repository.CategoryRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CategoryRepositoryImpl extends AbstractJdbcRepository<Category> implements CategoryRepository {

    private static final String COLUMNS = "CategoryId, Name";

    @Override
    public Category save(Category category) throws SQLException {
        if (category.getCategoryId() == 0) {
            category.setCategoryId(executeInsert("INSERT INTO Category (Name) VALUES (?)",
                    ps -> ps.setString(1, category.getName())));
        } else {
            executeUpdate("UPDATE Category SET Name = ? WHERE CategoryId = ?",
                    ps -> {
                        ps.setString(1, category.getName());
                        ps.setInt(2, category.getCategoryId());
                    });
        }
        return category;
    }

    @Override
    public Optional<Category> findById(int categoryId) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM Category WHERE CategoryId = ?", categoryId);
    }

    @Override
    public List<Category> findAll() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Category ORDER BY Name");
    }

    @Override
    public List<Category> findByName(String name) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Category WHERE Name LIKE ? ORDER BY Name",
                "%" + name + "%");
    }

    @Override
    public boolean deleteById(int categoryId) throws SQLException {
        return executeUpdate("DELETE FROM Category WHERE CategoryId = ?",
                ps -> ps.setInt(1, categoryId));
    }

    @Override
    public long count() throws SQLException {
        return queryCount("SELECT COUNT(*) FROM Category");
    }

    @Override
    protected Category mapRow(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("CategoryId"), rs.getString("Name"));
    }
}
