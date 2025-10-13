package com.taxisim.model;


import com.taxisim.model.Booking.BookingObserver;

import com.taxisim.util.Logger;

public class BookingLoggerObserver implements BookingObserver {
    private final Logger log = Logger.getInstance();

    @Override
    public void onStatusChange(Booking b, BookingStatus oldStatus, BookingStatus newStatus) {
        log.info("Booking " + b.getBookingId() + " changed from " + oldStatus + " to " + newStatus);
    }
}
