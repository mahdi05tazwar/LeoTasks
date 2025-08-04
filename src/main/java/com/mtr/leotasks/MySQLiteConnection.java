package com.mtr.leotasks;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLiteConnection {
    public static Connection Connector(){
        try{
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:tasks.db");
        }
        catch (Exception e){
            return null;
        }
    }
}
