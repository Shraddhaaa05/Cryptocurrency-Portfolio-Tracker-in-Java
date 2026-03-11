package mpj1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mpj"; // Update with your database name
    private static final String USER = "root"; // Update with your MySQL username
    private static final String PASSWORD = "root"; // Update with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(50) NOT NULL);"
                );
                conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS cryptocurrencies (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "symbol VARCHAR(10) NOT NULL, " +
                    "quantity DOUBLE NOT NULL, " +
                    "purchase_price DOUBLE NOT NULL, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id));"
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}