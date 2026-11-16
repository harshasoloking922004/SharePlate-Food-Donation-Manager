
package com.shareplate;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.net.URI;
import java.sql.*;

public class NGODashboard extends JFrame {

    private final String ngoName;

    private JTable donationTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private JLabel totalLabel;
    private JLabel availableLabel;
    private JLabel collectedLabel;

    // DESIGN COLORS

    private final Color NAVY = new Color(17, 48, 48);
    private final Color TEAL = new Color(22, 105, 91);
    private final Color GREEN = new Color(39, 157, 112);
    private final Color BACKGROUND = new Color(244, 247, 245);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT_DARK = new Color(38, 57, 51);
    private final Color TEXT_MUTED = new Color(125, 143, 134);
    private final Color BORDER = new Color(225, 233, 228);

    public NGODashboard(String ngoName) {

        this.ngoName = ngoName;

        setTitle("SharePlate | NGO Dashboard");
        setSize(1250, 780);
        setMinimumSize(new Dimension(1050, 650));
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
        root.add(createMainPanel(), BorderLayout.CENTER);

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

        addSidebarItem(
            sidebar, "▦   Available Food", this::showAvailableFood
        );

        addSidebarItem(
            sidebar, "▤   My Collections", this::showCollected
        );

        addSidebarItem(
            sidebar, "↻   Refresh", this::refreshDashboard
        );

        addSidebarItem(
            sidebar, "⌖   Live Tracking", this::openLiveTracking
        );

        sidebar.add(Box.createVerticalGlue());

        // PROFILE CARD

        JPanel profile = new JPanel();
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
        profile.setBackground(new Color(29, 70, 62));
        profile.setBorder(new EmptyBorder(14, 12, 14, 12));
        profile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        profile.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel profileName = new JLabel("  " + ngoName);
        profileName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        profileName.setForeground(Color.WHITE);

        JLabel profileRole = new JLabel("  NGO / Volunteer");
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

    private JPanel createMainPanel() {

        JPanel main = new JPanel(new BorderLayout(20, 22));
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(28, 30, 25, 30));

        // HEADER

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel headings = new JPanel();
        headings.setOpaque(false);
        headings.setLayout(new BoxLayout(headings, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Food Rescue Marketplace");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(NAVY);

        JLabel welcome = new JLabel(
            "Welcome back, " + ngoName
            + " | Help good food reach people in need."
        );

        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        welcome.setForeground(TEXT_MUTED);

        headings.add(title);
        headings.add(Box.createVerticalStrut(7));
        headings.add(welcome);

        header.add(headings, BorderLayout.WEST);

        // STATISTICS

        JPanel stats = createStatsPanel();

        JPanel top = new JPanel(new BorderLayout(15, 20));
        top.setOpaque(false);

        top.add(header, BorderLayout.NORTH);
        top.add(stats, BorderLayout.CENTER);

        // SEARCH

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField();

        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(TEXT_DARK);
        searchField.setBackground(WHITE);
        searchField.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));

        searchField.setToolTipText(
            "Search by food name or category"
        );

        JButton searchButton = new JButton("Search");
        styleButton(searchButton, TEAL, 105);

        searchButton.addActionListener(e -> loadDonations());
        searchField.addActionListener(e -> loadDonations());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // TABLE

        String[] columns = {
            "ID", "Donor", "Food Name", "Category",
            "Quantity (kg)", "Pickup Location",
            "Deadline", "Status", "Collected By"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        donationTable = new JTable(tableModel);

        donationTable.setRowHeight(42);
        donationTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        donationTable.setForeground(TEXT_DARK);
        donationTable.setBackground(WHITE);
        donationTable.setSelectionBackground(new Color(226, 243, 232));
        donationTable.setSelectionForeground(NAVY);
        donationTable.setGridColor(new Color(237, 241, 238));
        donationTable.setShowVerticalLines(false);
        donationTable.setShowHorizontalLines(true);
        donationTable.setIntercellSpacing(new Dimension(0, 1));
        donationTable.setFillsViewportHeight(true);
        donationTable.setAutoCreateRowSorter(true);
        donationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        donationTable.getTableHeader().setPreferredSize(
            new Dimension(0, 42)
        );

        donationTable.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 11)
        );

