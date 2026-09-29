package com.example.model;

import java.time.LocalDate;

public class Member {
    
    private int memberId;
    private String name;
    private String nrc;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String phoneNo;
    private String address;
    private LocalDate registerDate;
    private LocalDate expiredDate;
    private int bookCount;
    
    public Member(int memberId, String name, String nrc, LocalDate dateOfBirth, String gender, String email,
            String phoneNo, String address, LocalDate registerDate, LocalDate expiredDate, int bookCount) {
        this.memberId = memberId;
        this.name = name;
        this.nrc = nrc;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phoneNo = phoneNo;
        this.address = address;
        this.registerDate = registerDate;
        this.expiredDate = expiredDate;
        this.bookCount = bookCount;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    @Override
    public String toString() {
        return "Member [memberId=" + memberId + ", name=" + name + ", nrc=" + nrc + ", dateOfBirth=" + dateOfBirth
                + ", gender=" + gender + ", email=" + email + ", phoneNo=" + phoneNo + ", address=" + address
                + ", registerDate=" + registerDate + ", expiredDate=" + expiredDate + ", bookCount=" + bookCount + "]";
    }

    
}