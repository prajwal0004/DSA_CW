import javax.swing.*;

public class MainBookingSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingSystemGUI().createAndShowGUI());
    }
}