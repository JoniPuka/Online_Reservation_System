import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Dashboard extends JFrame {

    private String username;
    private JTable resv_list;
    private DefaultTableModel tableModel;
    private JButton new_reserv;
    private JButton delete_reserv;
    private JButton refresh_button;
    private JButton exit_button;

    public Dashboard(String username) {
        this.username = username;

        setTitle("Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));


        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label_reserv = new JLabel("Your Reservations");
        label_reserv.setFont(new Font("Arial", Font.PLAIN, 15));
        label_reserv.setBorder(new EmptyBorder(8, 0, 8, 0));
        label_reserv.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(welcomeLabel);
        topPanel.add(label_reserv);


        String[] columnNames = {"Reservation Number","Date", "Origin", "Destination", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resv_list = new JTable(tableModel);
        resv_list.setRowHeight(28);
        resv_list.setFont(new Font("Arial", Font.PLAIN, 13));
        resv_list.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        resv_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resv_list.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(resv_list);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));



        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        new_reserv = new JButton("New Reservation");
        delete_reserv = new JButton("Delete Reservation");
        refresh_button = new JButton("Refresh");
        exit_button = new JButton("Exit");

        Dimension buttonSize = new Dimension(160, 32);
        new_reserv.setPreferredSize(buttonSize);
        delete_reserv.setPreferredSize(buttonSize);
        refresh_button.setPreferredSize(new Dimension(100, 32));
        exit_button.setPreferredSize(new Dimension(100, 32));

        delete_reserv.addActionListener(e -> deleteSelectedReservation());
        refresh_button.addActionListener(e -> loadUsersFromDatabase());
        new_reserv.addActionListener(e -> {
            NewReservationDialog dialog = new NewReservationDialog(this, username);
            dialog.setVisible(true); // blocks here until dialog closes (it's modal)

            if (dialog.isReservationCreated()) {
                loadUsersFromDatabase(); // refresh the table with the new reservation
            }
        });
        exit_button.addActionListener(e -> handleExit());

        leftButtons.add(exit_button);
        rightButtons.add(refresh_button);
        rightButtons.add(delete_reserv);
        rightButtons.add(new_reserv);

        buttonPanel.add(leftButtons, BorderLayout.WEST);
        buttonPanel.add(rightButtons, BorderLayout.EAST);


        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadUsersFromDatabase();
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

    private void loadUsersFromDatabase() {
        String query = "SELECT ticket_id,date, origin_id, destination_id, status FROM ticket WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                tableModel.setRowCount(0); // clear existing rows

                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("ticket_id"),
                            rs.getDate("date"),
                            rs.getString("origin_id"),
                            rs.getString("destination_id"),
                            rs.getString("status")
                    };
                    tableModel.addRow(row);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading reservations: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
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

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this reservation?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String deleteQuery = "DELETE FROM ticket WHERE ticket_id = ? AND username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, id);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                loadUsersFromDatabase(); // refresh table
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


}