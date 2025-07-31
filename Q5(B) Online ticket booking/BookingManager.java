import java.util.concurrent.*;
import java.util.*;

public class BookingManager {
    private final Seat[][] seats;
    private final Queue<BookingRequest> requestQueue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public BookingManager(int rows, int cols) {
        seats = new Seat[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                seats[r][c] = new Seat();
    }

    public void addRequest(BookingRequest req) {
        requestQueue.add(req);
    }

    public void processBookings(boolean optimistic) {
        while (!requestQueue.isEmpty()) {
            BookingRequest req = requestQueue.poll();
            executor.execute(() -> {
                Seat seat = seats[req.seatRow][req.seatCol];

                if (optimistic) {
                    if (!seat.isBooked()) {
                        try { Thread.sleep(50); } catch (InterruptedException e) {}
                        if (seat.book()) {
                            System.out.println(req.user + " successfully booked seat " + req.seatRow + "," + req.seatCol);
                        } else {
                            System.out.println(req.user + " failed (conflict)");
                        }
                    } else {
                        System.out.println(req.user + ": Seat already booked.");
                    }
                } else {
                    synchronized (seat) {
                        if (seat.book()) {
                            System.out.println(req.user + " booked seat " + req.seatRow + "," + req.seatCol);
                        } else {
                            System.out.println(req.user + ": Seat already booked.");
                        }
                    }
                }
            });
        }
    }

    public Seat[][] getSeats() {
        return seats;
    }

    public void shutdown() {
        executor.shutdown();
    }
}