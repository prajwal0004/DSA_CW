import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class BookingSystemGUI {
    private final int rows = 5;
    private final int cols = 5;
    private final BookingManager manager = new BookingManager(rows, cols);
    private final JButton[][] seatButtons = new JButton[rows][cols];
    private boolean useOptimistic = true;
    private JLabel statusLabel;

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Online Ticket Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Status label
        statusLabel = new JLabel("Ready to process bookings");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Seat grid panel
        JPanel seatPanel = new JPanel(new GridLayout(rows, cols, 2, 2));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton("O");
                btn.setFont(new Font("Arial", Font.BOLD, 14));
                final int row = r;
                final int col = c;
                btn.addActionListener(e -> {
                    if (!manager.getSeats()[row][col].isBooked()) {
                        manager.addRequest(new BookingRequest("User", row, col));
                        statusLabel.setText("Added booking request for seat (" + row + "," + col + ")");
                        refreshSeats();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Seat (" + row + "," + col + ") is already booked!");
                    }
                });
                seatButtons[r][c] = btn;
                seatPanel.add(btn);
            }
        }

        // Control buttons
        JButton generateRequests = new JButton("Add Random Requests");
        generateRequests.addActionListener(e -> {
            Random rand = new Random();
            int addedCount = 0;
            for (int i = 0; i < 5; i++) {
                int r = rand.nextInt(rows);
                int c = rand.nextInt(cols);
                if (!manager.getSeats()[r][c].isBooked()) {
                    manager.addRequest(new BookingRequest("User" + i, r, c));
                    addedCount++;
                }
            }
            statusLabel.setText("Added " + addedCount + " random booking requests");
            refreshSeats();
            JOptionPane.showMessageDialog(frame, "Added " + addedCount + " random requests!");
        });

        JButton processButton = new JButton("Process Bookings");
        processButton.addActionListener(e -> {
            processButton.setEnabled(false);
            generateRequests.setEnabled(false);
            statusLabel.setText("Processing bookings...");
            
            new Thread(() -> {
                try {
                    manager.processBookings(useOptimistic);
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Bookings processed successfully!");
                        processButton.setEnabled(true);
                        generateRequests.setEnabled(true);
                        refreshSeats();
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error processing bookings: " + ex.getMessage());
                        processButton.setEnabled(true);
                        generateRequests.setEnabled(true);
                    });
                }
            }).start();
        });

        JButton toggleLock = new JButton("Toggle Lock: Optimistic");
        toggleLock.addActionListener(e -> {
            useOptimistic = !useOptimistic;
            toggleLock.setText("Toggle Lock: " + (useOptimistic ? "Optimistic" : "Pessimistic"));
            statusLabel.setText("Switched to " + (useOptimistic ? "Optimistic" : "Pessimistic") + " locking");
        });

        JButton clearButton = new JButton("Clear All Seats");
        clearButton.addActionListener(e -> {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    manager.getSeats()[r][c].cancel();
                }
            }
            statusLabel.setText("All seats cleared");
            refreshSeats();
        });

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(generateRequests);
        controlPanel.add(processButton);
        controlPanel.add(toggleLock);
        controlPanel.add(clearButton);

        frame.add(statusLabel, BorderLayout.NORTH);
        frame.add(seatPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Add window listener to properly shutdown the manager
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                manager.shutdown();
            }
        });

        refreshSeats();
    }

    private void refreshSeats() {
        SwingUtilities.invokeLater(() -> {
            Seat[][] seats = manager.getSeats();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    seatButtons[r][c].setText(seats[r][c].isBooked() ? "X" : "O");
                    seatButtons[r][c].setBackground(seats[r][c].isBooked() ? Color.RED : Color.GREEN);
                    seatButtons[r][c].setForeground(seats[r][c].isBooked() ? Color.WHITE : Color.BLACK);
                    seatButtons[r][c].setEnabled(!seats[r][c].isBooked());
                }
            }
        });
    }
}
