
package com.shareplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

public class SharePlateApp extends JFrame {

    private JTextField donorNameField, donorPhoneField;
    private JTextField foodNameField, quantityField;
    private JTextField locationField, deadlineField;

    private JTextField ngoNameField, contactPersonField;
    private JTextField ngoPhoneField, emailField, addressField;
    private JTextField searchNgoField;

    private JComboBox<String> categoryBox;

    private JTable donationTable, ngoTable;
    private DefaultTableModel donationModel, ngoModel;

    public SharePlateApp() {
        setTitle("SharePlate - Food Surplus Donation Manager");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createGUI();
        loadDonations();
        loadNGOs();
    }

    private void createGUI() {

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Food Donations", createDonationPanel());
        tabs.addTab("NGO Management", createNGOPanel());

        add(tabs);
    }

    // ---------------- FOOD DONATION PANEL ----------------

    private JPanel createDonationPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel(
            "SharePlate - Food Surplus Donation Manager",
            JLabel.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(new Color(30, 120, 70));

        panel.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        form.setBorder(
            BorderFactory.createTitledBorder("Food Donation Details")
        );

        donorNameField = new JTextField();
        donorPhoneField = new JTextField();
        foodNameField = new JTextField();
        quantityField = new JTextField();
        locationField = new JTextField();
        deadlineField = new JTextField();

        categoryBox = new JComboBox<>(new String[]{
            "Vegetarian", "Non-Vegetarian", "Snacks",
            "Fruits", "Bakery", "Other"
        });

        form.add(new JLabel("Donor Name:"));
        form.add(donorNameField);

        form.add(new JLabel("Donor Phone:"));
        form.add(donorPhoneField);

        form.add(new JLabel("Food Name:"));
        form.add(foodNameField);

        form.add(new JLabel("Category:"));
        form.add(categoryBox);

        form.add(new JLabel("Quantity (KG):"));
        form.add(quantityField);

        form.add(new JLabel("Pickup Location:"));
        form.add(locationField);

        form.add(new JLabel("Deadline (yyyy-MM-dd HH:mm:ss):"));
        form.add(deadlineField);

        JButton addButton = new JButton("Add Donation");
        JButton refreshButton = new JButton("Refresh");
        JButton collectButton = new JButton("Mark Collected");
        JButton deleteButton = new JButton("Delete Donation");

        form.add(addButton);
        form.add(refreshButton);

        JPanel actions = new JPanel();
        actions.add(collectButton);
        actions.add(deleteButton);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.add(form, BorderLayout.CENTER);
        top.add(actions, BorderLayout.SOUTH);

        String[] columns = {
            "ID", "Donor Name", "Donor Phone", "Food Name",
            "Category", "Quantity KG", "Pickup Location",
            "Deadline", "Status"
        };

        donationModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        donationTable = new JTable(donationModel);
        donationTable.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(donationTable);
        scroll.setBorder(
            BorderFactory.createTitledBorder("Food Donations")
        );

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        addButton.addActionListener(e -> addDonation());
        refreshButton.addActionListener(e -> loadDonations());
        collectButton.addActionListener(e -> markCollected());
        deleteButton.addActionListener(e -> deleteDonation());

        return panel;
    }

