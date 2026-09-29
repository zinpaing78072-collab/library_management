package com.example.repository.impl;

import com.example.model.Publisher;
import com.example.repository.PublisherRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PublisherRepositoryImpl extends AbstractJdbcRepository<Publisher> implements PublisherRepository {

    private static final String COLUMNS = "PublisherId, Name";

    @Override
    public Publisher save(Publisher publisher) throws SQLException {
        if (publisher.getPublisherId() == 0) {
            publisher.setPublisherId(executeInsert("INSERT INTO Publisher (Name) VALUES (?)",
                    ps -> ps.setString(1, publisher.getName())));
        } else {
            executeUpdate("UPDATE Publisher SET Name = ? WHERE PublisherId = ?",
                    ps -> {
                        ps.setString(1, publisher.getName());
                        ps.setInt(2, publisher.getPublisherId());
                    });
        }
        return publisher;
    }

    @Override
    public Optional<Publisher> findById(int publisherId) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM Publisher WHERE PublisherId = ?", publisherId);
    }

    @Override
    public List<Publisher> findAll() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Publisher ORDER BY Name");
    }

    @Override
    public List<Publisher> findByName(String name) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Publisher WHERE Name LIKE ? ORDER BY Name",
                "%" + name + "%");
    }

    @Override
    public boolean deleteById(int publisherId) throws SQLException {
        return executeUpdate("DELETE FROM Publisher WHERE PublisherId = ?",
                ps -> ps.setInt(1, publisherId));
    }

    @Override
    public long count() throws SQLException {
        return queryCount("SELECT COUNT(*) FROM Publisher");
    }

    @Override
    protected Publisher mapRow(ResultSet rs) throws SQLException {
        return new Publisher(rs.getInt("PublisherId"), rs.getString("Name"));
    }
}
