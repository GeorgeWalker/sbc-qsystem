/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.gov.sbc.qsystem;
import java.io.File;
import org.flywaydb.core.Flyway;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
/**
 *
 * @author George Walker
 */
public class Seeder {
    
    public static void main(String[] args) {
        
        System.out.println("QSystem Seeder");
        
        if (args.length < 1)
        {
            System.out.println ("ERROR - no database script folder location specified as Parameter 1 to the Seeder command.");
        }
        else
        {
            // get parameters from the environment
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://" + System.getenv ("MYSQL_SERVICE");
            
            System.out.println ("URL is " + url);
            
            String user = System.getenv ("MYSQL_ROOT_USER");
            if (user == null || user.isEmpty())
            {
                user = "root";
            }
            
            String password = System.getenv ("MYSQL_PASSWORD");
            String name = System.getenv ("MYSQL_DATABASE");

            // Create database if it does not exist
            String sql = "CREATE DATABASE IF NOT EXISTS " + name + " DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_unicode_ci";
             
            try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Flyway will need the database to be part of the URL  
            url = "jdbc:mysql://" + System.getenv ("MYSQL_SERVICE") + "/" + System.getenv ("MYSQL_DATABASE");
            
            password = System.getenv ("MYSQL_PASSWORD");
            
            // Create the Flyway instance
            Flyway flyway = new Flyway();

            // Point it to the database
            flyway.setDataSource(url, user, password);
        
            File temp = new File (args[0]);
            String filepath = "filesystem:" + temp.getAbsolutePath();
            
            System.out.println ("Migration source is " + filepath);
            
            flyway.setLocations(filepath);
            flyway.migrate();
        
            // Now create the configuration file. 
        }
    }
}