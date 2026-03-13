package io.abc_def.kickstart_fx.domain;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class User {
    private SimpleLongProperty id;
    private SimpleStringProperty username;
    private SimpleStringProperty passwordHash;
    private SimpleStringProperty role;

    public User() {
        this.id = new SimpleLongProperty();
        this.username = new SimpleStringProperty();
        this.passwordHash = new SimpleStringProperty();
        this.role = new SimpleStringProperty();
    }

    public User(String username, String passwordHash) {
        this.id = new SimpleLongProperty();
        this.username = new SimpleStringProperty(username);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.role = new SimpleStringProperty("USER");
    }

    public User(String username, String passwordHash, String role) {
        this.id = new SimpleLongProperty();
        this.username = new SimpleStringProperty(username);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.role = new SimpleStringProperty(role);
    }

    public User(Long id, String username, String passwordHash, String role) {
        this.id = new SimpleLongProperty(id);
        this.username = new SimpleStringProperty(username);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.role = new SimpleStringProperty(role);
    }

    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id.set(id);
    }

    public SimpleLongProperty idProperty() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash.get();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash.set(passwordHash);
    }

    public SimpleStringProperty passwordHashProperty() {
        return passwordHash;
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public SimpleStringProperty roleProperty() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" + "id="
                + id.get() + ", username='"
                + username.get() + '\'' + ", role='"
                + role.get() + '\'' + '}';
    }
}
