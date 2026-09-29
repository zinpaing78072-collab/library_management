package com.example.repository.impl;

import com.example.model.User;
import com.example.repository.UserRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl extends AbstractJdbcRepository<User> implements UserRepository {

    private static final String COLUMNS =
            "UserId, Name, Password, DateOfBirth, Gender, Email, PhoneNo, Role, Address, JoinedDate, Image";
    private static final String INSERT_SQL =
            "INSERT INTO Users (Name, Password, DateOfBirth, Gender, Email, PhoneNo, Role, Address, JoinedDate, Image) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Users SET Name = ?, Password = ?, DateOfBirth = ?, Gender = ?, Email = ?, PhoneNo = ?, "
                    + "Role = ?, Address = ?, JoinedDate = ?, Image = ? WHERE UserId = ?";

    @Override
    public User save(User u) throws SQLException {
        if (u.getUserId() == 0) {
            u.setUserId(executeInsert(INSERT_SQL, ps -> fill(ps, u, 1)));
        } else {
            executeUpdate(UPDATE_SQL, ps -> {
                fill(ps, u, 1);
                ps.setInt(11, u.getUserId());
            });
        }
        return u;
    }

    private static void fill(PreparedStatement ps, User u, int at) throws SQLException {
        ps.setString(at, u.getName());
        ps.setString(at + 1, u.getPassword());
        ps.setDate(at + 2, u.getDateOfBirth() == null ? null : Date.valueOf(u.getDateOfBirth()));
        ps.setString(at + 3, u.getGender());
        ps.setString(at + 4, u.getEmail());
        ps.setString(at + 5, u.getPhoneNo());
        ps.setString(at + 6, u.getRole());
        ps.setString(at + 7, u.getAddress());
        ps.setDate(at + 8, u.getJoinedDate() == null ? null : Date.valueOf(u.getJoinedDate()));
        ps.setString(at + 9, u.getImage());
    }

    @Override
    public Optional<User> findById(int userId) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM Users WHERE UserId = ?", userId);
    }

    @Override
    public List<User> findAll() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Users ORDER BY Name");
    }

    @Override
    public List<User> findByName(String name) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Users WHERE Name LIKE ? ORDER BY Name",
                "%" + name + "%");
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM Users WHERE Email = ?", email);
    }

    @Override
    public Optional<User> login(String name, String password) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM Users WHERE Name = ? AND Password = ?", name, password);
    }

    @Override
    public List<User> findByRole(String role) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Users WHERE Role = ? ORDER BY Name", role);
    }

    @Override
    public boolean deleteById(int userId) throws SQLException {
        return executeUpdate("DELETE FROM Users WHERE UserId = ?", ps -> ps.setInt(1, userId));
    }

    @Override
    public long count() throws SQLException {
        return queryCount("SELECT COUNT(*) FROM Users");
    }

    @Override
    protected User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("UserId"),
                rs.getString("Name"),
                rs.getString("Password"),
                toLocalDate(rs.getDate("DateOfBirth")),
                rs.getString("Gender"),
                rs.getString("Email"),
                rs.getString("PhoneNo"),
                rs.getString("Role"),
                rs.getString("Address"),
                toLocalDate(rs.getDate("JoinedDate")),
                rs.getString("Image"));
    }

    private static java.time.LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
