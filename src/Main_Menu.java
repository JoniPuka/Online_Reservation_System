import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Main_Menu extends JFrame implements ActionListener {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;

    public Main_Menu() {

        setTitle("Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel titleLabel = new JLabel("Welcome");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);


        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(usernameField, gbc);


        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);


        loginButton = new JButton("Log In");
        loginButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(loginButton, gbc);


        signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(signUpButton, gbc);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (e.getSource() == loginButton) {
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both username and password.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (checkLogin(username, password)) {
                JOptionPane.showMessageDialog(this,
                        "Login successful! Welcome, " + username,
                        "Log In",
                        JOptionPane.INFORMATION_MESSAGE);

                if (!username.equals("admin")) {
                    Dashboard dashboard = new Dashboard(username);
                    dashboard.setVisible(true);


                    this.dispose();
                }
                else{
                    adminDashboard adminDashboard = new adminDashboard(username);
                    adminDashboard.setVisible(true);


                    this.dispose();
                }
            }
            else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Log In Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (e.getSource() == signUpButton) {
        this.dispose();
        new Sign_Up().setVisible(true);
    }

    }
        private boolean checkLogin(String username, String password) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, username);
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main_Menu menu = new Main_Menu();
            menu.setVisible(true);
        });
    }
}