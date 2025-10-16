package com.taxisim.dao.LogBooking;

import com.taxisim.DBConnection.ConnectionPool.DBConnectionPool;
import com.taxisim.model.Booking;
import com.taxisim.model.BookingStatus;
import com.taxisim.logging.Logger;

import java.sql.*;
import java.time.LocalDateTime;

public class BookingLogDaoImpl implements BookingLogDao {
    private final Logger log = Logger.getInstance();

    @Override
    public void logStatusChange(Booking booking, BookingStatus newStatus) throws Exception {
        String sql = "INSERT INTO bookings_log (booking_code, rider_name, taxi_code, status, requested_time, completed_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection c = DBConnectionPool.getInstance().takeConnection();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, booking.getBookingId());
            ps.setString(2, booking.getRiderName());
            ps.setString(3, booking.getAssignedTaxiId());
            ps.setString(4, newStatus.name());
            ps.setTimestamp(5, Timestamp.valueOf(booking.getRequestedTime()));
            ps.setTimestamp(6, Timestamp.valueOf(booking.getCompletedTime()));
            ps.executeUpdate();
            log.info("Booking log inserted for " + booking.getBookingId() + " : status=" + newStatus);
        } finally {
            DBConnectionPool.getInstance().releaseConnection(c);
        }
    }
}

