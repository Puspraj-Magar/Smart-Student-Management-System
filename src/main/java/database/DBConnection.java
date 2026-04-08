package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    // 🔥 FIXED: Update these credentials!
    private static final String URL = "jdbc:mysql://localhost:3307/student_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";           // ✅ Your MySQL username
    private static final String PASSWORD = "Pusp123raj@#$";  // 🔥 CHANGE THIS!

    private DBConnection() {
        try {
            // Load MySQL driver explicitly (Java 24 compatibility)
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found! Add mysql-connector-j.jar");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            System.err.println("🔍 Check: MySQL running? Correct credentials? Database exists?");
            e.printStackTrace();
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        // 🔥 CRITICAL FIX: Check if connection is valid
        try {
            if (connection == null || connection.isClosed()) {
                System.err.println("⚠️ Reconnecting to database...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("❌ Cannot get connection: " + e.getMessage());
            return null;  // Return null instead of throwing
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}