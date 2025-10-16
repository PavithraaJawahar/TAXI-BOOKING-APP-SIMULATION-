package com.taxisim.DBConnection.SingletonConnection;

import com.taxisim.config.Manager.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostGresConnection implements Connections {
    private static PostGresConnection pgcon;
    private Connection con;
    private PostGresConnection() throws SQLException
    {
        String url= ConfigManager.getInstance().get("db.url");
        String user= ConfigManager.getInstance().get("db.user");
        String pwd= ConfigManager.getInstance().get("db.password");
        try{
            this.con = DriverManager.getConnection(url, user, pwd);
        }
        catch(Exception e)
        {
            throw new SQLException("Cannot create connection");
        }
    }

    public static PostGresConnection getInstance() throws SQLException
    {
        if(pgcon==null) pgcon=new PostGresConnection();
        return pgcon;
    }

    public Connection getconnection()
    {
        return con;
    }
}
