package com.taxisim.dao.Booking;

import com.taxisim.model.Booking;
import com.taxisim.model.BookingStatus;

import java.util.List;

public interface IBookingDao {
    void save(Booking booking) throws Exception;
    Booking findById(String bookingId) throws Exception;
    List<Booking> findAll() throws Exception;
    void updateStatus(String bookingId, BookingStatus status) throws Exception;
    void updateAssignedTaxi(String bookingId, String taxiCode) throws Exception;
}