    private void addDonation() {

        String donor = donorNameField.getText().trim();
        String phone = donorPhoneField.getText().trim();
        String food = foodNameField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String quantity = quantityField.getText().trim();
        String location = locationField.getText().trim();
        String deadline = deadlineField.getText().trim();

        if (donor.isEmpty() || phone.isEmpty() || food.isEmpty()
                || quantity.isEmpty() || location.isEmpty()
                || deadline.isEmpty()) {
            showMessage("Please fill in all donation fields.");
            return;
        }

        if (!phone.matches("[0-9]{10,15}")) {
            showMessage("Enter a valid donor phone number.");
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

                ps.setString(1, donor);
                ps.setString(2, phone);
                ps.setString(3, food);
                ps.setString(4, category);
                ps.setBigDecimal(5, amount);
                ps.setString(6, location);
                ps.setTimestamp(7, time);

                ps.executeUpdate();
            }

            showMessage("Food donation added successfully!");

            donorNameField.setText("");
            donorPhoneField.setText("");
            foodNameField.setText("");
            quantityField.setText("");
            locationField.setText("");
            deadlineField.setText("");
            categoryBox.setSelectedIndex(0);

            loadDonations();

        } catch (NumberFormatException ex) {
            showMessage("Enter a valid quantity.");
        } catch (IllegalArgumentException ex) {
            showMessage("Invalid deadline. Use yyyy-MM-dd HH:mm:ss.");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadDonations() {

        if (donationModel == null) return;

        donationModel.setRowCount(0);

        String sql = "SELECT * FROM food_donations " +
            "ORDER BY donation_id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                donationModel.addRow(new Object[]{
                    rs.getInt("donation_id"),
                    rs.getString("donor_name"),
                    rs.getString("donor_phone"),
                    rs.getString("food_name"),
                    rs.getString("category"),
                    rs.getBigDecimal("quantity_kg"),
                    rs.getString("pickup_location"),
                    rs.getTimestamp("deadline"),
                    rs.getString("status")
                });
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void markCollected() {

        int row = donationTable.getSelectedRow();

        if (row == -1) {
            showMessage("Select a donation first.");
            return;
        }

        int id = (int) donationModel.getValueAt(row, 0);

        String sql = "UPDATE food_donations " +
            "SET status = 'COLLECTED' WHERE donation_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            showMessage("Donation marked as collected!");
            loadDonations();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteDonation() {

        int row = donationTable.getSelectedRow();

        if (row == -1) {
            showMessage("Select a donation first.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Delete selected donation?",
            "Confirm",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) return;

        int id = (int) donationModel.getValueAt(row, 0);

        String sql = "DELETE FROM food_donations WHERE donation_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            showMessage("Donation deleted successfully!");
            loadDonations();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // ---------------- NGO MANAGEMENT PANEL ----------------

    private JPanel createNGOPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel(
            "SharePlate - NGO Registration Management",
            JLabel.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(new Color(30, 120, 70));

        panel.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));

        form.setBorder(
            BorderFactory.createTitledBorder("NGO Registration Details")
        );

        ngoNameField = new JTextField();
        contactPersonField = new JTextField();
        ngoPhoneField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextField();

        form.add(new JLabel("NGO Name:"));
        form.add(ngoNameField);

        form.add(new JLabel("Contact Person:"));
        form.add(contactPersonField);

        form.add(new JLabel("Phone Number:"));
        form.add(ngoPhoneField);

        form.add(new JLabel("Email Address:"));
        form.add(emailField);

        form.add(new JLabel("NGO Address:"));
        form.add(addressField);

        JButton registerButton = new JButton("Register NGO");
        JButton refreshButton = new JButton("Refresh NGOs");
        JButton deleteButton = new JButton("Delete NGO");

        form.add(registerButton);
        form.add(refreshButton);

        searchNgoField = new JTextField(20);

        JButton searchButton = new JButton("Search NGO");

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search by NGO Name:"));
        searchPanel.add(searchNgoField);
        searchPanel.add(searchButton);
        searchPanel.add(deleteButton);

        String[] columns = {
            "NGO ID", "NGO Name", "Contact Person",
            "Phone", "Email", "Address"
        };

        ngoModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ngoTable = new JTable(ngoModel);
        ngoTable.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(ngoTable);

        scroll.setBorder(
            BorderFactory.createTitledBorder("Registered NGOs")
        );

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.add(form, BorderLayout.CENTER);
        top.add(searchPanel, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        registerButton.addActionListener(e -> registerNGO());
        refreshButton.addActionListener(e -> loadNGOs());
        searchButton.addActionListener(e -> searchNGO());
        deleteButton.addActionListener(e -> deleteNGO());

        return panel;
    }

    private void registerNGO() {

        String name = ngoNameField.getText().trim();
        String contact = contactPersonField.getText().trim();
        String phone = ngoPhoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || contact.isEmpty()
                || phone.isEmpty() || address.isEmpty()) {
            showMessage("Please fill in all required NGO fields.");
            return;
        }

        if (!phone.matches("[0-9]{10,15}")) {
            showMessage("Enter a valid NGO phone number.");
            return;
        }

        if (!email.isEmpty()
                && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showMessage("Enter a valid email address.");
            return;
        }

        String sql = "INSERT INTO ngos " +
            "(ngo_name, contact_person, phone, email, address) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setString(3, phone);
            ps.setString(4, email.isEmpty() ? null : email);
            ps.setString(5, address);

            ps.executeUpdate();

            showMessage("NGO registered successfully!");

            ngoNameField.setText("");
            contactPersonField.setText("");
            ngoPhoneField.setText("");
            emailField.setText("");
            addressField.setText("");

            loadNGOs();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadNGOs() {

        if (ngoModel == null) return;

        ngoModel.setRowCount(0);

        String sql = "SELECT * FROM ngos ORDER BY ngo_id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ngoModel.addRow(new Object[]{
                    rs.getInt("ngo_id"),
                    rs.getString("ngo_name"),
                    rs.getString("contact_person"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                });
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void searchNGO() {

        String keyword = searchNgoField.getText().trim();

        ngoModel.setRowCount(0);

        String sql = "SELECT * FROM ngos " +
            "WHERE ngo_name LIKE ? ORDER BY ngo_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    ngoModel.addRow(new Object[]{
                        rs.getInt("ngo_id"),
                        rs.getString("ngo_name"),
                        rs.getString("contact_person"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                    });
                }
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteNGO() {

        int row = ngoTable.getSelectedRow();

        if (row == -1) {
            showMessage("Select an NGO first.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Delete selected NGO?",
            "Confirm",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) return;

        int id = (int) ngoModel.getValueAt(row, 0);

        String sql = "DELETE FROM ngos WHERE ngo_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            showMessage("NGO deleted successfully!");
            loadNGOs();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // ---------------- COMMON METHODS ----------------

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
    LoginFrame.main(args);
}
}
