package mpj1;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainDashboard extends JFrame {
    private CryptoPortfolio portfolio;
    private JTable portfolioTable;

    public MainDashboard(int userId) {
        portfolio = new CryptoPortfolio(userId);
        setTitle("Cryptocurrency Portfolio Tracker");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = createBackgroundPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Section
        JLabel titleLabel = new JLabel("Cryptocurrency Portfolio Tracker", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        JButton addCryptoButton = createButton("Add Cryptocurrency", new Color(50, 150, 250)); 
        JButton calculateButton = createButton("Calculate Profit/Loss", new Color(50, 200, 100));
        JButton logoutButton = createButton("Logout", new Color(200, 50, 50));

        buttonPanel.add(addCryptoButton);
        buttonPanel.add(calculateButton);
        buttonPanel.add(logoutButton);

        // Portfolio Display
        portfolioTable = createStyledTable();
        JScrollPane portfolioScrollPane = new JScrollPane(portfolioTable);
        portfolioScrollPane.setOpaque(false);
        portfolioScrollPane.getViewport().setOpaque(false);

        // Add Components to Main Panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(portfolioScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add Main Panel to Frame
        add(mainPanel);

        // Add Button Actions
        addCryptoButton.addActionListener(e -> openAddCryptoDialog());
        calculateButton.addActionListener(e -> calculateProfitLoss());
        logoutButton.addActionListener(e -> logout());

        // Load Portfolio Data
        loadPortfolio();

        setVisible(true);
    }

    private void openAddCryptoDialog() {
        List<String[]> allCryptos = CryptoAPI.getAllCryptos();
        if (allCryptos == null || allCryptos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Failed to fetch cryptocurrency list.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> cryptoNames = allCryptos.stream()
                .map(crypto -> crypto[0] + " (" + crypto[1] + ")")
                .collect(Collectors.toList());

        JTextField searchField = new JTextField();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> cryptoList = new JList<>(listModel);
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();

        cryptoNames.forEach(listModel::addElement);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateList(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateList(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateList(); }

            private void updateList() {
                String searchText = searchField.getText().toLowerCase();
                listModel.clear();
                cryptoNames.stream()
                        .filter(name -> name.toLowerCase().contains(searchText))
                        .forEach(listModel::addElement);
            }
        });

        JPanel inputPanel = createAddCryptoInputPanel(searchField, cryptoList, quantityField, priceField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Cryptocurrency", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String selectedCrypto = cryptoList.getSelectedValue();
            if (selectedCrypto == null || quantityField.getText().isEmpty() || priceField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a cryptocurrency and provide all inputs.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double quantity = Double.parseDouble(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                String[] selectedCryptoDetails = allCryptos.stream()
                        .filter(crypto -> selectedCrypto.startsWith(crypto[0]))
                        .findFirst()
                        .orElse(null);

                if (selectedCryptoDetails != null) {
                	portfolio.addOrUpdateCrypto(selectedCryptoDetails[0], selectedCryptoDetails[1], quantity, price);
                    loadPortfolio();
                    JOptionPane.showMessageDialog(this, "Cryptocurrency added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add the selected cryptocurrency.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values for quantity and price.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createBackgroundPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon background = new ImageIcon("C:\\Users\\gidde\\Downloads\\maindash.jpeg");
                    Image image = background.getImage();
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(new Color(50, 50, 50));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JTable createStyledTable() {
        String[] columns = {"Name", "Symbol", "Quantity", "Purchase Price", "Current Price", "Profit/Loss"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setBackground(new Color(40, 40, 40));
        table.setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(60, 60, 60));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        return table;
    }

    private JPanel createAddCryptoInputPanel(JTextField searchField, JList<String> cryptoList, JTextField quantityField, JTextField priceField) {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(new Color(30, 30, 30));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        fieldsPanel.setBackground(new Color(30, 30, 30));

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setForeground(Color.WHITE);
        JLabel priceLabel = new JLabel("Purchase Price:");
        priceLabel.setForeground(Color.WHITE);

        fieldsPanel.add(quantityLabel);
        fieldsPanel.add(quantityField);
        fieldsPanel.add(priceLabel);
        fieldsPanel.add(priceField);

        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setBackground(new Color(30, 30, 30));
        JLabel searchLabel = new JLabel("Search Cryptocurrency:");
        searchLabel.setForeground(Color.WHITE);
        listPanel.add(searchLabel, BorderLayout.NORTH);
        listPanel.add(searchField, BorderLayout.CENTER);
        listPanel.add(new JScrollPane(cryptoList), BorderLayout.SOUTH);

        inputPanel.add(listPanel, BorderLayout.CENTER);
        inputPanel.add(fieldsPanel, BorderLayout.SOUTH);

        return inputPanel;
    }

    private void loadPortfolio() {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) portfolioTable.getModel();
            model.setRowCount(0); // Clear existing data

            List<String[]> cryptoList = portfolio.getAllCrypto();
            if (cryptoList == null) {
                JOptionPane.showMessageDialog(this, "Failed to load portfolio data.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (String[] crypto : cryptoList) {
                String name = crypto[0];
                String symbol = crypto[1];
                double quantity = Double.parseDouble(crypto[2]);
                double purchasePrice = Double.parseDouble(crypto[3]);
                double currentPrice = 0.0;
                try {
                    currentPrice = CryptoAPI.getCurrentPrice(symbol);
                } catch (RuntimeException ex) {
                    currentPrice = 0.0;
                }
                double profitLoss = (currentPrice - purchasePrice) * quantity;

                model.addRow(new Object[]{name, symbol, quantity, purchasePrice, currentPrice, profitLoss});
            }
        });
    }

    private void calculateProfitLoss() {
        SwingUtilities.invokeLater(() -> {
            List<String[]> cryptoList = portfolio.getAllCrypto();
            if (cryptoList == null || cryptoList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your portfolio is empty. Add some cryptocurrencies first.", "Portfolio Empty", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            double totalInvestment = 0.0;
            double totalProfitLoss = 0.0;
            String recommendation = "No recommendation available.";

            String cryptoToSell = "";
            double highestProfit = Double.NEGATIVE_INFINITY;

            for (String[] crypto : cryptoList) {
                String name = crypto[0];
                String symbol = crypto[1];
                double quantity = Double.parseDouble(crypto[2]);
                double purchasePrice = Double.parseDouble(crypto[3]);
                double currentPrice;

                try {
                    currentPrice = CryptoAPI.getCurrentPrice(symbol); // Get live price
                } catch (RuntimeException ex) {
                    currentPrice = purchasePrice; // Fallback in case of an API error
                }

                double profitLoss = (currentPrice - purchasePrice) * quantity;

                totalInvestment += (quantity * purchasePrice);
                totalProfitLoss += profitLoss;

                // Determine which crypto to sell based on the highest profit
                if (profitLoss > highestProfit) {
                    highestProfit = profitLoss;
                    cryptoToSell = name + " (" + symbol + ")";
                }
            }

            // Generate recommendation message
            if (highestProfit > 0) {
                recommendation = "Consider selling: " + cryptoToSell + " (Highest Profit: ₹" + String.format("%.2f", highestProfit) + ")";
            } else {
                recommendation = "No cryptocurrency is currently profitable.";
            }

            // Create custom dialog
            JDialog resultDialog = new JDialog(this, "Profit/Loss Summary", true);
            resultDialog.setSize(500, 400);
            resultDialog.setLocationRelativeTo(this);

            JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
            dialogPanel.setBackground(new Color(30, 30, 30));
            dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Header Section
            JLabel headerLabel = new JLabel("Profit/Loss Summary", JLabel.CENTER);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
            headerLabel.setForeground(Color.WHITE);

            // Investment and Profit/Loss Section
            JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            summaryPanel.setBackground(new Color(40, 40, 40));
            summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel investmentLabel = new JLabel("Total Investment: ₹" + String.format("%.2f", totalInvestment));
            investmentLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            investmentLabel.setForeground(Color.WHITE);

            JLabel profitLossLabel = new JLabel("Total Profit/Loss: ₹" + String.format("%.2f", totalProfitLoss));
            profitLossLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            profitLossLabel.setForeground(totalProfitLoss >= 0 ? new Color(50, 200, 100) : new Color(200, 50, 50));

            JLabel recommendationLabel = new JLabel(recommendation, JLabel.CENTER);
            recommendationLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            recommendationLabel.setForeground(Color.CYAN);

            summaryPanel.add(investmentLabel);
            summaryPanel.add(profitLossLabel);
            summaryPanel.add(recommendationLabel);

            // Footer Section with Close Button
            JButton closeButton = new JButton("Close");
            closeButton.setFont(new Font("Arial", Font.BOLD, 16));
            closeButton.setBackground(new Color(50, 150, 250));
            closeButton.setForeground(Color.WHITE);
            closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            closeButton.setFocusPainted(false);
            closeButton.addActionListener(e -> resultDialog.dispose());

            JPanel footerPanel = new JPanel();
            footerPanel.setBackground(new Color(30, 30, 30));
            footerPanel.add(closeButton);

            // Add components to the dialog panel
            dialogPanel.add(headerLabel, BorderLayout.NORTH);
            dialogPanel.add(summaryPanel, BorderLayout.CENTER);
            dialogPanel.add(footerPanel, BorderLayout.SOUTH);

            // Add panel to dialog and display
            resultDialog.add(dialogPanel);
            resultDialog.setVisible(true);
        });
    }



    private void logout() {
        dispose();
        new LoginRegisterGUI().setVisible(true);
    }
}
