
package com.shareplate;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
import java.net.URI;

public class DonorDashboard extends JFrame {

    private final String donorName;

    private JTable table;
    private DefaultTableModel model;

    private JLabel totalLabel;
    private JLabel availableLabel;
    private JLabel collectedLabel;

    private JTextField foodField;
    private JTextField quantityField;
    private JTextField phoneField;
    private JTextField locationField;
    private JTextField deadlineField;
    private JComboBox<String> categoryBox;

    // DESIGN COLORS

    private final Color NAVY = new Color(17, 48, 48);
    private final Color TEAL = new Color(22, 105, 91);
    private final Color GREEN = new Color(39, 157, 112);
    private final Color BACKGROUND = new Color(244, 247, 245);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT_DARK = new Color(38, 57, 51);
    private final Color TEXT_MUTED = new Color(125, 143, 134);
    private final Color BORDER = new Color(225, 233, 228);

    public DonorDashboard(String donorName) {

        this.donorName = donorName;

        setTitle("SharePlate | Donor Dashboard");
        setSize(1250, 780);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createDashboard();
        loadDonations();
        loadStatistics();
    }

    // MAIN DASHBOARD

    private void createDashboard() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createMainContent(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // SIDEBAR

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(new Dimension(225, 0));
        sidebar.setBackground(NAVY);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(28, 17, 22, 17));

