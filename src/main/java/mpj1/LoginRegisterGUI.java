package mpj1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoginRegisterGUI extends JFrame {

    public LoginRegisterGUI() {
        setTitle("Login / Register");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the database
        DatabaseConnection.initializeDatabase();

        // Set up the main panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    String imagePath = "C:/Users/gidde/Downloads/pexels-rdne-8369594.jpg";
                    if (Files.exists(Paths.get(imagePath))) {
                        ImageIcon imageIcon = new ImageIcon(imagePath);
                        Image image = imageIcon.getImage();
                        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                    } else {
                        g.setColor(Color.BLACK);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        g.setColor(Color.WHITE);
                        g.drawString("Background image not found.", 20, 20);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better alignment

        // Create a semi-transparent panel for buttons
        JPanel buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent background
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setPreferredSize(new Dimension(300, 200));

        // Login Button
        JButton loginButton = createCustomButton("Login", new Color(50, 150, 250));
        loginButton.addActionListener(e -> openLoginScreen());

        // Register Button
        JButton registerButton = createCustomButton("Register", new Color(50, 200, 100));
        registerButton.addActionListener(e -> openRegistrationScreen());

        // Title Label
        JLabel titleLabel = new JLabel("Welcome to Crypto Tracker", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(false);

        // Add components to the button panel
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Add components to the main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        mainPanel.add(buttonPanel, gbc);

        // Add the main panel to the frame
        add(mainPanel);

        // Make the frame visible
        setVisible(true);
    }

    private JButton createCustomButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setOpaque(true); // Ensure visibility on dark backgrounds

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private void openLoginScreen() {
        dispose(); // Close the current window
        new LoginScreen().setVisible(true); // Open the login screen
    }

    private void openRegistrationScreen() {
        dispose(); // Close the current window
        new RegistrationScreen().setVisible(true); // Open the registration screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginRegisterGUI().setVisible(true);
        });
    }
}