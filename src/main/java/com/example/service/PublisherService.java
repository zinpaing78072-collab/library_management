package com.example.service;

import com.example.model.Publisher;
import com.example.repository.PublisherRepository;
import com.example.repository.impl.PublisherRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/** Application service for the publisher-management screen. */
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService() {
        this(new PublisherRepositoryImpl());
    }

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = Objects.requireNonNull(publisherRepository, "publisherRepository");
    }

    public List<Publisher> getPublishers() throws SQLException {
        return publisherRepository.findAll();
    }

    public List<Publisher> searchPublishers(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return publisherRepository.findAll();
        }
        return publisherRepository.findByName(name.trim());
    }

    /** Inserts or updates the publisher after validating the name. */
    public Publisher savePublisher(Publisher publisher) throws SQLException {
        if (publisher == null) {
            throw new IllegalArgumentException("Publisher is required.");
        }
        if (publisher.getName() == null || publisher.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        publisher.setName(publisher.getName().trim());
        publisherRepository.findByName(publisher.getName()).stream()
                .filter(other -> other.getPublisherId() != publisher.getPublisherId()
                        && other.getName().equalsIgnoreCase(publisher.getName()))
                .findFirst()
                .ifPresent(other -> {
                    throw new IllegalArgumentException("A publisher with this name already exists.");
                });
        return publisherRepository.save(publisher);
    }
}
