package com.taxisim.exceptions;

import com.taxisim.logging.Logger;

public class InvalidLoginException extends Exception {
    private final String user;

    public InvalidLoginException(String msg, String user) {
        super(msg);
        this.user = user;
        Logger.getInstance().warn("InvalidLoginException created for user=" + user+ " msg=" + msg);
    }


    public void handle() {
        Logger.getInstance().info("Handling InvalidLoginException for user=" + user);
        System.out.println("Login failed for user: " + user + " Please check username/password");
    }
}
