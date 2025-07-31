public class MainTrafficSystem {
    public static void main(String[] args) {
        TrafficController controller = new TrafficController();
        controller.startSignalCycle();
        controller.startVehicleProcessor();

        controller.addVehicle(new Vehicle("car"));
        controller.addVehicle(new Vehicle("ambulance"));
        controller.addVehicle(new Vehicle("firetruck"));
        controller.addVehicle(new Vehicle("car"));
    }
}