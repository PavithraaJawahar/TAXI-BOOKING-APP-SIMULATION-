package com.taxisim.exceptions;

import com.taxisim.logging.Logger;

public class NoTaxiAvailableException extends Exception {
    public NoTaxiAvailableException(String message) {
        super(message);
        Logger.getInstance().warn("NoTaxiAvailableException: " + message);
    }

    public void handle() {
        Logger.getInstance().info("Handling NoTaxiAvailableException: " + getMessage());
        System.out.println("No taxis are available currently. Your booking is queued and we will try again shortly.");
    }
}