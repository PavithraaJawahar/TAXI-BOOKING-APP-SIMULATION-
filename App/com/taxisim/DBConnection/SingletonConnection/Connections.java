package com.taxisim.DBConnection.SingletonConnection;

import java.sql.Connection;
//this interface has been declared for future scope for any database to have a singleton connection
public interface Connections {
    public Connection getconnection();
}
