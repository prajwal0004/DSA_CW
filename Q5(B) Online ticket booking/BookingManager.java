import java.util.concurrent.*;
import java.util.*;

public class BookingManager {
    private final Seat[][] seats;
    private final Queue<BookingRequest> requestQueue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final int rows, cols;

    public BookingManager(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        seats = new Seat[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                seats[r][c] = new Seat();
    }

    public void addRequest(BookingRequest req) {
        // Validate seat coordinates
        if (req.seatRow >= 0 && req.seatRow < rows && req.seatCol >= 0 && req.seatCol < cols) {
            requestQueue.add(req);
        } else {
            System.err.println("Invalid seat coordinates: " + req.seatRow + "," + req.seatCol);
        }
    }

    public void processBookings(boolean optimistic) {
        List<Future<?>> futures = new ArrayList<>();
        
        while (!requestQueue.isEmpty()) {
            BookingRequest req = requestQueue.poll();
            Future<?> future = executor.submit(() -> {
                try {
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
                } catch (Exception e) {
                    System.err.println("Error processing booking for " + req.user + ": " + e.getMessage());
                }
            });
            futures.add(future);
        }
        
        // Wait for all bookings to complete
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.err.println("Error waiting for booking completion: " + e.getMessage());
            }
        }
    }

    public Seat[][] getSeats() {
        return seats;
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}