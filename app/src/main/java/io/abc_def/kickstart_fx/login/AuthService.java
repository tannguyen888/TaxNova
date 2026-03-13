package io.abc_def.kickstart_fx.login;

import io.abc_def.kickstart_fx.domain.User;
import io.abc_def.kickstart_fx.persistence.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthService {

    private final UserRepository userRepository;
    private static User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String username, String password) {
        var user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        String hashedPassword = hashPassword(password);
        boolean authenticated = user.getPasswordHash().equals(hashedPassword);
        if (authenticated) {
            currentUser = user;
        }
        return authenticated;
    }

    public User register(String username, String password) {
        var existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        String hashedPassword = hashPassword(password);
        var newUser = new User(username, hashedPassword, "USER");
        userRepository.save(newUser);
        return newUser;
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
