package com.taxisim.auth;

public class Rider extends User {
    public Rider(String username) { super(username); }
    @Override public String role() { return "RIDER"; }
}