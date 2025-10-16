package com.taxisim.auth;

public class Admin extends User {
    public Admin(String username) { super(username); }
    @Override public String role() { return "ADMIN"; }
}
