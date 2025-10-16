package com.taxisim.DBConnection.ConnectionPool;

import com.taxisim.config.Manager.ConfigManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
//this implementation is only needed when we need more than 1 connection to the DB.
//for a simple console based application - a single connection works best
public class DBConnectionPool {
    private static DBConnectionPool instance;
    private BlockingQueue<Connection> connections;
    private static int poolSize=5;

    private DBConnectionPool() throws SQLException
    {
        String url= ConfigManager.getInstance().get("db.url");
        String user= ConfigManager.getInstance().get("db.user");
        String pwd= ConfigManager.getInstance().get("db.password");
        this.connections=new ArrayBlockingQueue<>(poolSize);
        for(int i=0;i<poolSize;i++)
        {
            connections.add(newConnection(url,user,pwd));
        }
    }
    public static DBConnectionPool getInstance() throws SQLException
    {
        if(instance==null) instance=new DBConnectionPool();
        return instance;
    }
    private Connection newConnection(String url,String user,String pwd) throws SQLException
    {
            return DriverManager.getConnection(url,user,pwd);
    }
    public Connection takeConnection() throws InterruptedException
    {
            return connections.take();
    }
    public void releaseConnection(Connection c)
    {
        if(c!=null) connections.offer(c);
    }




}
