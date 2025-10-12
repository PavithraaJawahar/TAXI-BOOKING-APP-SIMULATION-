package com.taxisim.auth;

public abstract class User {
    protected final String username;
    public User(String username) { this.username = username; }
    public String getUsername() { return username; }
    public abstract String role();
}
