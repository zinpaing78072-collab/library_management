package com.example.repository;

import com.example.model.Member;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    /** Inserts or updates the member; returns the member with its generated id (inserts only). */
    Member save(Member member) throws SQLException;

    Optional<Member> findById(int memberId) throws SQLException;

    List<Member> findAll() throws SQLException;

    /** Members whose name contains the given text (case-insensitive). */
    List<Member> findByName(String name) throws SQLException;

    Optional<Member> findByEmail(String email) throws SQLException;

    /** Members whose expiredDate is before today. */
    List<Member> findExpired() throws SQLException;

    boolean deleteById(int memberId) throws SQLException;

    long count() throws SQLException;
}
