package mpj1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        setTitle("Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main panel
        JPanel mainPanel = new JPanel() {
        	@Override
        	protected void paintComponent(Graphics g) {
        	    super.paintComponent(g);
        	    try {
        	        String imagePath = "C:/Users/gidde/Downloads/pexels-rdne-8369594.jpg"; // Ensure no extra characters
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
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for alignment

        // Create a semi-transparent panel for the form
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent background
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridLayout(5, 1, 10, 10));
        formPanel.setPreferredSize(new Dimension(300, 250));

        // Username Field
        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        // Password Field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        // Login Button
        JButton loginButton = createCustomButton("Login", new Color(50, 150, 250));
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Authenticate the user
            if (UserDAO.authenticateUser(username, password)) {
                dispose(); // Close the login screen
                new MainDashboard(UserDAO.getUserId(username)).setVisible(true); // Open the main dashboard
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password");
            }
        });

        // Back Button
        JButton backButton = createCustomButton("Back", new Color(200, 50, 50));
        backButton.addActionListener(e -> {
            dispose(); // Close the login screen
            new LoginRegisterGUI().setVisible(true); // Open the login/register screen
        });

        // Add components to the form panel
        formPanel.add(usernameField);
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(backButton);

        // Add form panel to the main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(formPanel, gbc);

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
        button.setOpaque(true);

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
}
