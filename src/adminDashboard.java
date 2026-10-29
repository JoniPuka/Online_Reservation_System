import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class adminDashboard extends JFrame {

    private String adminUsername;

    private JTable resv_list;
    private DefaultTableModel reservationModel;

    private JTable user_list;
    private DefaultTableModel userModel;

    private JButton refresh_button;
    private JButton delete_reserv;
    private JButton delete_user;
    private JButton exit_button;

    private JTable location_list;
    private DefaultTableModel locationModel;

    private JButton add_location;
    private JButton delete_location;

    public adminDashboard(String adminUsername) {
        this.adminUsername = adminUsername;

        setTitle("Admin Dashboard");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));


        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Admin Panel — " + adminUsername);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Manage reservations and users");
        subLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subLabel.setForeground(Color.DARK_GRAY);
        subLabel.setBorder(new EmptyBorder(6, 0, 8, 0));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(welcomeLabel);
        topPanel.add(subLabel);


        JTabbedPane tabs = new JTabbedPane();

        // Reservations tab
        String[] reservationCols = {"Ticket ID", "Username", "Origin", "Destination", "Status", "Date"};
        reservationModel = new DefaultTableModel(reservationCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resv_list = new JTable(reservationModel);
        resv_list.setRowHeight(26);
        resv_list.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        resv_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resv_list.setFillsViewportHeight(true);
        JScrollPane reservationScroll = new JScrollPane(resv_list);
        reservationScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel reservationTab = new JPanel(new BorderLayout(0, 10));
        reservationTab.setBorder(new EmptyBorder(10, 0, 0, 0));
        reservationTab.add(reservationScroll, BorderLayout.CENTER);

        JPanel reservationButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        delete_reserv = new JButton("Delete Reservation");
        delete_reserv.setPreferredSize(new Dimension(170, 32));
        delete_reserv.addActionListener(e -> deleteSelectedReservation());
        reservationButtons.add(delete_reserv);
        reservationTab.add(reservationButtons, BorderLayout.SOUTH);

        tabs.addTab("Reservations", reservationTab);


        String[] userCols = {"ID", "Username", "Role ID"};
        userModel = new DefaultTableModel(userCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        user_list = new JTable(userModel);
        user_list.setRowHeight(26);
        user_list.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        user_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        user_list.setFillsViewportHeight(true);
        JScrollPane userScroll = new JScrollPane(user_list);
        userScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel userTab = new JPanel(new BorderLayout(0, 10));
        userTab.setBorder(new EmptyBorder(10, 0, 0, 0));
        userTab.add(userScroll, BorderLayout.CENTER);

        JPanel userButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        delete_user = new JButton("Delete User");
        delete_user.setPreferredSize(new Dimension(170, 32));
        delete_user.addActionListener(e -> deleteSelectedUser());
        userButtons.add(delete_user);
        userTab.add(userButtons, BorderLayout.SOUTH);

        tabs.addTab("Users", userTab);


        String[] locationCols = { "Location"};
        locationModel = new DefaultTableModel(locationCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        location_list = new JTable(locationModel);
        location_list.setRowHeight(26);
        location_list.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        location_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        location_list.setFillsViewportHeight(true);

        JScrollPane locationScroll = new JScrollPane(location_list);
        locationScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel locationTab = new JPanel(new BorderLayout(0, 10));
        locationTab.setBorder(new EmptyBorder(10, 0, 0, 0));
        locationTab.add(locationScroll, BorderLayout.CENTER);

        JPanel locationButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        add_location = new JButton("Add Location");
        add_location.setPreferredSize(new Dimension(150, 32));
        add_location.addActionListener(e -> addLocation());

        delete_location = new JButton("Delete Location");
        delete_location.setPreferredSize(new Dimension(150, 32));
        delete_location.addActionListener(e -> deleteSelectedLocation());

        locationButtons.add(add_location);
        locationButtons.add(delete_location);

        locationTab.add(locationButtons, BorderLayout.SOUTH);

        tabs.addTab("Locations", locationTab);


        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        exit_button = new JButton("Exit");
        exit_button.setPreferredSize(new Dimension(100, 32));
        exit_button.addActionListener(e -> handleExit());

        refresh_button = new JButton("Refresh");
        refresh_button.setPreferredSize(new Dimension(100, 32));
        refresh_button.addActionListener(e -> {
            loadReservations();
            loadUsers();
            loadLocations();
        });

        leftButtons.add(exit_button);
        rightButtons.add(refresh_button);

        bottomPanel.add(leftButtons, BorderLayout.WEST);
        bottomPanel.add(rightButtons, BorderLayout.EAST);


        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadReservations();
        loadUsers();
        loadLocations();
    }

    private void loadReservations() {
        String query = "SELECT ticket_id, username, origin_id, destination_id, status, date FROM ticket";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            reservationModel.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("ticket_id"),
                        rs.getString("username"),
                        rs.getString("origin_id"),
                        rs.getString("destination_id"),
                        rs.getString("status"),
                        rs.getDate("date")
                };
                reservationModel.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading reservations: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsers() {
        String query = "SELECT  username,password, role_id FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            userModel.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("role_id")
                };
                userModel.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading users: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLocations() {
        String query = "SELECT location_name FROM locations";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            locationModel.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                        rs.getString("location_name")
                };

                locationModel.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "Error loading locations: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void addLocation() {

        String locationName = JOptionPane.showInputDialog(
                this,
                "Enter location name:",
                "Add Location",
                JOptionPane.PLAIN_MESSAGE
        );

        if (locationName == null || locationName.trim().isEmpty()) {
            return;
        }

        String query = "INSERT INTO locations (location_name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, locationName.trim());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                loadLocations();

                JOptionPane.showMessageDialog(
                        this,
                        "Location added successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding location: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void deleteSelectedLocation() {

        int selectedRow = location_list.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a location to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }



        String locationName =
                (String) locationModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete location \"" + locationName + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String deleteQuery =
                "DELETE FROM locations WHERE location_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setString(1, locationName);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                loadLocations();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Location not found.",
                        "Delete Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting location: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteSelectedReservation() {
        int selectedRow = resv_list.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reservation to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ticketId = (int) reservationModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this reservation? This cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String deleteQuery = "DELETE FROM ticket WHERE ticket_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, ticketId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                loadReservations();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No matching reservation found to delete.",
                        "Delete Failed",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting reservation: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = user_list.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String usernameToDelete = (String) userModel.getValueAt(selectedRow, 1);

        if (usernameToDelete.equals(adminUsername)) {
            JOptionPane.showMessageDialog(this,
                    "You cannot delete your own admin account.",
                    "Action Blocked",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete user \"" + usernameToDelete + "\"? This cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String deleteQuery = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setString(1, usernameToDelete);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No matching user found to delete.",
                        "Delete Failed",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting user: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Main_Menu dialog = new Main_Menu();
            dialog.setVisible(true);
            this.dispose();
        }
    }



}