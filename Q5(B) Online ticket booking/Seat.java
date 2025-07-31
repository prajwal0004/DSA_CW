public class Seat {
    private boolean booked = false;

    public synchronized boolean book() {
        if (!booked) {
            booked = true;
            return true;
        }
        return false;
    }

    public synchronized boolean isBooked() {
        return booked;
    }

    public synchronized void cancel() {
        booked = false;
    }
}