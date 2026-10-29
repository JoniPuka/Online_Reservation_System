import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class NewReservationDialog extends JDialog {

    private String username;
    private JComboBox<String> originCombo;
    private JComboBox<String> destinationCombo;
    private JSpinner dateSpinner;
    private JButton confirmButton;
    private JButton cancelButton;

    private boolean reservationCreated = false; // lets Dashboard know whether to refresh

    public NewReservationDialog(JFrame parent, String username) {
        super(parent, "New Reservation", true); // true = modal, blocks parent until closed
        this.username = username;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Book a New Reservation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel originLabel = new JLabel("From:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(originLabel, gbc);

        originCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(originCombo, gbc);

        JLabel destinationLabel = new JLabel("To:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(destinationLabel, gbc);

        destinationCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(destinationCombo, gbc);

        JLabel dateLabel = new JLabel("Date:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(dateLabel, gbc);

        Calendar today = Calendar.getInstance();
        Calendar minDate = (Calendar) today.clone();
        SpinnerDateModel dateModel = new SpinnerDateModel(today.getTime(), minDate.getTime(), null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(dateSpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 0, 8);
        panel.add(buttonPanel, gbc);

        add(panel);

        loadStations();

        confirmButton.addActionListener(e -> handleConfirm());
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadStations() {
        String query = "SELECT location_name FROM locations ORDER BY location_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("location_name");
                originCombo.addItem(name);
                destinationCombo.addItem(name);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading locations: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleConfirm() {
        String originName = (String) originCombo.getSelectedItem();
        String destinationName = (String) destinationCombo.getSelectedItem();
        java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();

        if (originName == null || destinationName == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select both origin and destination.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (originName.equals(destinationName)) {
            JOptionPane.showMessageDialog(this,
                    "Origin and destination cannot be the same.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String insertQuery = "INSERT INTO ticket (username, origin_id, destination_id, status, date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, username);
            stmt.setString(2, originName);
            stmt.setString(3, destinationName);
            stmt.setString(4, "active"); // default status — adjust to your schema
            stmt.setDate(5, new Date(selectedDate.getTime())); // java.sql.Date for JDBC

            stmt.executeUpdate();

            reservationCreated = true;
            JOptionPane.showMessageDialog(this,
                    "Reservation created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error creating reservation: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isReservationCreated() {
        return reservationCreated;
    }
}