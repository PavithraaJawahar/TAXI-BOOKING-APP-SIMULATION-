package com.taxisim.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconfig {
    public static String url="jdbc:postgresql://localhost:5432/postgres";
    public static String user="pavithra-pt8006";
    public static String pwd="password";


    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(url,user,pwd);
    }
}
