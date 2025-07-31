import java.util.*;
import java.util.concurrent.*;

public class TrafficController {
    private final Queue<Vehicle> vehicleQueue = new LinkedList<>();
    private final PriorityQueue<Vehicle> emergencyQueue = new PriorityQueue<>();

    private volatile boolean greenLight = true;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public void addVehicle(Vehicle v) {
        if (v.priority < 5) emergencyQueue.add(v);
        else vehicleQueue.add(v);
    }

    public void startSignalCycle() {
        executor.execute(() -> {
            while (true) {
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                greenLight = !greenLight;
                System.out.println("Traffic Light: " + (greenLight ? "GREEN" : "RED"));
            }
        });
    }

    public void startVehicleProcessor() {
        executor.execute(() -> {
            while (true) {
                if (greenLight) {
                    Vehicle v = !emergencyQueue.isEmpty() ? emergencyQueue.poll() : vehicleQueue.poll();
                    if (v != null) {
                        System.out.println(v.type + " passed the signal.");
                    }
                }
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        });
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}