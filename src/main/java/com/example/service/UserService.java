package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.repository.impl.UserRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Application service for the user-management screen. */
public class UserService {
    /** Roles offered in the role ComboBox. */
    public static final List<String> ROLES = List.of("Admin", "Librarian");

    private final UserRepository userRepository;

    public UserService() {
        this(new UserRepositoryImpl());
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
    }

    public List<User> getUsers() throws SQLException {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return userRepository.findAll();
        }
        return userRepository.findByName(name.trim());
    }

    public User registerUser(User user) throws SQLException {
        validate(user);
        Optional<User> sameEmail = userRepository.findByEmail(user.getEmail());
        if (sameEmail.isPresent() && sameEmail.get().getUserId() != user.getUserId()) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }
        return userRepository.save(user);
    }

    private void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is required.");
        }
        requireText(user.getName(), "User name");
        requireText(user.getPassword(), "Password");
        requireText(user.getGender(), "Gender");
        requireText(user.getEmail(), "Email");
        requireText(user.getPhoneNo(), "Phone number");
        requireText(user.getRole(), "Role");
        requireText(user.getAddress(), "Address");
        if (user.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth is required.");
        }
        if (user.getJoinedDate() == null) {
            throw new IllegalArgumentException("Join date is required.");
        }
    }

    private static void requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }
}
