package com.taxisim.auth;

public class Driver extends User {
    public Driver(String username) { super(username); }
    @Override public String role() { return "DRIVER"; }
}
