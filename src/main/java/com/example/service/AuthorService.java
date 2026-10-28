package com.example.service;

import com.example.model.Author;
import com.example.repository.AuthorRepository;
import com.example.repository.impl.AuthorRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/** Application service for the author-management screen. */
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService() {
        this(new AuthorRepositoryImpl());
    }

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = Objects.requireNonNull(authorRepository, "authorRepository");
    }

    public List<Author> getAuthors() throws SQLException {
        return authorRepository.findAll();
    }

    public List<Author> searchAuthors(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return authorRepository.findAll();
        }
        return authorRepository.findByName(name.trim());
    }

    /** Inserts or updates the author after validating the name. */
    public Author saveAuthor(Author author) throws SQLException {
        if (author == null) {
            throw new IllegalArgumentException("Author is required.");
        }
        if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        author.setName(author.getName().trim());
        authorRepository.findByName(author.getName()).stream()
                .filter(other -> other.getAuthorId() != author.getAuthorId()
                        && other.getName().equalsIgnoreCase(author.getName()))
                .findFirst()
                .ifPresent(other -> {
                    throw new IllegalArgumentException("An author with this name already exists.");
                });
        return authorRepository.save(author);
    }
}
