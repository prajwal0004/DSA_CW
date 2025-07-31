import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

public class BookingSystemGUI {
    private final int rows = 5;
    private final int cols = 5;
    private final BookingManager manager = new BookingManager(rows, cols);
    private final JButton[][] seatButtons = new JButton[rows][cols];
    private boolean useOptimistic = true;

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Online Ticket Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Seat grid panel
        JPanel seatPanel = new JPanel(new GridLayout(rows, cols));
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton("O");
                final int row = r;
                final int col = c;
                btn.addActionListener(e -> {
                    manager.addRequest(new BookingRequest("User", row, col));
                    refreshSeats();
                });
                seatButtons[r][c] = btn;
                seatPanel.add(btn);
            }
        }

        // Control buttons
        JButton generateRequests = new JButton("Add Random Requests");
        generateRequests.addActionListener((ActionEvent e) -> {
            Random rand = new Random();
            for (int i = 0; i < 5; i++) {
                int r = rand.nextInt(rows);
                int c = rand.nextInt(cols);
                manager.addRequest(new BookingRequest("User" + i, r, c));
            }
            JOptionPane.showMessageDialog(frame, "Random requests added!");
        });

        JButton processButton = new JButton("Process Bookings");
        processButton.addActionListener(e -> {
            new Thread(() -> {
                manager.processBookings(useOptimistic);
                refreshSeats();
            }).start();
        });

        JButton toggleLock = new JButton("Toggle Lock: Optimistic");
        toggleLock.addActionListener(e -> {
            useOptimistic = !useOptimistic;
            toggleLock.setText("Toggle Lock: " + (useOptimistic ? "Optimistic" : "Pessimistic"));
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(generateRequests);
        controlPanel.add(processButton);
        controlPanel.add(toggleLock);

        frame.add(seatPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setSize(400, 400);
        frame.setVisible(true);

        refreshSeats();
    }

    private void refreshSeats() {
        SwingUtilities.invokeLater(() -> {
            Seat[][] seats = manager.getSeats();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    seatButtons[r][c].setText(seats[r][c].isBooked() ? "X" : "O");
                    seatButtons[r][c].setBackground(seats[r][c].isBooked() ? Color.RED : Color.GREEN);
                }
            }
        });
    }
}
