package com.taxisim.dao.Booking;

import com.taxisim.DBConnection.ConnectionPool.DBConnectionPool;
import com.taxisim.model.Booking;
import com.taxisim.model.BookingStatus;
import com.taxisim.logging.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IBookingDaoImpl implements IBookingDao {
    private final Logger log = Logger.getInstance();

    @Override
    public void save(Booking b) throws Exception {
        String sql = "INSERT INTO bookings_log (booking_code, rider_name, taxi_code, status, requested_time, completed_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnectionPool.getInstance().takeConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getBookingId());
            ps.setString(2, b.getRiderName());
            ps.setString(3, b.getAssignedTaxiId());
            ps.setString(4, b.getStatus().name());
            ps.setTimestamp(5, Timestamp.valueOf(b.getRequestedTime()));
            ps.setTimestamp(6, null); // initially null
            ps.executeUpdate();
            log.info("Booking inserted into bookings_log: " + b.getBookingId());
        }
    }

    @Override
    public Booking findById(String bookingId) throws Exception {
        String sql = "SELECT * FROM bookings_log WHERE booking_code = ?";
        try (Connection c = DBConnectionPool.getInstance().takeConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildBookingFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Booking> findAll() throws Exception {
        String sql = "SELECT * FROM bookings_log ORDER BY requested_time DESC";
        List<Booking> list = new ArrayList<>();
        try (Connection c = DBConnectionPool.getInstance().takeConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(buildBookingFromResultSet(rs));
            }
        }
        return list;
    }

    @Override
    public void updateStatus(String bookingId, BookingStatus status) throws Exception {
        String sql = "UPDATE bookings_log SET status=? WHERE booking_code=?";
        try (Connection c = DBConnectionPool.getInstance().takeConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, bookingId);
            ps.executeUpdate();
            log.info("Booking " + bookingId + " updated to status " + status + " in bookings_log");
        }
    }

    @Override
    public void updateAssignedTaxi(String bookingId, String taxiCode) throws Exception {
        String sql = "UPDATE bookings_log SET taxi_code=? WHERE booking_code=?";
        try (Connection c = DBConnectionPool.getInstance().takeConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, taxiCode);
            ps.setString(2, bookingId);
            ps.executeUpdate();
            log.info("Booking " + bookingId + " assigned to taxi " + taxiCode + " in bookings_log");
        }
    }


    private Booking buildBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking b = new Booking.Builder(rs.getString("booking_code"), rs.getString("rider_name"))
                .status(BookingStatus.valueOf(rs.getString("status")))
                .when(rs.getTimestamp("requested_time").toLocalDateTime())
                .build();
        b.setAssignedTaxiId(rs.getString("taxi_code"));
        return b;
    }
}
