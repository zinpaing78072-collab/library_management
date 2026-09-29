package com.example.repository.impl;

import com.example.model.Member;
import com.example.repository.MemberRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MemberRepositoryImpl extends AbstractJdbcRepository<Member> implements MemberRepository {

    private static final String COLUMNS =
            "MemberId, Name, NRC, DateOfBirth, Gender, Email, PhoneNo, Address, RegisterDate, ExpiredDate, BookCount";
    private static final String INSERT_SQL =
            "INSERT INTO Member (Name, NRC, DateOfBirth, Gender, Email, PhoneNo, Address, RegisterDate, ExpiredDate, BookCount) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Member SET Name = ?, NRC = ?, DateOfBirth = ?, Gender = ?, Email = ?, PhoneNo = ?, "
                    + "Address = ?, RegisterDate = ?, ExpiredDate = ?, BookCount = ? WHERE MemberId = ?";

    @Override
    public Member save(Member m) throws SQLException {
        if (m.getMemberId() == 0) {
            m.setMemberId(executeInsert(INSERT_SQL, ps -> fill(ps, m, 1)));
        } else {
            executeUpdate(UPDATE_SQL, ps -> {
                fill(ps, m, 1);
                ps.setInt(11, m.getMemberId());
            });
        }
        return m;
    }

    private static void fill(PreparedStatement ps, Member m, int at) throws SQLException {
        ps.setString(at, m.getName());
        ps.setString(at + 1, m.getNrc());
        ps.setDate(at + 2, m.getDateOfBirth() == null ? null : Date.valueOf(m.getDateOfBirth()));
        ps.setString(at + 3, m.getGender());
        ps.setString(at + 4, m.getEmail());
        ps.setString(at + 5, m.getPhoneNo());
        ps.setString(at + 6, m.getAddress());
        ps.setDate(at + 7, m.getRegisterDate() == null ? null : Date.valueOf(m.getRegisterDate()));
        ps.setDate(at + 8, m.getExpiredDate() == null ? null : Date.valueOf(m.getExpiredDate()));
        ps.setInt(at + 9, m.getBookCount());
    }

    @Override
    public Optional<Member> findById(int memberId) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM Member WHERE MemberId = ?", memberId);
    }

    @Override
    public List<Member> findAll() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Member ORDER BY Name");
    }

    @Override
    public List<Member> findByName(String name) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Member WHERE Name LIKE ? ORDER BY Name",
                "%" + name + "%");
    }

    @Override
    public Optional<Member> findByEmail(String email) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM Member WHERE Email = ?", email);
    }

    @Override
    public List<Member> findExpired() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Member WHERE ExpiredDate < CURDATE() ORDER BY Name");
    }

    @Override
    public boolean deleteById(int memberId) throws SQLException {
        return executeUpdate("DELETE FROM Member WHERE MemberId = ?", ps -> ps.setInt(1, memberId));
    }

    @Override
    public long count() throws SQLException {
        return queryCount("SELECT COUNT(*) FROM Member");
    }

    @Override
    protected Member mapRow(ResultSet rs) throws SQLException {
        Date dateOfBirth = rs.getDate("DateOfBirth");
        Date registerDate = rs.getDate("RegisterDate");
        Date expiredDate = rs.getDate("ExpiredDate");
        return new Member(
                rs.getInt("MemberId"),
                rs.getString("Name"),
                rs.getString("NRC"),
                dateOfBirth == null ? null : dateOfBirth.toLocalDate(),
                rs.getString("Gender"),
                rs.getString("Email"),
                rs.getString("PhoneNo"),
                rs.getString("Address"),
                registerDate == null ? null : registerDate.toLocalDate(),
                expiredDate == null ? null : expiredDate.toLocalDate(),
                rs.getInt("BookCount"));
    }
}
