package io.abc_def.kickstart_fx.login;

import io.abc_def.kickstart_fx.persistence.UserRepository;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String username, String password) {
        var user = userRepository.findByUsername(username);
        if (user == null) return false;
        return user.getPasswordHash().equals(password);
    }
}
