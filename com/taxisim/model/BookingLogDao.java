package com.taxisim.model;

import com.taxisim.model.Booking;

import java.util.List;

public interface BookingLogDao {
    void logBooking(Booking booking) throws Exception;

    List<Booking> getAllBookings();

    Booking getBooking(String bookingId);
}

