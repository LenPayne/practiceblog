/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services;

import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Len Payne <len.payne@lambtoncollege.ca>
 */
@ApplicationScoped
public class Wiper {
    static Date lastUpdate = new Date();

    static public Date getLastUpdate() {
        return lastUpdate;
    }

    static public void setLastUpdate(Date date) {
        lastUpdate = date;
    }
    
    static public void wipeIfOld() {
        Date twoMinutesAgo = new Date(new Date().getTime() - (2 * 60 * 1000));
        System.out.println("Seeing if " + lastUpdate + " is before " + twoMinutesAgo);
        if (lastUpdate.before(twoMinutesAgo)) {
            try {
                System.out.println("Database Inactive - Wiping Clean");
                Connection conn = getConnection();
                Statement stmt = conn.createStatement();            
                stmt.executeUpdate("TRUNCATE blog");
            } catch (SQLException ex) {
                Logger.getLogger(Wiper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        lastUpdate = new Date();
    }
    
    static private Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String jdbc = "jdbc:mysql://" + System.getenv("OPENSHIFT_MYSQL_DB_HOST") + ":" +
                    System.getenv("OPENSHIFT_MYSQL_DB_PORT") + "/practiceblog";
            String user = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
            String pass = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");        
            conn = DriverManager.getConnection(jdbc, user, pass);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Blog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
}