        JLabel logo = new JLabel("🌿 SharePlate");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("FOOD RESCUE NETWORK");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 9));
        subtitle.setForeground(new Color(151, 205, 174));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(7));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(45));

        JLabel menuTitle = new JLabel("  MAIN MENU");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuTitle.setForeground(new Color(143, 174, 159));
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(menuTitle);
        sidebar.add(Box.createVerticalStrut(15));

        addSidebarItem(sidebar, "▦   Overview", () -> {
            loadDonations();
            loadStatistics();
        });

        addSidebarItem(sidebar, "▤   My Donations", this::loadDonations);

        addSidebarItem(sidebar, "＋   Add Food Donation", this::showDonationForm);

        addSidebarItem(sidebar, "⌖   Live Tracking", this::openLiveTracking);

        sidebar.add(Box.createVerticalGlue());

        // PROFILE CARD

        JPanel profile = new JPanel();
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
        profile.setBackground(new Color(29, 70, 62));
        profile.setBorder(new EmptyBorder(14, 12, 14, 12));
        profile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        profile.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel profileName = new JLabel("  " + donorName);
        profileName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        profileName.setForeground(Color.WHITE);

        JLabel profileRole = new JLabel("  Food Donor");
        profileRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        profileRole.setForeground(new Color(178, 207, 187));

        profile.add(profileName);
        profile.add(Box.createVerticalStrut(5));
        profile.add(profileRole);

        sidebar.add(profile);
        sidebar.add(Box.createVerticalStrut(12));

        // LOGOUT

        JButton logout = new JButton("←   Logout");

        logout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logout.setForeground(new Color(255, 225, 220));
        logout.setBackground(new Color(29, 70, 62));
        logout.setFocusPainted(false);
        logout.setBorderPainted(false);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));

        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        sidebar.add(logout);

        return sidebar;
    }

    private void addSidebarItem(
        JPanel sidebar, String text, Runnable action
    ) {

        JButton button = new JButton(text);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(new Color(225, 239, 230));
        button.setBackground(NAVY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorder(new EmptyBorder(0, 12, 0, 5));

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(TEAL);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(NAVY);
            }
        });

        button.addActionListener(e -> action.run());

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(7));
    }

    // MAIN CONTENT

    private JPanel createMainContent() {

        JPanel main = new JPanel(new BorderLayout(20, 22));
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(28, 30, 25, 30));

        // TOP HEADER

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel headings = new JPanel();
        headings.setOpaque(false);
        headings.setLayout(new BoxLayout(headings, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome back, " + donorName + "!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(NAVY);

        JLabel subtitle = new JLabel(
            "Manage your food donations and make a difference today."
        );

        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);

        headings.add(title);
        headings.add(Box.createVerticalStrut(7));
        headings.add(subtitle);

        JButton newDonation = new JButton("+  New Donation");
        styleDashboardButton(newDonation, TEAL);

        newDonation.addActionListener(e -> showDonationForm());

        top.add(headings, BorderLayout.WEST);
        top.add(newDonation, BorderLayout.EAST);

        main.add(top, BorderLayout.NORTH);

        // DASHBOARD BODY

        JPanel center = new JPanel(new BorderLayout(18, 20));
        center.setOpaque(false);

        center.add(createStatistics(), BorderLayout.NORTH);
        center.add(createDonationTable(), BorderLayout.CENTER);

        main.add(center, BorderLayout.CENTER);

        return main;
    }

    // STATISTICS

    private JPanel createStatistics() {

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);

        totalLabel = new JLabel("0");
        availableLabel = new JLabel("0");
        collectedLabel = new JLabel("0");

        stats.add(createStatCard(
            "TOTAL DONATIONS",
            totalLabel,
            new Color(44, 112, 96),
            "Food donations added"
        ));

        stats.add(createStatCard(
            "AVAILABLE",
            availableLabel,
            new Color(39, 157, 112),
            "Ready for collection"
        ));

        stats.add(createStatCard(
            "COLLECTED",
            collectedLabel,
            new Color(205, 139, 57),
            "Successfully collected"
        ));

        return stats;
    }

    private JPanel createStatCard(
        String heading,
        JLabel value,
        Color accent,
        String description
    ) {

        JPanel card = new JPanel();

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(19, 20, 17, 20)
        ));

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Segoe UI", Font.BOLD, 10));
        title.setForeground(TEXT_MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        value.setFont(new Font("Segoe UI", Font.BOLD, 34));
        value.setForeground(accent);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detail = new JLabel(description);
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detail.setForeground(TEXT_MUTED);
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(12));
        card.add(value);
        card.add(Box.createVerticalStrut(5));
        card.add(detail);

        return card;
    }

    // DONATION TABLE

    private JPanel createDonationTable() {

        JPanel panel = new JPanel(new BorderLayout(12, 16));

        panel.setBackground(WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(18, 18, 15, 18)
        ));

        // TABLE HEADER

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel headingPanel = new JPanel();
        headingPanel.setOpaque(false);
        headingPanel.setLayout(new BoxLayout(
            headingPanel, BoxLayout.Y_AXIS
        ));

        JLabel heading = new JLabel("My Food Donations");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 19));
        heading.setForeground(NAVY);

        JLabel subheading = new JLabel(
            "View and manage all your food donation records"
        );

        subheading.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subheading.setForeground(TEXT_MUTED);

        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(5));
        headingPanel.add(subheading);

        JButton addButton = new JButton("+ Add Donation");
        styleDashboardButton(addButton, GREEN);

        addButton.addActionListener(e -> showDonationForm());

        header.add(headingPanel, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        // TABLE

        String[] columns = {
            "ID",
            "Food Name",
            "Category",
            "Quantity (KG)",
            "Pickup Location",
            "Deadline",
            "Status"
        };

        model = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(42);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(TEXT_DARK);
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(226, 243, 232));
        table.setSelectionForeground(NAVY);
        table.setGridColor(new Color(237, 241, 238));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        table.getTableHeader().setPreferredSize(
            new Dimension(0, 42)
        );

        table.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 11)
        );

        table.getTableHeader().setBackground(
            new Color(239, 246, 241)
        );

        table.getTableHeader().setForeground(NAVY);

        table.getTableHeader().setBorder(
            new MatteBorder(0, 0, 1, 0, BORDER)
        );

        table.getTableHeader().setReorderingAllowed(false);

        // COLORED STATUS TEXT

        table.getColumnModel().getColumn(6).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean selected,
                    boolean focused,
                    int row,
                    int column
                ) {

                    JLabel label = (JLabel)
                        super.getTableCellRendererComponent(
                            table, value, selected, focused, row, column
                        );

                    label.setHorizontalAlignment(
                        SwingConstants.CENTER
                    );

                    label.setFont(
                        new Font("Segoe UI", Font.BOLD, 11)
                    );

                    if (!selected && value != null) {

                        String status = value.toString();

                        if ("AVAILABLE".equalsIgnoreCase(status)) {

                            label.setForeground(
                                new Color(25, 137, 87)
                            );

                            label.setBackground(
                                new Color(231, 247, 237)
                            );

                        } else if ("COLLECTED".equalsIgnoreCase(status)) {

                            label.setForeground(
                                new Color(40, 104, 164)
                            );

                            label.setBackground(
                                new Color(231, 241, 252)
                            );

                        } else {

                            label.setForeground(TEXT_MUTED);
                            label.setBackground(WHITE);
                        }
                    }

                    return label;
                }
            }
        );

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER, 1));
        scroll.getViewport().setBackground(WHITE);

        // ACTION BUTTONS

        JButton refresh = new JButton("↻  Refresh");
        styleDashboardButton(
            refresh, new Color(79, 111, 96)
        );

        refresh.addActionListener(e -> {
            loadDonations();
            loadStatistics();
        });

        JButton delete = new JButton("Delete Selected");
        styleDashboardButton(
            delete, new Color(177, 70, 65)
        );

        delete.addActionListener(e -> deleteDonation());

        JPanel actions = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 9, 0)
        );

        actions.setOpaque(false);
        actions.add(refresh);
        actions.add(delete);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    // BUTTON DESIGN

    private void styleDashboardButton(
        JButton button, Color color
    ) {

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(145, 39));

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    // ADD DONATION FORM

    private void showDonationForm() {

        foodField = new JTextField();
        quantityField = new JTextField();
        phoneField = new JTextField();
        locationField = new JTextField();
        deadlineField = new JTextField();

        categoryBox = new JComboBox<>(new String[]{
            "Vegetarian",
            "Non-Vegetarian",
            "Snacks",
            "Fruits",
            "Bakery",
            "Other"
        });

        JTextField[] fields = {
            foodField,
            quantityField,
            phoneField,
            locationField,
            deadlineField
        };

        for (JTextField field : fields) {
            styleFormField(field);
        }

        categoryBox.setFont(
            new Font("Segoe UI", Font.PLAIN, 13)
        );

        JPanel form = new JPanel(new GridLayout(6, 2, 12, 13));

        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.setBackground(WHITE);

        addFormRow(form, "Food Name:", foodField);
        addFormRow(form, "Category:", categoryBox);
        addFormRow(form, "Quantity (KG):", quantityField);
        addFormRow(form, "Donor Phone:", phoneField);
        addFormRow(form, "Pickup Location:", locationField);
        addFormRow(
            form,
            "Deadline (yyyy-MM-dd HH:mm:ss):",
            deadlineField
        );

        int choice = JOptionPane.showConfirmDialog(
            this,
            form,
            "Add Food Donation",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (choice == JOptionPane.OK_OPTION) {
            saveDonation();
        }
    }

    private void addFormRow(
        JPanel panel, String text, Component component
    ) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_DARK);

        panel.add(label);
        panel.add(component);
    }

    private void styleFormField(JTextField field) {

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(TEXT_DARK);
        field.setBackground(new Color(249, 251, 248));

        field.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(9, 10, 9, 10)
        ));
    }

    // SAVE DONATION

    private void saveDonation() {

        String food = foodField.getText().trim();
        String quantity = quantityField.getText().trim();
        String phone = phoneField.getText().trim();
        String location = locationField.getText().trim();
        String deadline = deadlineField.getText().trim();

        if (food.isEmpty() || quantity.isEmpty()
                || phone.isEmpty() || location.isEmpty()
                || deadline.isEmpty()) {

            showMessage("Please fill in all fields.");
            return;
        }

        if (!phone.matches("[0-9]{10,15}")) {
            showMessage("Enter a valid phone number.");
            return;
        }

        try {

            BigDecimal amount = new BigDecimal(quantity);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showMessage("Quantity must be greater than zero.");
                return;
            }

            Timestamp time = Timestamp.valueOf(deadline);

            String sql = "INSERT INTO food_donations " +
                "(donor_name, donor_phone, food_name, category, " +
                "quantity_kg, pickup_location, deadline) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, donorName);
                ps.setString(2, phone);
                ps.setString(3, food);
                ps.setString(4, (String) categoryBox.getSelectedItem());
                ps.setBigDecimal(5, amount);
                ps.setString(6, location);
                ps.setTimestamp(7, time);

                ps.executeUpdate();
            }

            showMessage("Your food donation has been added!");

            loadDonations();
            loadStatistics();

        } catch (NumberFormatException ex) {

            showMessage("Enter a valid quantity.");

        } catch (IllegalArgumentException ex) {

            showMessage("Use deadline format yyyy-MM-dd HH:mm:ss.");

        } catch (SQLException ex) {

            showError(ex);
        }
    }

    // LOAD DONATIONS

    private void loadDonations() {

        if (model == null) return;

        model.setRowCount(0);

        String sql = "SELECT donation_id, food_name, category, " +
            "quantity_kg, pickup_location, deadline, status " +
            "FROM food_donations WHERE donor_name = ? " +
            "ORDER BY donation_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, donorName);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    model.addRow(new Object[]{

                        rs.getInt("donation_id"),
                        rs.getString("food_name"),
                        rs.getString("category"),
                        rs.getBigDecimal("quantity_kg"),
                        rs.getString("pickup_location"),
                        rs.getTimestamp("deadline"),
                        rs.getString("status")

                    });
                }
            }

        } catch (SQLException ex) {

            showError(ex);
        }
    }

    // LOAD STATISTICS

    private void loadStatistics() {

        if (totalLabel == null) return;

        String sql = "SELECT COUNT(*) AS total, " +
            "SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available, " +
            "SUM(CASE WHEN status = 'COLLECTED' THEN 1 ELSE 0 END) AS collected " +
            "FROM food_donations WHERE donor_name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, donorName);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    totalLabel.setText(
                        String.valueOf(rs.getInt("total"))
                    );

                    availableLabel.setText(
                        String.valueOf(rs.getInt("available"))
                    );

                    collectedLabel.setText(
                        String.valueOf(rs.getInt("collected"))
                    );
                }
            }

        } catch (SQLException ex) {

            showError(ex);
        }
    }

    // DELETE DONATION

    private void deleteDonation() {

        int row = table.getSelectedRow();

        if (row == -1) {
            showMessage("Select a donation first.");
            return;
        }

        int id = (int) model.getValueAt(
            table.convertRowIndexToModel(row), 0
        );

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Delete this donation?",
            "Confirm",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM food_donations " +
            "WHERE donation_id = ? AND donor_name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, donorName);

            ps.executeUpdate();

            showMessage("Donation deleted.");

            loadDonations();
            loadStatistics();

        } catch (SQLException ex) {

            showError(ex);
        }
    }

    // LIVE TRACKING

    private void openLiveTracking() {

        String findSql = """
            SELECT tracking_id
            FROM tracking_sessions
            WHERE donor_name = ?
              AND journey_status NOT IN ('COMPLETED', 'CANCELLED')
            ORDER BY tracking_id DESC
            LIMIT 1
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement findStatement =
                 connection.prepareStatement(findSql)) {

            findStatement.setString(1, donorName);

            long trackingId;

            try (ResultSet result = findStatement.executeQuery()) {

                if (!result.next()) {

                    showMessage(
                        "No active tracking session found. " +
                        "An NGO must collect your donation first."
                    );

                    return;
                }

                trackingId = result.getLong("tracking_id");
            }

            // GENERATE TEMPORARY DONOR ACCESS CODE

            String launchCode = TrackingTokenUtil.generateToken();
            String launchHash = TrackingTokenUtil.hashToken(launchCode);

            String updateSql = """
                UPDATE tracking_sessions
                SET donor_launch_hash = ?,
                    donor_launch_expires_at =
                        DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 15 MINUTE)
                WHERE tracking_id = ?
                  AND donor_name = ?
                """;

            try (PreparedStatement updateStatement =
                     connection.prepareStatement(updateSql)) {

                updateStatement.setString(1, launchHash);
                updateStatement.setLong(2, trackingId);
                updateStatement.setString(3, donorName);

                int updated = updateStatement.executeUpdate();

                if (updated == 0) {

                    showMessage(
                        "Unable to generate donor tracking access."
                    );

                    return;
                }
            }

            String trackingUrl =
                "http://localhost:8080/tracking.html"
                + "#launch=" + launchCode
                + "&trackingId=" + trackingId
                + "&role=DONOR";

            if (!Desktop.isDesktopSupported()
                    || !Desktop.getDesktop().isSupported(
                        Desktop.Action.BROWSE
                    )) {

                showMessage(
                    "Browser is not supported on this system."
                );

                return;
            }

            Desktop.getDesktop().browse(new URI(trackingUrl));

        } catch (Exception ex) {

            ex.printStackTrace();
            showError(ex);
        }
    }

    // MESSAGES

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
}
