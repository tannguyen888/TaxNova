package io.abc_def.kickstart_fx.login;

import io.abc_def.kickstart_fx.domain.User;
import io.abc_def.kickstart_fx.persistence.DatabaseManager;
import io.abc_def.kickstart_fx.persistence.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthService {

    private final UserRepository userRepository;
    private static User currentUser;
    private final DatabaseManager databaseManager;

    public AuthService(UserRepository userRepository, DatabaseManager databaseManager) {
        this.userRepository = userRepository;
        this.databaseManager = databaseManager;
        databaseManager.connect();
    }

    public void forgetPassword(String username, String newPassword) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (var stmt = databaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                String email = rs.getString("Enter your name");
                if (email != null) {
                    // Simulate sending a password reset email
                    System.out.println("Ready to call for changepassword()");

                    changePassword(username, newPassword);
                } else {
                    System.out.println("No email associated with this username.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate(String username, String password) {
        var user = userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("❌ User not found in database: " + username);
            return false;
        }
        String hashedPassword = hashPassword(password);
        String storedHash = user.getPasswordHash();

        // Debug logging
        System.out.println("📝 Authenticating user: " + username);
        System.out.println("   Input password hash: " + hashedPassword);
        System.out.println("   Stored hash:        " + storedHash);
        System.out.println("   Match: " + hashedPassword.equals(storedHash));

        boolean authenticated = storedHash.equals(hashedPassword);
        if (authenticated) {
            currentUser = user;
            System.out.println("✓ Login successful!");
        } else {
            System.out.println("✗ Password mismatch!");
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

    public void changePassword(String username, String newPassword) {
        var user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        String hashedPassword = hashPassword(newPassword);
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);
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
