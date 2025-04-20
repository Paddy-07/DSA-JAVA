package busroutingsystemproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BusRoutingSystem {
    private static TransportSystem transportSystem = new TransportSystem();
    private static String loggedInUser = "";

    public static void main(String[] args) {
        initializeTransportSystem();
        showLoginPage();
    }

    private static void initializeTransportSystem() {
        for (String city : new String[]{"Coimbatore", "Palakkad", "Chennai", "Bangalore"}) {
            transportSystem.addCity(city);
        }

        transportSystem.addRoute("Coimbatore", "Palakkad", "Premium", 350, 90);
        transportSystem.addRoute("Palakkad", "Chennai", "Economy", 800, 420);
        transportSystem.addRoute("Chennai", "Bangalore", "Premium", 1000, 360);
        transportSystem.addRoute("Bangalore", "Coimbatore", "Economy", 900, 390);
    }

    private static void showLoginPage() {
        JFrame loginFrame = new JFrame("Login Page");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 350);
        loginFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginFrame.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        loginFrame.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginFrame.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginFrame.add(passField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginFrame.add(loginButton, gbc);

        JButton registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginFrame.add(registerButton, gbc);

        JLabel statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        loginFrame.add(statusLabel, gbc);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (validateUser(username, password)) {
                loggedInUser = username;
                loginFrame.dispose();
                showMainTabbedPane();
            } else {
                statusLabel.setText("Invalid username or password. Try again.");
            }
        });

        registerButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (registerUser(username, password)) {
                statusLabel.setText("Registration successful! You can now log in.");
            } else {
                statusLabel.setText("User already exists. Try a different username.");
            }
        });

        loginFrame.setVisible(true);
    }

    private static boolean validateUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].equals(username) && userDetails[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean registerUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].equals(username)) {
                    return false;
                }
            }
        } catch (IOException e) {
            // File not found; new user file will be created
        }

        try (FileWriter writer = new FileWriter("users.csv", true)) {
            writer.write(username + "," + password + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void showMainTabbedPane() {
        JFrame mainFrame = new JFrame("Bus Routing System - Welcome " + loggedInUser);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel bookingPanel = new JPanel(new GridBagLayout());
        setupTicketBookingPanel(bookingPanel);
        tabbedPane.addTab("Ticket Booking", bookingPanel);

        JPanel historyPanel = new JPanel(new BorderLayout());
        setupTransactionHistoryPanel(historyPanel);
        tabbedPane.addTab("Transaction History", historyPanel);

        mainFrame.add(tabbedPane);
        mainFrame.setVisible(true);
    }

    private static void setupTicketBookingPanel(JPanel bookingPanel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel fromLabel = new JLabel("From:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        bookingPanel.add(fromLabel, gbc);

        JComboBox<String> fromComboBox = new JComboBox<>(new String[]{"Coimbatore", "Palakkad", "Chennai", "Bangalore"});
        gbc.gridx = 1;
        gbc.gridy = 0;
        bookingPanel.add(fromComboBox, gbc);

        JLabel toLabel = new JLabel("To:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        bookingPanel.add(toLabel, gbc);

        JComboBox<String> toComboBox = new JComboBox<>(new String[]{"Coimbatore", "Palakkad", "Chennai", "Bangalore"});
        gbc.gridx = 1;
        gbc.gridy = 1;
        bookingPanel.add(toComboBox, gbc);

        JLabel dateLabel = new JLabel("Travel Date:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        bookingPanel.add(dateLabel, gbc);

        JComboBox<String> dateComboBox = new JComboBox<>();
        populateDateOptions(dateComboBox);
        gbc.gridx = 1;
        gbc.gridy = 2;
        bookingPanel.add(dateComboBox, gbc);

        JButton searchButton = new JButton("Search Buses");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bookingPanel.add(searchButton, gbc);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        bookingPanel.add(resultsPanel, gbc);

        JButton bookButton = new JButton("Book Ticket");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bookingPanel.add(bookButton, gbc);

        searchButton.addActionListener(e -> {
            String fromCity = (String) fromComboBox.getSelectedItem();
            String toCity = (String) toComboBox.getSelectedItem();

            if (fromCity.equals(toCity)) {
                JOptionPane.showMessageDialog(bookingPanel, "Source and destination cannot be the same.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            resultsPanel.removeAll();
            TripResult tripResult = transportSystem.calculateBestRoute(fromCity, toCity);

            if (tripResult == null) {
                JLabel noRouteLabel = new JLabel("No routes available from " + fromCity + " to " + toCity + ".");
                resultsPanel.add(noRouteLabel);
            } else {
                for (int i = 0; i < tripResult.route.size() - 1; i++) {
                    String details = "Route: " + tripResult.route.get(i) + " → " + tripResult.route.get(i + 1) +
                            ", Comfort: Premium, Cost: ₹1000, Duration: 4 hrs";
                    JCheckBox busCheckBox = new JCheckBox(details);
                    resultsPanel.add(busCheckBox);
                }
            }
            resultsPanel.revalidate();
            resultsPanel.repaint();
        });

        bookButton.addActionListener(e -> {
            Component[] components = resultsPanel.getComponents();
            ArrayList<String> selectedBuses = new ArrayList<>();

            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        selectedBuses.add(checkBox.getText());
                    }
                }
            }

            if (selectedBuses.isEmpty()) {
                JOptionPane.showMessageDialog(bookingPanel, "No buses selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (FileWriter writer = new FileWriter("bookings.csv", true)) {
                for (String bus : selectedBuses) {
                    writer.write(loggedInUser + "," + bus + "\n");
                }
                JOptionPane.showMessageDialog(bookingPanel, "Tickets booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(bookingPanel, "Error saving bookings. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void populateDateOptions(JComboBox<String> dateComboBox) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (int i = 0; i < 10; i++) {
            dateComboBox.addItem(today.plusDays(i).format(formatter));
        }
    }

    private static void setupTransactionHistoryPanel(JPanel historyPanel) {
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);

        try (BufferedReader reader = new BufferedReader(new FileReader("bookings.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookingDetails = line.split(",");
                historyArea.append("User: " + bookingDetails[0] + " | Bus: " + bookingDetails[1] + "\n");
            }
        } catch (IOException ex) {
            historyArea.setText("Error loading transaction history.");
        }

        JScrollPane scrollPane = new JScrollPane(historyArea);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
    }
}
