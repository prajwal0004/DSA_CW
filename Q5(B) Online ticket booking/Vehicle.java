public class Vehicle implements Comparable<Vehicle> {
    public String type;
    public int priority;

    public Vehicle(String type) {
        this.type = type;
        this.priority = switch (type) {
            case "ambulance" -> 1;
            case "firetruck" -> 2;
            default -> 5;
        };
    }

    @Override
    public int compareTo(Vehicle v) {
        return Integer.compare(this.priority, v.priority);
    }
}