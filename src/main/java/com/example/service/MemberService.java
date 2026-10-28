package com.example.service;

import com.example.model.Member;
import com.example.repository.MemberRepository;
import com.example.repository.impl.MemberRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService() {
        this(new MemberRepositoryImpl());
    }

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = Objects.requireNonNull(memberRepository, "memberRepository");
    }

    public List<Member> getMembers() throws SQLException {
        return memberRepository.findAll();
    }

    public List<Member> searchMembers(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return memberRepository.findAll();
        }
        return memberRepository.findByName(name.trim());
    }

    public Member registerMember(Member member) throws SQLException {
        validate(member);
        Optional<Member> sameEmail = memberRepository.findByEmail(member.getEmail());
        if (sameEmail.isPresent() && sameEmail.get().getMemberId() != member.getMemberId()) {
            throw new IllegalArgumentException("A member with this email already exists.");
        }
        return memberRepository.save(member);
    }

    private void validate(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member is required.");
        }
        requireText(member.getName(), "Name");
        requireText(member.getNrc(), "NRC Number");
        requireText(member.getGender(), "Gender");
        requireText(member.getEmail(), "Email");
        requireText(member.getPhoneNo(), "Phone Number");
        requireText(member.getAddress(), "Address");
        if (member.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth is required.");
        }
        if (member.getRegisterDate() == null) {
            throw new IllegalArgumentException("Register date is required.");
        }
        if (member.getExpiredDate() == null) {
            throw new IllegalArgumentException("Expired date is required.");
        }
        if (member.getExpiredDate().isBefore(member.getRegisterDate())) {
            throw new IllegalArgumentException("Expired date must be after the register date.");
        }
    }

    private static void requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }
}
