package com.taxisim.auth;

import com.taxisim.DBConnection.ConnectionPool.DBConnectionPool;
import com.taxisim.logging.Logger;

import com.taxisim.exceptions.InvalidLoginException;

import java.sql.*;

public class UserFactory {
    private final Logger log = Logger.getInstance();
    DBConnectionPool pool;
    Connection c=null;
    public User login(String username, String password) throws Exception {
        try
        {
        String sql = "SELECT role FROM users WHERE username=? AND password=?";
        pool = DBConnectionPool.getInstance();
        c=pool.takeConnection();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    log.info("User " + username + " authenticated as " + role);
                    switch (role.toUpperCase()) {
                        case "ADMIN": return new Admin(username);
                        case "DRIVER": return new Driver(username);
                        default: return new Rider(username);
                    }
                } else {
                    log.warn("Failed login attempt: " + username);
                    InvalidLoginException ex = new InvalidLoginException("Invalid credentials", username);
                    ex.handle();
                }
            }
        }
        } catch (SQLException | InterruptedException e) {
            log.error("DB error during login");
        }
        finally{
            if (c != null) {
                try {
                    DBConnectionPool.getInstance().releaseConnection(c);
                } catch (SQLException e) {
                    log.error("Error releasing connection: " + e.getMessage());
                }
        }
        }
        return null;
    }
}
