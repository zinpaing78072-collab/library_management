package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.repository.impl.UserRepositoryImpl;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/** Application service for the login screen. */
public class LoginService {

    private final UserRepository userRepository;

    public LoginService() {
        this(new UserRepositoryImpl());
    }

    public LoginService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
    }

    /** Authenticates the credentials and confirms the user has the selected role.
     *  Throws {@link IllegalArgumentException} with a message meant for the user. */
    public User login(String name, String password, String role) throws SQLException {
        if (isBlank(name)) {
            throw new IllegalArgumentException("Enter your user name.");
        }
        if (isBlank(password)) {
            throw new IllegalArgumentException("Enter your password.");
        }
        if (isBlank(role)) {
            throw new IllegalArgumentException("Select your role.");
        }
        Optional<User> user = userRepository.login(name.trim(), password);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Incorrect user name or password.");
        }
        User found = user.get();
        if (!found.getRole().equalsIgnoreCase(role.trim())) {
            throw new IllegalArgumentException("This account is not registered as " + role + ".");
        }
        return found;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
