package com.taxisim.dao.Taxi;

import com.taxisim.DBConnection.ConnectionPool.DBConnectionPool;
import com.taxisim.model.Taxi;
import com.taxisim.model.TaxiStatus;
import com.taxisim.logging.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ITaxiDaoImpl implements ITaxiDao {
    private final Logger log = Logger.getInstance();

    @Override
    public void save(Taxi t) throws Exception {
        String sql = "INSERT INTO taxis(taxi_id, driver_name, x, y, status, earnings, rating) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection c = DBConnectionPool.getInstance().takeConnection();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getTaxiId());
            ps.setString(2, t.getDriverName());
            ps.setInt(3, t.getX());
            ps.setInt(4, t.getY());
            ps.setString(5, t.getStatus().name());
            ps.setDouble(6, t.getEarnings());
            ps.setDouble(7, t.getRating());
            ps.executeUpdate();
            log.info("Taxi inserted: " + t.getTaxiId());
        } finally {
            DBConnectionPool.getInstance().releaseConnection(c);
        }
    }

    @Override
    public Taxi findById(String taxiId) throws Exception {
        String sql = "SELECT * FROM taxis WHERE taxi_id=?";
        Connection c = DBConnectionPool.getInstance().takeConnection();
        Taxi taxi = null;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, taxiId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    taxi = new Taxi.Builder(rs.getString("taxi_id"), rs.getString("driver_name"))
                            .location(rs.getInt("x"), rs.getInt("y"))
                            .status(TaxiStatus.valueOf(rs.getString("status")))
                            .earnings(rs.getDouble("earnings"))
                            .rating(rs.getDouble("rating"))
                            .build();
                }
            }
        } finally {
            DBConnectionPool.getInstance().releaseConnection(c);
        }
        return taxi;
    }

    @Override
    public List<Taxi> findAll() throws Exception {
        String sql = "SELECT * FROM taxis";
        Connection c = DBConnectionPool.getInstance().takeConnection();
        List<Taxi> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Taxi t = new Taxi.Builder(rs.getString("taxi_id"), rs.getString("driver_name"))
                        .location(rs.getInt("x"), rs.getInt("y"))
                        .status(TaxiStatus.valueOf(rs.getString("status")))
                        .earnings(rs.getDouble("earnings"))
                        .rating(rs.getDouble("rating"))
                        .build();
                list.add(t);
            }
        } finally {
            DBConnectionPool.getInstance().releaseConnection(c);
        }
        return list;
    }

    @Override
    public List<Taxi> findAvailable() throws Exception {
        String sql = "SELECT * FROM taxis WHERE status='AVAILABLE'";
        Connection c = DBConnectionPool.getInstance().takeConnection();
        List<Taxi> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Taxi t = new Taxi.Builder(rs.getString("taxi_id"), rs.getString("driver_name"))
                        .location(rs.getInt("x"), rs.getInt("y"))
                        .status(TaxiStatus.valueOf(rs.getString("status")))
                        .earnings(rs.getDouble("earnings"))
                        .rating(rs.getDouble("rating"))
                        .build();
                list.add(t);
            }
        } finally {
            DBConnectionPool.getInstance().releaseConnection(c);
        }
        return list;
    }

    @Override
    public void updateStatusAndLocation(String taxiId, String status, int x, int y) throws Exception {
        String sql = "UPDATE taxis SET status=?, x=?, y=? WHERE taxi_id=?";
        Connection c = DBConnectionPool.getInstance().takeConnection();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setString(4, taxiId);
            ps.executeUpdate();
            log.info("Taxi updated: " + taxiId + " status=" + status);
        } finally {
            DBConnectionPool.getInstance().releaseConnection(c);
        }
    }

    @Override
    public void addEarnings(String taxiId, double amount) throws Exception {
        String sql = "UPDATE taxis SET earnings = COALESCE(earnings,0) + ? WHERE taxi_id=?";
        Connection c = DBConnectionPool.getInstance().takeConnection();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, taxiId);
            ps.executeUpdate();
            log.info("Added earnings " + amount + " to " + taxiId);
        } finally {
            DBConnectionPool.getInstance().releaseConnection(c);
        }
    }
}
