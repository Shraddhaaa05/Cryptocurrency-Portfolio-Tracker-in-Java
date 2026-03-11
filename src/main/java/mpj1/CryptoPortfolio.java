package mpj1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CryptoPortfolio {
    private final int userId;

    public CryptoPortfolio(int userId) {
        this.userId = userId;
    }

    /**
     * Adds a new cryptocurrency or updates an existing one.
     * If the crypto already exists, updates its quantity and average purchase price.
     * @return true if successful, false otherwise.
     */
    public boolean addOrUpdateCrypto(String name, String symbol, double quantity, double purchasePrice) {
        // Check if the crypto already exists
        String[] existingCrypto = getCryptoBySymbol(symbol);
        
        if (existingCrypto != null) {
            // Crypto exists → update quantity and average price
            double existingQuantity = Double.parseDouble(existingCrypto[2]);
            double existingPrice = Double.parseDouble(existingCrypto[3]);
            
            // Calculate new average price
            double totalQuantity = existingQuantity + quantity;
            double newAvgPrice = ((existingQuantity * existingPrice) + (quantity * purchasePrice)) / totalQuantity;
            
            // Update the record
            return updateCrypto(symbol, totalQuantity, newAvgPrice);
        } else {
            // Crypto doesn't exist → insert new record
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
                System.err.println("Error adding crypto: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Fetches all cryptocurrencies for the user, including current price and profit/loss.
     * @return List of String arrays, where each array represents a crypto.
     */
    public List<String[]> getAllCrypto() {
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
                double currentPrice = CryptoAPI.getCurrentPrice(symbol.toLowerCase());
                double profitLoss = (currentPrice - purchasePrice) * quantity;
                
                cryptoList.add(new String[]{
                    name, 
                    symbol, 
                    String.valueOf(quantity), 
                    String.valueOf(purchasePrice), 
                    String.valueOf(currentPrice), 
                    String.valueOf(profitLoss)
                });
            }
        } catch (SQLException e) {
            System.err.println("Error fetching portfolio: " + e.getMessage());
        }
        return cryptoList;
    }

    /**
     * Fetches a cryptocurrency by its symbol.
     * @return String array with [name, symbol, quantity, purchasePrice, currentPrice, profitLoss], or null if not found.
     */
    public String[] getCryptoBySymbol(String symbol) {
        String sql = "SELECT * FROM cryptocurrencies WHERE user_id = ? AND symbol = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, symbol);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                double quantity = rs.getDouble("quantity");
                double purchasePrice = rs.getDouble("purchase_price");
                double currentPrice = CryptoAPI.getCurrentPrice(symbol.toLowerCase());
                double profitLoss = (currentPrice - purchasePrice) * quantity;
                
                return new String[]{
                    name, 
                    symbol, 
                    String.valueOf(quantity), 
                    String.valueOf(purchasePrice),
                    String.valueOf(currentPrice),
                    String.valueOf(profitLoss)
                };
            }
        } catch (SQLException e) {
            System.err.println("Error fetching crypto: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates an existing cryptocurrency's quantity and purchase price.
     * @return true if successful, false otherwise.
     */
    public boolean updateCrypto(String symbol, double newQuantity, double newPurchasePrice) {
        String sql = "UPDATE cryptocurrencies SET quantity = ?, purchase_price = ? WHERE user_id = ? AND symbol = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newQuantity);
            pstmt.setDouble(2, newPurchasePrice);
            pstmt.setInt(3, userId);
            pstmt.setString(4, symbol);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating crypto: " + e.getMessage());
            return false;
        }
    }
}