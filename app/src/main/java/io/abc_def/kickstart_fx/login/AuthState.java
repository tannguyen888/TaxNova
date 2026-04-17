package io.abc_def.kickstart_fx.login;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class AuthState {

    private static final BooleanProperty authenticated = new SimpleBooleanProperty(false);
    private static String currentUsername = null;

    public static boolean isAuthenticated() {
        return authenticated.get();
    }

    public static void setAuthenticated(boolean value) {
        authenticated.set(value);
    }

    public static BooleanProperty authenticatedProperty() {
        return authenticated;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public void login(String username) {
        setCurrentUsername(username);
        setAuthenticated(true);
    }

    public void logout(String username) {
        setCurrentUsername(null);
        setAuthenticated(false);
    }

    public static void logout() {
        authenticated.set(false);
        currentUsername = null;
    }
}