        donationTable.getTableHeader().setBackground(
            new Color(239, 246, 241)
        );

        donationTable.getTableHeader().setForeground(NAVY);
        donationTable.getTableHeader().setReorderingAllowed(false);

        int[] widths = {
            50, 115, 125, 110, 100, 190, 155, 105, 145
        };

        for (int i = 0; i < widths.length; i++) {
            donationTable.getColumnModel()
                .getColumn(i).setPreferredWidth(widths[i]);
        }

        // STATUS DESIGN

        donationTable.getColumnModel().getColumn(7)
            .setCellRenderer(new DefaultTableCellRenderer() {

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
            });

        JScrollPane scrollPane = new JScrollPane(donationTable);
        scrollPane.setBorder(new LineBorder(BORDER, 1));
        scrollPane.getViewport().setBackground(WHITE);

        // COLLECT BUTTON

        JButton collectButton = new JButton("Collect Selected Food");
        styleButton(collectButton, GREEN, 190);

        collectButton.addActionListener(e -> collectDonation());

        JPanel bottom = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 0, 0)
        );

        bottom.setOpaque(false);
        bottom.add(collectButton);

        // TABLE CARD

        JPanel tableCard = new JPanel(new BorderLayout(12, 15));
        tableCard.setBackground(WHITE);
        tableCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(18, 18, 15, 18)
        ));

        JPanel tableHeading = new JPanel();
        tableHeading.setOpaque(false);
        tableHeading.setLayout(new BoxLayout(
            tableHeading, BoxLayout.Y_AXIS
        ));

        JLabel tableTitle = new JLabel("Available Food Donations");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        tableTitle.setForeground(NAVY);

        JLabel tableSubtitle = new JLabel(
            "Discover surplus food and arrange collections"
        );

        tableSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tableSubtitle.setForeground(TEXT_MUTED);

        tableHeading.add(tableTitle);
        tableHeading.add(Box.createVerticalStrut(5));
        tableHeading.add(tableSubtitle);

        JPanel tableTop = new JPanel(new BorderLayout(10, 12));
        tableTop.setOpaque(false);

        tableTop.add(tableHeading, BorderLayout.NORTH);
        tableTop.add(searchPanel, BorderLayout.CENTER);

        tableCard.add(tableTop, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        tableCard.add(bottom, BorderLayout.SOUTH);

        main.add(top, BorderLayout.NORTH);
        main.add(tableCard, BorderLayout.CENTER);

        return main;
    }

    // STATISTICS

    private JPanel createStatsPanel() {

        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 0));
        panel.setOpaque(false);

        totalLabel = new JLabel("0");
        availableLabel = new JLabel("0");
        collectedLabel = new JLabel("0");

        panel.add(createStatCard(
            "TOTAL DONATIONS",
            totalLabel,
            new Color(44, 112, 96),
            "All food donation records"
        ));

        panel.add(createStatCard(
            "AVAILABLE FOOD",
            availableLabel,
            GREEN,
            "Ready for collection"
        ));

        panel.add(createStatCard(
            "COLLECTED FOOD",
            collectedLabel,
            new Color(205, 139, 57),
            "Successfully collected"
        ));

        return panel;
    }

    private JPanel createStatCard(
        String title,
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

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 10));
        heading.setForeground(TEXT_MUTED);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        value.setFont(new Font("Segoe UI", Font.BOLD, 34));
        value.setForeground(accent);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detail = new JLabel(description);
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detail.setForeground(TEXT_MUTED);
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(heading);
        card.add(Box.createVerticalStrut(12));
        card.add(value);
        card.add(Box.createVerticalStrut(5));
        card.add(detail);

        return card;
    }

    // BUTTON DESIGN

    private void styleButton(
        JButton button, Color color, int width
    ) {

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, 39));

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    // AVAILABLE FOOD

    private void showAvailableFood() {

        searchField.setText("");
        loadDonations();
    }

    // REFRESH

    private void refreshDashboard() {

        searchField.setText("");
        loadDonations();
        loadStatistics();
    }

    // LOAD AVAILABLE DONATIONS

    private void loadDonations() {

        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        String keyword = searchField == null
            ? ""
            : searchField.getText().trim();

        String sql = """
            SELECT donation_id, donor_name, food_name, category,
                   quantity_kg, pickup_location, deadline, status,
                   collected_by
            FROM food_donations
            WHERE status = 'AVAILABLE'
              AND (food_name LIKE ? OR category LIKE ?)
            ORDER BY donation_id DESC
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql)) {

            statement.setString(1, "%" + keyword + "%");
            statement.setString(2, "%" + keyword + "%");

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    tableModel.addRow(new Object[]{

                        result.getInt("donation_id"),
                        result.getString("donor_name"),
                        result.getString("food_name"),
                        result.getString("category"),
                        result.getBigDecimal("quantity_kg"),
                        result.getString("pickup_location"),
                        result.getTimestamp("deadline"),
                        result.getString("status"),
                        result.getString("collected_by")

                    });
                }
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // MY COLLECTIONS

    private void showCollected() {

        tableModel.setRowCount(0);

        String sql = """
            SELECT donation_id, donor_name, food_name, category,
                   quantity_kg, pickup_location, deadline, status,
                   collected_by
            FROM food_donations
            WHERE status = 'COLLECTED'
              AND collected_by = ?
            ORDER BY donation_id DESC
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql)) {

            statement.setString(1, ngoName);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    tableModel.addRow(new Object[]{

                        result.getInt("donation_id"),
                        result.getString("donor_name"),
                        result.getString("food_name"),
                        result.getString("category"),
                        result.getBigDecimal("quantity_kg"),
                        result.getString("pickup_location"),
                        result.getTimestamp("deadline"),
                        result.getString("status"),
                        result.getString("collected_by")

                    });
                }
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // COLLECT DONATION

    private void collectDonation() {

        int selectedRow = donationTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a food donation."
            );

            return;
        }

        int modelRow =
            donationTable.convertRowIndexToModel(selectedRow);

        int donationId = (int) tableModel.getValueAt(modelRow, 0);

        String donorName = String.valueOf(
            tableModel.getValueAt(modelRow, 1)
        );

        String deliveryAddress = JOptionPane.showInputDialog(
            this,
            "Enter the custom delivery address:",
            "Delivery Address",
            JOptionPane.QUESTION_MESSAGE
        );

        if (deliveryAddress == null) {
            return;
        }

        deliveryAddress = deliveryAddress.trim();

        if (deliveryAddress.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Delivery address cannot be empty."
            );

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Collect this donation?\n\nDelivery Address:\n"
                + deliveryAddress,
            "Confirm Food Collection",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = """
            UPDATE food_donations
            SET status = 'COLLECTED', collected_by = ?
            WHERE donation_id = ? AND status = 'AVAILABLE'
            """;

        try (Connection connection = DBConnection.getConnection()) {

            boolean originalAutoCommit = connection.getAutoCommit();

            try {

                connection.setAutoCommit(false);

                try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                    statement.setString(1, ngoName);
                    statement.setInt(2, donationId);

                    int updated = statement.executeUpdate();

                    if (updated == 0) {

                        connection.rollback();

                        JOptionPane.showMessageDialog(
                            this,
                            "This donation is no longer available."
                        );

                        loadDonations();
                        loadStatistics();

                        return;
                    }
                }

                TrackingService trackingService = new TrackingService();

                TrackingService.TrackingSessionCredentials credentials =
                    trackingService.createTrackingSessionWithTokens(
                        connection,
                        donationId,
                        donorName,
                        ngoName,
                        deliveryAddress,
                        null,
                        null,
                        null,
                        null
                    );

                connection.commit();

                JOptionPane.showMessageDialog(
                    this,
                    "Food collected and tracking session created!"
                        + "\n\nTracking ID: "
                        + credentials.trackingId()
                        + "\n\nDONOR TOKEN (share securely with donor):"
                        + "\n" + credentials.donorToken()
                        + "\n\nNGO tracking access:"
                        + "\nClick Live Tracking in the sidebar."
                        + "\nA temporary secure access link will be generated."
                        + "\n\nDelivery Address:"
                        + "\n" + deliveryAddress,
                    "Collection Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );

                loadDonations();
                loadStatistics();

            } catch (SQLException e) {

                connection.rollback();
                showDatabaseError(e);

            } finally {

                connection.setAutoCommit(originalAutoCommit);
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // LOAD STATISTICS

    private void loadStatistics() {

        if (totalLabel == null) {
            return;
        }

        String sql = """
            SELECT COUNT(*) AS total,
                   SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available,
                   SUM(CASE WHEN status = 'COLLECTED' THEN 1 ELSE 0 END) AS collected
            FROM food_donations
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {

                totalLabel.setText(
                    String.valueOf(result.getInt("total"))
                );

                availableLabel.setText(
                    String.valueOf(result.getInt("available"))
                );

                collectedLabel.setText(
                    String.valueOf(result.getInt("collected"))
                );
            }

        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // GENERATE TEMPORARY NGO TRACKING CODE

    private String generateNgoLaunchCode(
        Connection connection,
        long trackingId
    ) throws SQLException {

        String launchCode = TrackingTokenUtil.generateToken();
        String launchHash = TrackingTokenUtil.hashToken(launchCode);

        String sql = """
            UPDATE tracking_sessions
            SET ngo_launch_hash = ?,
                ngo_launch_expires_at =
                    DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 15 MINUTE)
            WHERE tracking_id = ?
              AND ngo_name = ?
            """;

        try (PreparedStatement statement =
                 connection.prepareStatement(sql)) {

            statement.setString(1, launchHash);
            statement.setLong(2, trackingId);
            statement.setString(3, ngoName);

            int updated = statement.executeUpdate();

            if (updated != 1) {

                throw new SQLException(
                    "Could not create the temporary tracking link."
                );
            }
        }

        return launchCode;
    }

    // LIVE TRACKING

    private void openLiveTracking() {

        String sql = """
            SELECT tracking_id
            FROM tracking_sessions
            WHERE ngo_name = ?
              AND journey_status NOT IN ('COMPLETED', 'CANCELLED')
            ORDER BY tracking_id DESC
            LIMIT 1
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                 connection.prepareStatement(sql)) {

            statement.setString(1, ngoName);

            long trackingId;

            try (ResultSet result = statement.executeQuery()) {

                if (!result.next()) {

                    JOptionPane.showMessageDialog(
                        this,
                        "No active tracking session found.\n"
                            + "Collect a food donation first.",
                        "Live Tracking",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    return;
                }

                trackingId = result.getLong("tracking_id");
            }

            String launchCode = generateNgoLaunchCode(
                connection,
                trackingId
            );

            String trackingUrl =
                "http://localhost:8080/tracking.html"
                + "#launch=" + launchCode
                + "&trackingId=" + trackingId
                + "&role=NGO";

            if (!Desktop.isDesktopSupported()
                    || !Desktop.getDesktop().isSupported(
                        Desktop.Action.BROWSE
                    )) {

                JOptionPane.showMessageDialog(
                    this,
                    "Browser is not supported on this system."
                );

                return;
            }

            Desktop.getDesktop().browse(new URI(trackingUrl));

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                this,
                "Unable to open Live Tracking: " + e.getMessage(),
                "SharePlate Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // DATABASE ERROR

    private void showDatabaseError(SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "Database error: " + e.getMessage(),
            "SharePlate Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
