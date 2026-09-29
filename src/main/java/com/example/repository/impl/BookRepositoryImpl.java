package com.example.repository.impl;

import com.example.model.Book;
import com.example.repository.BookRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepositoryImpl extends AbstractJdbcRepository<Book> implements BookRepository {

    private static final String COLUMNS =
            "BookId, Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image";
    private static final String INSERT_SQL =
            "INSERT INTO Book (Title, ISBN, AuthorId, PublisherId, PublishedYear, CategoryId, Description, Copies, Image) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Book SET Title = ?, ISBN = ?, AuthorId = ?, PublisherId = ?, PublishedYear = ?, "
                    + "CategoryId = ?, Description = ?, Copies = ?, Image = ? WHERE BookId = ?";

    @Override
    public Book save(Book b) throws SQLException {
        if (b.getBookId() == 0) {
            b.setBookId(executeInsert(INSERT_SQL, ps -> fill(ps, b, 1)));
        } else {
            executeUpdate(UPDATE_SQL, ps -> {
                fill(ps, b, 1);
                ps.setInt(10, b.getBookId());
            });
        }
        return b;
    }

    private static void fill(PreparedStatement ps, Book b, int at) throws SQLException {
        ps.setString(at, b.getTitle());
        ps.setString(at + 1, b.getIsbn());
        ps.setInt(at + 2, b.getAuthorId());
        ps.setInt(at + 3, b.getPublisherId());
        ps.setInt(at + 4, b.getPublishedYear());
        ps.setInt(at + 5, b.getCategoryId());
        ps.setString(at + 6, b.getDescription());
        ps.setInt(at + 7, b.getCopies());
        ps.setString(at + 8, b.getImage());
    }

    @Override
    public Optional<Book> findById(int bookId) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM Book WHERE BookId = ?", bookId);
    }

    @Override
    public List<Book> findAll() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Book ORDER BY Title");
    }

    @Override
    public List<Book> findByTitle(String title) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Book WHERE Title LIKE ? ORDER BY Title",
                "%" + title + "%");
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM Book WHERE ISBN = ?", isbn);
    }

    @Override
    public List<Book> findByAuthorId(int authorId) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Book WHERE AuthorId = ? ORDER BY Title", authorId);
    }

    @Override
    public List<Book> findByCategoryId(int categoryId) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Book WHERE CategoryId = ? ORDER BY Title", categoryId);
    }

    @Override
    public List<Book> findByFilters(Integer authorId, Integer categoryId, Integer publisherId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS + " FROM Book WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (authorId != null) {
            sql.append(" AND AuthorId = ?");
            params.add(authorId);
        }
        if (categoryId != null) {
            sql.append(" AND CategoryId = ?");
            params.add(categoryId);
        }
        if (publisherId != null) {
            sql.append(" AND PublisherId = ?");
            params.add(publisherId);
        }
        sql.append(" ORDER BY Title");
        return queryList(sql.toString(), params.toArray());
    }

    @Override
    public boolean deleteById(int bookId) throws SQLException {
        return executeUpdate("DELETE FROM Book WHERE BookId = ?", ps -> ps.setInt(1, bookId));
    }

    @Override
    public long count() throws SQLException {
        return queryCount("SELECT COUNT(*) FROM Book");
    }

    @Override
    protected Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("BookId"),
                rs.getString("Title"),
                rs.getString("ISBN"),
                rs.getInt("AuthorId"),
                rs.getInt("PublisherId"),
                rs.getInt("PublishedYear"),
                rs.getInt("CategoryId"),
                rs.getString("Description"),
                rs.getInt("Copies"),
                rs.getString("Image"));
    }
}
