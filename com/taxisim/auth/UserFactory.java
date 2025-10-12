package com.taxisim.auth;

import com.taxisim.util.Logger;
import com.taxisim.config.DBconfig;
import com.taxisim.exceptions.InvalidLoginException;

import java.sql.*;

public class UserFactory {
    private final Logger log = Logger.getInstance();

    public User login(String username, String password) throws Exception {
        String sql = "SELECT role FROM users WHERE username=? AND password=?";
        try (Connection c = DriverManager.getConnection(DBconfig.url, DBconfig.user, DBconfig.pwd);
             PreparedStatement ps = c.prepareStatement(sql)) {
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
        } catch (SQLException ex) {
            log.error("DB error during login");

        }
        return null;
    }
}
