package com.taxisim.model;

import com.taxisim.config.DBconfig;
import com.taxisim.util.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingLogDaoImpl implements BookingLogDao {
    private final Logger log = Logger.getInstance();
    private DBconfig con;
    @Override
    public void logBooking(Booking booking) throws Exception {
        String sql = "INSERT INTO bookings_log(booking_code, rider_name, taxi_code, status, requested_time, completed_time) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = con.getConnection(DBconfig.url, DBconfig.user, DBconfig.pwd);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, booking.getBookingId());
            ps.setString(2, booking.getRiderName());
            ps.setString(3, booking.getAssignedTaxiId());
            ps.setString(4, booking.getStatus().name());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(booking.getRequestedTime()));
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(LocalDateTime.now())); // completed_time
            ps.executeUpdate();

            log.info("Logged booking " + booking.getBookingId() + " to bookings_log table.");
        } catch (Exception ex) {
            log.error("Failed to log booking " + booking.getBookingId() + ": " + ex.getMessage());
        }
    }


    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT booking_code, rider_name, taxi_code, status, requested_time FROM bookings_log";

        try (Connection c = con.getConnection(DBconfig.url, DBconfig.user, DBconfig.pwd);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String code = rs.getString("booking_code");
                String rider = rs.getString("rider_name");
                String taxi = rs.getString("taxi_code");
                BookingStatus status = BookingStatus.valueOf(rs.getString("status"));
                LocalDateTime requestedTime = rs.getTimestamp("requested_time").toLocalDateTime();

                Booking b = new Booking.Builder(code, rider)
                        .pickup(0, 0) // pickup/drop coordinates not stored in DB; use placeholders or extend table
                        .drop(0, 0)
                        .when(requestedTime)
                        .status(status)
                        .build();
                b.setAssignedTaxiCode(taxi);

                bookings.add(b);
            }

        } catch (Exception ex) {
            log.error("Failed to fetch all bookings: " + ex.getMessage());
        }

        return bookings;
    }


    public Booking getBooking(String bookingCode) {
        String sql = "SELECT booking_code, rider_name, taxi_code, status, requested_time FROM bookings_log WHERE booking_code = ?";
        try (Connection c = con.getConnection(DBconfig.url, DBconfig.user, DBconfig.pwd);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, bookingCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String code = rs.getString("booking_code");
                    String rider = rs.getString("rider_name");
                    String taxi = rs.getString("taxi_code");
                    BookingStatus status = BookingStatus.valueOf(rs.getString("status"));
                    LocalDateTime requestedTime = rs.getTimestamp("requested_time").toLocalDateTime();

                    Booking b = new Booking.Builder(code, rider)
                            .pickup(0, 0) // placeholder
                            .drop(0, 0)
                            .when(requestedTime)
                            .status(status)
                            .build();
                    b.setAssignedTaxiCode(taxi);

                    return b;
                }
            }

        } catch (Exception ex) {
            log.error("Failed to fetch booking " + bookingCode + ": " + ex.getMessage());
        }

        return null;
    }
}
