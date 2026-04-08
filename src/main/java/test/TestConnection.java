package test;

import database.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        DBConnection db = DBConnection.getInstance();
        Connection conn = db.getConnection();

        if (conn != null) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                System.out.println("✅ CONNECTION SUCCESSFUL!");
                System.out.println("Database ready for Student Management System!");
            } catch (Exception e) {
                System.err.println("❌ Query failed: " + e.getMessage());
            }
        } else {
            System.err.println("❌ NO CONNECTION!");
        }
    }
}