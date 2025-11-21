package mpj1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CryptoPortfolioDAO {
    // Add a cryptocurrency to the portfolio
    public static boolean addCrypto(int userId, String name, String symbol, double quantity, double purchasePrice) {
        String sql = "INSERT INTO cryptocurrencies(user_id, name, symbol, quantity, purchase_price) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            pstmt.setString(3, symbol);
            pstmt.setDouble(4, quantity);
            pstmt.setDouble(5, purchasePrice);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetch all cryptocurrencies for a user
    public static List<String[]> getAllCrypto(int userId) {
        List<String[]> cryptoList = new ArrayList<>();
        String sql = "SELECT * FROM cryptocurrencies WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String symbol = rs.getString("symbol");
                double quantity = rs.getDouble("quantity");
                double purchasePrice = rs.getDouble("purchase_price");
                cryptoList.add(new String[]{name, symbol, String.valueOf(quantity), String.valueOf(purchasePrice)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cryptoList;
    }
}