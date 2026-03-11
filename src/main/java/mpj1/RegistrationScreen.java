package mpj1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class RegistrationScreen extends JFrame {
    public RegistrationScreen() {
        setTitle("Register");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a panel with a background image
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon imageIcon = new ImageIcon("C:/Users/gidde/Downloads/pexels-rdne-8369594.jpg");
                    Image image = imageIcon.getImage();
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        panel.setLayout(new GridLayout(6, 2, 10, 10));

        // Username Field
        JLabel usernameLabel = new JLabel("Username (Min 4 characters):");
        usernameLabel.setForeground(Color.WHITE);
        JTextField usernameField = new JTextField();

        // Email Field
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setForeground(Color.WHITE);
        JTextField emailField = new JTextField();

        // Password Field
        JLabel passwordLabel = new JLabel("Password (Min 8 characters):");
        passwordLabel.setForeground(Color.WHITE);
        JPasswordField passwordField = new JPasswordField();

        // Confirm Password Field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setForeground(Color.WHITE);
        JPasswordField confirmPasswordField = new JPasswordField();

        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(50, 200, 100));
        registerButton.setForeground(Color.WHITE);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(200, 50, 50));
        backButton.setForeground(Color.WHITE);

        // Add components to the panel
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        panel.add(registerButton);
        panel.add(backButton);

        // Add the panel to the frame
        add(panel);

        // Add event listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                // Validate inputs
                if (!validateUsername(username)) {
                    JOptionPane.showMessageDialog(null, "Invalid username! Must be at least 4 characters and contain no spaces.");
                    return;
                }
                if (!validateEmail(email)) {
                    JOptionPane.showMessageDialog(null, "Invalid email address!");
                    return;
                }
                if (!validatePassword(password)) {
                    JOptionPane.showMessageDialog(null, "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match!");
                    return;
                }

                // Register the user
                if (UserDAO.registerUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "Registration successful!");
                    dispose(); // Close the registration screen
                    new LoginRegisterGUI().setVisible(true); // Open the login/register screen
                } else {
                    JOptionPane.showMessageDialog(null, "Registration failed. Username may already exist.");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the registration screen
                new LoginRegisterGUI().setVisible(true); // Open the login/register screen
            }
        });

        // Make the frame visible
        setVisible(true);
    }

    // Input validation methods
    private boolean validateUsername(String username) {
        return username.length() >= 4 && !username.contains(" ");
    }

    private boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean validatePassword(String password) {
        // Updated regex to include `#`
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
        boolean isValid = Pattern.matches(passwordRegex, password);
        System.out.println("Password: " + password + ", Valid: " + isValid);
        return isValid;
    }


}
