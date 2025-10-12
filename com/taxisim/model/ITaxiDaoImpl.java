package com.taxisim.model;

import com.taxisim.config.DBconfig;
import com.taxisim.util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ITaxiDaoImpl implements ITaxiDao {
    private final Logger log = Logger.getInstance();

    @Override
    public Taxi save(Taxi t) throws Exception {
        String sql = "INSERT INTO taxis(taxi_code, driver_name, location_x, location_y, status, earnings) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DriverManager.getConnection(DBconfig.url, DBconfig.user, DBconfig.pwd);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getTaxiId());
            ps.setString(2, t.getDriverName());
            ps.setInt(3, t.getX());
            ps.setInt(4, t.getY());
            ps.setString(5, t.getStatus().name());
            ps.setDouble(6, t.getEarnings());
            ps.executeUpdate();
            log.info("Inserted taxi " + t.getTaxiId());

        } catch (SQLException ex) {
            log.error("DB error saving taxi");

        }
        return t;
    }

    @Override
    public Optional<Taxi> findById(String taxiCode) throws Exception {
        String sql = "SELECT taxi_code, driver_name, location_x, location_y, status, earnings FROM taxis WHERE taxi_code=?";
        try (Connection c = DriverManager.getConnection(DBconfig.url, DBconfig.user, DBconfig.pwd);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, taxiCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Taxi t = new Taxi.Builder(rs.getString("taxi_code"), rs.getString("driver_name"))
                            .location(rs.getInt("location_x"), rs.getInt("location_y"))
                            .status(TaxiStatus.valueOf(rs.getString("status")))
                            .earnings(rs.getDouble("earnings"))
                            .build();
                    return Optional.of(t);
                }
            }
        } catch (SQLException ex) {
            Logger.getInstance().error("DB error findByCode");
        }
        return Optional.empty();
    }

    @Override
    public List<Taxi> findAll() throws Exception {
        String sql = "SELECT taxi_code, driver_name, location_x, location_y, status, earnings FROM taxis";
        List<Taxi> res = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(DBconfig.url, DBconfig.user, DBconfig.pwd);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Taxi t = new Taxi.Builder(rs.getString("taxi_code"), rs.getString("driver_name"))
                        .location(rs.getInt("location_x"), rs.getInt("location_y"))
                        .status(TaxiStatus.valueOf(rs.getString("status")))
                        .earnings(rs.getDouble("earnings"))
                        .build();
                res.add(t);
            }

        } catch (SQLException ex) {
            Logger.getInstance().error("DB error findAll taxis");
        }
        return res;
    }

    @Override
    public List<Taxi> findAvailable() throws Exception {
        String sql = "SELECT taxi_code, driver_name, location_x, location_y, status, earnings FROM taxis WHERE status='AVAILABLE'";
        List<Taxi> res = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(DBconfig.url, DBconfig.user, DBconfig.pwd);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Taxi t = new Taxi.Builder(rs.getString("taxi_code"), rs.getString("driver_name"))
                        .location(rs.getInt("location_x"), rs.getInt("location_y"))
                        .status(TaxiStatus.valueOf(rs.getString("status")))
                        .earnings(rs.getDouble("earnings"))
                        .build();
                res.add(t);
            }
            return res;
        }
    }

    @Override
    public void updateStatusAndLocation(String taxiCode, String status, int x, int y) throws Exception {
        String sql = "UPDATE taxis SET status=?, location_x=?, location_y=? WHERE taxi_code=?";
        try (Connection c = DriverManager.getConnection(DBconfig.url, DBconfig.user, DBconfig.pwd);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setString(4, taxiCode);
            ps.executeUpdate();
            log.info("Updated taxi " + taxiCode + " status=" + status + " loc=(" + x + "," + y + ")");
        }
    }

    @Override
    public void addEarnings(String taxiCode, double amount) throws Exception {
        String sql = "UPDATE taxis SET earnings = COALESCE(earnings,0) + ? WHERE taxi_code=?";
        try (Connection c = DriverManager.getConnection(DBconfig.url, DBconfig.user, DBconfig.pwd);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, taxiCode);
            ps.executeUpdate();
            log.info("Added earnings " + amount + " to taxi " + taxiCode);
        }
    }
}
