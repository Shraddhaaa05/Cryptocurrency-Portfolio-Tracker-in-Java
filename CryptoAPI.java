package mpj1;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CryptoAPI {
    private static final Map<String, Double> priceCache = new ConcurrentHashMap<>();
    private static final Map<String, Long> timestampCache = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = 5 * 60 * 1000; // Cache duration: 5 minutes in milliseconds

    // Fetch the current price of a cryptocurrency using Binance API
    public static double getCurrentPrice(String symbol) {
        // Check if the price is cached and still valid
        if (isCacheValid(symbol)) {
            return priceCache.get(symbol); // Return cached price
        }

        try {
            // Construct Binance API URL
            String apiUrl = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol.toUpperCase() + "USDT";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Handle API response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                String jsonResponse = readResponse(conn);
                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                double price = jsonObject.get("price").getAsDouble();

                // Cache the price and timestamp
                priceCache.put(symbol, price);
                timestampCache.put(symbol, System.currentTimeMillis());
                return price;
            } else {
                System.err.println("Error: Received HTTP response code " + responseCode);
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error fetching price for symbol: " + symbol);
            e.printStackTrace();
        }

        // Return cached price if available, otherwise return 0.0
        return priceCache.getOrDefault(symbol, 0.0);
    }

    // Fetch the list of all cryptocurrencies using CoinGecko API
    public static List<String[]> getAllCryptos() {
        List<String[]> cryptoList = new ArrayList<>();
        try {
            // Construct CoinGecko API URL
            String apiUrl = "https://api.coingecko.com/api/v3/coins/list";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Handle API response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                String jsonResponse = readResponse(conn);
                JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();

                for (JsonElement element : jsonArray) {
                    JsonObject crypto = element.getAsJsonObject();
                    String id = crypto.get("id").getAsString();
                    String symbol = crypto.get("symbol").getAsString().toUpperCase();
                    String name = crypto.get("name").getAsString();
                    cryptoList.add(new String[]{id, symbol, name});
                }
            } else {
                System.err.println("Error: Received HTTP response code " + responseCode);
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error fetching cryptocurrency list.");
            e.printStackTrace();
        }
        return cryptoList;
    }

    // Helper method to check if cache is valid for a symbol
    private static boolean isCacheValid(String symbol) {
        if (priceCache.containsKey(symbol) && timestampCache.containsKey(symbol)) {
            long currentTime = System.currentTimeMillis();
            long cacheTime = timestampCache.get(symbol);
            return (currentTime - cacheTime) < CACHE_DURATION;
        }
        return false;
    }

    // Helper method to read the response from an HttpURLConnection
    private static String readResponse(HttpURLConnection conn) {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception e) {
            System.err.println("Error reading API response.");
            e.printStackTrace();
        }
        return response.toString();
    }
}
