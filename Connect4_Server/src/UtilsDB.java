

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *  Document   : Utils
 *  Created on : 06/09/2017, 12:48:01
 *  Author     : Daniel Abay
 */
public class UtilsDB
{
    public static Connection getDBConnection(String dbPath) 
    {
        File f = new File(dbPath);
        String dbFilePath = f.getAbsolutePath();
        
        String dbURL = "jdbc:ucanaccess://" + dbFilePath;
        String dbUserName = "";
        String dbPassword = "";

        Connection con = null;
        try
        {
          con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
        } catch(SQLException e) {e.printStackTrace();
        }
        
        return con;
    }
}
