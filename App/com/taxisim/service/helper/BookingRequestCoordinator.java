package com.taxisim.service.helper;

import com.taxisim.model.Booking;
import com.taxisim.model.Taxi;

import java.util.concurrent.atomic.AtomicBoolean;

public class BookingRequestCoordinator {
    private final Booking booking;
    private final AtomicBoolean accepted = new AtomicBoolean(false);
    private Taxi acceptedTaxi;

    public BookingRequestCoordinator(Booking booking) {
        this.booking = booking;
    }

    public boolean tryAccept(Taxi taxi) {
        if (accepted.compareAndSet(false, true)) {
            this.acceptedTaxi = taxi;
            return true;
        }
        return false;
    }

    public Taxi getAcceptedTaxi() {
        return acceptedTaxi;
    }

    public Booking getBooking() {
        return booking;
    }
}
