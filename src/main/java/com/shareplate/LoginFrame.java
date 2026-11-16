
package com.shareplate;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    private final Color DARK = new Color(16, 48, 45);
    private final Color TEAL = new Color(22, 105, 91);
    private final Color GREEN = new Color(37, 153, 112);
    private final Color CREAM = new Color(246, 248, 242);
    private final Color TEXT = new Color(35, 55, 49);
    private final Color MUTED = new Color(125, 140, 132);
    private final Color BORDER = new Color(225, 233, 226);

    public LoginFrame() {

        setTitle("SharePlate | Food Rescue Network");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createLoginUI();
    }

    private void createLoginUI() {

        JPanel background = new JPanel(new GridLayout(1, 2));
        background.setBackground(CREAM);

        // LEFT BRANDING SECTION

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(DARK);
        leftPanel.setBorder(new EmptyBorder(38, 42, 30, 42));

        JPanel branding = new JPanel();
        branding.setOpaque(false);
        branding.setLayout(new BoxLayout(branding, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("🌿 SharePlate");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logo.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("FOOD RESCUE NETWORK");
        tagline.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tagline.setForeground(new Color(159, 208, 177));

        branding.add(logo);
        branding.add(Box.createVerticalStrut(7));
        branding.add(tagline);
        branding.add(Box.createVerticalStrut(65));

        JLabel headline = new JLabel(
            "<html>Good Food.<br>Better Future.</html>"
        );
        headline.setFont(new Font("Segoe UI", Font.BOLD, 43));
        headline.setForeground(Color.WHITE);

        JLabel description = new JLabel(
            "<html>Connect surplus food with people who need it.<br>" +
            "Together, let's create a world with less food waste.</html>"
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setForeground(new Color(203, 220, 207));

        branding.add(headline);
        branding.add(Box.createVerticalStrut(18));
        branding.add(description);

        leftPanel.add(branding, BorderLayout.NORTH);

        NatureArtwork artwork = new NatureArtwork();
        leftPanel.add(artwork, BorderLayout.CENTER);

        JLabel slogan = new JLabel(
            "SAVE FOOD     •     SHARE LOVE     •     GROW KINDNESS"
        );
        slogan.setFont(new Font("Segoe UI", Font.BOLD, 10));
        slogan.setForeground(new Color(166, 206, 173));

        leftPanel.add(slogan, BorderLayout.SOUTH);

        // RIGHT LOGIN SECTION

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(CREAM);

        RoundedPanel card = new RoundedPanel(
            25, Color.WHITE
        );

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(38, 40, 38, 40));
        card.setPreferredSize(new Dimension(410, 560));

        JLabel badge = new JLabel("  WELCOME TO SHAREPLATE  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(TEAL);
        badge.setBackground(new Color(230, 244, 234));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(8, 10, 8, 10));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcome = new JLabel("Welcome Back!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 30));
        welcome.setForeground(DARK);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "Login to continue your food rescue journey."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLabel = createLabel("EMAIL ADDRESS");

        emailField = new JTextField();
        styleInput(emailField);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passwordLabel = createLabel("PASSWORD");

        passwordField = new JPasswordField();
        styleInput(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginButton = new JButton("LOGIN TO SHAREPLATE   →");
        stylePrimaryButton(loginButton);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel accountText = new JLabel("Don't have an account?");
        accountText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountText.setForeground(MUTED);

        JButton registerButton = new JButton("Create Account");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerButton.setForeground(TEAL);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        registerPanel.setOpaque(false);
        registerPanel.add(accountText);
        registerPanel.add(registerButton);
        registerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel footer = new JLabel(
            "Together, we can make a difference. 🌱"
        );
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footer.setForeground(MUTED);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(badge);
        card.add(Box.createVerticalStrut(24));
        card.add(welcome);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(32));

        card.add(emailLabel);
        card.add(Box.createVerticalStrut(9));
        card.add(emailField);
        card.add(Box.createVerticalStrut(22));

        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(9));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(28));

        card.add(loginButton);
        card.add(Box.createVerticalStrut(19));
        card.add(registerPanel);
        card.add(Box.createVerticalStrut(25));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(17));
        card.add(footer);

        rightPanel.add(card);

        background.add(leftPanel);
        background.add(rightPanel);

        setContentPane(background);

        // EXISTING ACTIONS

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> openRegistration());

        getRootPane().setDefaultButton(loginButton);
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private void styleInput(JTextField field) {

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setBackground(new Color(249, 251, 248));
        field.setCaretColor(TEAL);
        field.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(10, 13, 10, 13)
        ));
    }

    private void stylePrimaryButton(JButton button) {

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(TEAL);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(GREEN);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(TEAL);
            }
        });
    }

    // LOGIN - EXISTING DATABASE FUNCTIONALITY

    private void login() {

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Please enter email and password.");
            return;
        }

        String sql = "SELECT full_name, password_hash, role " +
                     "FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String storedHash = rs.getString("password_hash");

                    if (PasswordUtil.verifyPassword(password, storedHash)) {

                        String name = rs.getString("full_name");
                        String role = rs.getString("role");

                        showMessage("Welcome, " + name + "!");

                        dispose();

                        SwingUtilities.invokeLater(() -> {

                            if ("DONOR".equalsIgnoreCase(role)) {
                                new DonorDashboard(name).setVisible(true);

                            } else if ("NGO".equalsIgnoreCase(role)) {
                                new NGODashboard(name).setVisible(true);

                            } else {
                                JOptionPane.showMessageDialog(
                                    null, "Unknown account role."
                                );
                                new LoginFrame().setVisible(true);
                            }
                        });

                    } else {
                        showMessage("Incorrect password.");
                    }

                } else {
                    showMessage("Account not found. Please register.");
                }
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

    // REGISTRATION - EXISTING DATABASE FUNCTIONALITY

    private void openRegistration() {

        JTextField nameField = new JTextField();
        JTextField emailInput = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordInput = new JPasswordField();

        JComboBox<String> roleBox = new JComboBox<>(
            new String[]{"DONOR", "NGO"}
        );

        JTextField[] fields = {
            nameField, emailInput, phoneField, passwordInput
        };

        for (JTextField field : fields) {
            styleInput(field);
        }

        roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleBox.setBackground(Color.WHITE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 12, 14));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] labels = {
            "Full Name:", "Email:", "Phone:",
            "Password:", "Account Type:"
        };

        Component[] components = {
            nameField, emailInput, phoneField,
            passwordInput, roleBox
        };

        for (int i = 0; i < labels.length; i++) {

            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(TEXT);

            panel.add(label);
            panel.add(components[i]);
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Create Your SharePlate Account",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        String email = emailInput.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordInput.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty()
                || phone.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showMessage("Enter a valid email.");
            return;
        }

        if (!phone.matches("[0-9]{10,15}")) {
            showMessage("Enter a valid phone number.");
            return;
        }

        if (password.length() < 8) {
            showMessage("Password must contain at least 8 characters.");
            return;
        }

        String sql = "INSERT INTO users " +
            "(full_name, email, phone, password_hash, role) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String hash = PasswordUtil.hashPassword(password);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, hash);
            ps.setString(5, role);

            ps.executeUpdate();

            showMessage("Account created successfully!");

        } catch (SQLIntegrityConstraintViolationException ex) {
            showMessage("This email is already registered.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "SharePlate Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // ROUNDED PANEL

    class RoundedPanel extends JPanel {

        private final int radius;
        private final Color backgroundColor;

        RoundedPanel(int radius, Color backgroundColor) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(backgroundColor);

            g2.fill(new RoundRectangle2D.Float(
                0, 0, getWidth(), getHeight(),
                radius, radius
            ));

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // DECORATIVE NATURE ARTWORK

    class NatureArtwork extends JPanel {

        private int movement = 0;

        public NatureArtwork() {

            setOpaque(false);

            Timer timer = new Timer(60, e -> {
                movement++;
                repaint();
            });

            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            // Soft decorative circles

            g2.setColor(new Color(255, 255, 255, 15));
            g2.fillOval(20, 60, 150, 150);
            g2.fillOval(width - 170, 120, 130, 130);

            // Floating leaves

            for (int i = 0; i < 8; i++) {

                int x = 35 + i * Math.max(1, width / 9);
                int y = 65 + (int)(
                    Math.sin(movement * 0.035 + i) * 10
                );

                g2.setColor(new Color(135, 184, 132, 120));

                g2.rotate(0.35, x, y);
                g2.fillOval(x, y, 35, 16);
                g2.rotate(-0.35, x, y);
            }

            // Ground

            g2.setColor(new Color(90, 143, 100, 90));
            g2.fillOval(10, height - 90, width - 20, 110);

            // Plant

            int centerX = width / 2;
            int centerY = height - 85;

            g2.setColor(new Color(115, 185, 112));
            g2.setStroke(new BasicStroke(5));

            g2.drawLine(centerX, centerY, centerX, centerY - 115);

            // Leaves

            g2.setColor(new Color(104, 173, 106));
            g2.fillOval(centerX - 75, centerY - 110, 75, 34);

            g2.setColor(new Color(157, 205, 129));
            g2.fillOval(centerX, centerY - 85, 75, 34);

            // Sun

            int sunY = 35 + (int)(
                Math.sin(movement * 0.025) * 6
            );

            g2.setColor(new Color(239, 204, 126, 210));
            g2.fillOval(width - 105, sunY, 55, 55);

            // Decorative message

            g2.setColor(new Color(232, 242, 226));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));

            String text = "Small actions. Greener tomorrow.";
            FontMetrics fm = g2.getFontMetrics();

            g2.drawString(
                text,
                (width - fm.stringWidth(text)) / 2,
                height - 25
            );

            g2.dispose();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
