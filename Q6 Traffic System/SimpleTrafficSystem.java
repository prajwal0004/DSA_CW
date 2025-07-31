import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.Timer;

public class SimpleTrafficSystem extends JFrame {
    private volatile String currentSignal = "RED";
    private volatile boolean running = true;
    private volatile boolean emergencyMode = false;
    
    private final Queue<Vehicle> regularQueue = new LinkedList<>();
    private final PriorityQueue<Vehicle> emergencyQueue = new PriorityQueue<>();
    private final ReentrantLock queueLock = new ReentrantLock();
    private final Random random = new Random();
    
    // UI Components
    private JPanel trafficLightPanel;
    private JTextArea logArea;
    private JTextArea regularQueueArea;
    private JTextArea emergencyQueueArea;
    private JLabel statusLabel;
    
    // Timers
    private Timer signalTimer;
    private Timer vehicleTimer;
    private Timer animationTimer;
    private Timer queueTimer;

    public SimpleTrafficSystem() {
        initializeGUI();
        startSystem();
    }

    private void initializeGUI() {
        setTitle("Traffic Signal Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create panels
        JPanel leftPanel = createControlPanel();
        JPanel centerPanel = createTrafficPanel();
        JPanel rightPanel = createQueuePanel();

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // Add window listener
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopSystem();
            }
        });
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Control Panel"));
        panel.setPreferredSize(new Dimension(200, 0));

        // Status label
        statusLabel = new JLabel("System Running");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10));

        // Control buttons
        JButton changeSignalBtn = new JButton("Change Signal");
        changeSignalBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeSignalBtn.addActionListener(e -> changeSignal());
        panel.add(changeSignalBtn);
        panel.add(Box.createVerticalStrut(5));

        JButton emergencyModeBtn = new JButton("Toggle Emergency Mode");
        emergencyModeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        emergencyModeBtn.addActionListener(e -> toggleEmergencyMode());
        panel.add(emergencyModeBtn);
        panel.add(Box.createVerticalStrut(10));

        // Emergency vehicle buttons
        JLabel emergencyLabel = new JLabel("Emergency Vehicles:");
        emergencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emergencyLabel);
        panel.add(Box.createVerticalStrut(5));

        JButton addAmbulanceBtn = new JButton("Add Ambulance");
        addAmbulanceBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addAmbulanceBtn.addActionListener(e -> addEmergencyVehicle("ambulance"));
        panel.add(addAmbulanceBtn);
        panel.add(Box.createVerticalStrut(3));

        JButton addFiretruckBtn = new JButton("Add Fire Truck");
        addFiretruckBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addFiretruckBtn.addActionListener(e -> addEmergencyVehicle("firetruck"));
        panel.add(addFiretruckBtn);
        panel.add(Box.createVerticalStrut(3));

        JButton addPoliceBtn = new JButton("Add Police Car");
        addPoliceBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addPoliceBtn.addActionListener(e -> addEmergencyVehicle("police"));
        panel.add(addPoliceBtn);
        panel.add(Box.createVerticalStrut(10));

        // Regular vehicle buttons
        JLabel regularLabel = new JLabel("Regular Vehicles:");
        regularLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(regularLabel);
        panel.add(Box.createVerticalStrut(5));

        JButton addCarBtn = new JButton("Add Car");
        addCarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addCarBtn.addActionListener(e -> addRegularVehicle("car"));
        panel.add(addCarBtn);
        panel.add(Box.createVerticalStrut(3));

        JButton addBusBtn = new JButton("Add Bus");
        addBusBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBusBtn.addActionListener(e -> addRegularVehicle("bus"));
        panel.add(addBusBtn);

        return panel;
    }

    private JPanel createTrafficPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Traffic Intersection"));

        // Traffic light panel
        trafficLightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTrafficLight(g);
            }
        };
        trafficLightPanel.setPreferredSize(new Dimension(250, 250));
        trafficLightPanel.setBackground(Color.DARK_GRAY);

        panel.add(trafficLightPanel, BorderLayout.CENTER);

        // Log area
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Log"));
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createQueuePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Vehicle Queues"));
        panel.setPreferredSize(new Dimension(250, 0));

        // Regular queue
        JLabel regularLabel = new JLabel("Regular Queue (FIFO):");
        regularLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(regularLabel);
        panel.add(Box.createVerticalStrut(5));

        regularQueueArea = new JTextArea(8, 20);
        regularQueueArea.setEditable(false);
        regularQueueArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane regularScroll = new JScrollPane(regularQueueArea);
        regularScroll.setBorder(BorderFactory.createTitledBorder("Regular Vehicles"));
        panel.add(regularScroll);
        panel.add(Box.createVerticalStrut(10));

        // Emergency queue
        JLabel emergencyLabel = new JLabel("Emergency Queue (Priority):");
        emergencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emergencyLabel);
        panel.add(Box.createVerticalStrut(5));

        emergencyQueueArea = new JTextArea(8, 20);
        emergencyQueueArea.setEditable(false);
        emergencyQueueArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane emergencyScroll = new JScrollPane(emergencyQueueArea);
        emergencyScroll.setBorder(BorderFactory.createTitledBorder("Emergency Vehicles"));
        panel.add(emergencyScroll);

        return panel;
    }

    private void drawTrafficLight(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int lightRadius = 30;

        // Draw traffic light housing
        g2d.setColor(Color.BLACK);
        g2d.fillRect(centerX - 40, centerY - 80, 80, 160);

        // Draw red light
        Color redColor = "RED".equals(currentSignal) ? Color.RED : Color.GRAY;
        g2d.setColor(redColor);
        g2d.fillOval(centerX - lightRadius/2, centerY - 60, lightRadius, lightRadius);

        // Draw yellow light
        Color yellowColor = "YELLOW".equals(currentSignal) ? Color.YELLOW : Color.GRAY;
        g2d.setColor(yellowColor);
        g2d.fillOval(centerX - lightRadius/2, centerY - 10, lightRadius, lightRadius);

        // Draw green light
        Color greenColor = "GREEN".equals(currentSignal) ? Color.GREEN : Color.GRAY;
        g2d.setColor(greenColor);
        g2d.fillOval(centerX - lightRadius/2, centerY + 40, lightRadius, lightRadius);

        // Draw intersection lines
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(centerX - 100, centerY, centerX + 100, centerY);
        g2d.drawLine(centerX, centerY - 100, centerX, centerY + 100);

        // Draw emergency mode indicator
        if (emergencyMode) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("EMERGENCY MODE", centerX - 60, centerY + 120);
        }

        // Draw current signal text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(currentSignal, centerX - 25, centerY + 150);
    }

    private void startSystem() {
        // Signal timer
        signalTimer = new Timer(5000, e -> {
            switch (currentSignal) {
                case "RED":
                    currentSignal = "GREEN";
                    break;
                case "GREEN":
                    currentSignal = "YELLOW";
                    break;
                case "YELLOW":
                    currentSignal = "RED";
                    break;
            }
            updateStatus("Traffic Signal: " + currentSignal);
        });

        // Vehicle processing timer
        vehicleTimer = new Timer(1000, e -> {
            if ("GREEN".equals(currentSignal)) {
                processVehicles();
            }
        });

        // Random vehicle addition timer
        Timer addVehicleTimer = new Timer(3000 + random.nextInt(4000), e -> {
            addRandomVehicle();
        });

        // Animation timer
        animationTimer = new Timer(100, e -> trafficLightPanel.repaint());

        // Queue update timer
        queueTimer = new Timer(500, e -> updateQueueDisplays());

        // Start all timers
        signalTimer.start();
        vehicleTimer.start();
        addVehicleTimer.start();
        animationTimer.start();
        queueTimer.start();
    }

    private void stopSystem() {
        running = false;
        if (signalTimer != null) signalTimer.stop();
        if (vehicleTimer != null) vehicleTimer.stop();
        if (animationTimer != null) animationTimer.stop();
        if (queueTimer != null) queueTimer.stop();
    }

    private void changeSignal() {
        switch (currentSignal) {
            case "RED":
                currentSignal = "GREEN";
                break;
            case "GREEN":
                currentSignal = "YELLOW";
                break;
            case "YELLOW":
                currentSignal = "RED";
                break;
        }
        updateStatus("Manual Signal Change: " + currentSignal);
    }

    private void toggleEmergencyMode() {
        emergencyMode = !emergencyMode;
        updateStatus("Emergency Mode: " + (emergencyMode ? "ON" : "OFF"));
    }

    private void addEmergencyVehicle(String type) {
        queueLock.lock();
        try {
            Vehicle vehicle = new Vehicle(type);
            emergencyQueue.add(vehicle);
            updateStatus("Emergency vehicle added: " + vehicle);
        } finally {
            queueLock.unlock();
        }
    }

    private void addRegularVehicle(String type) {
        queueLock.lock();
        try {
            Vehicle vehicle = new Vehicle(type);
            regularQueue.add(vehicle);
            updateStatus("Regular vehicle added: " + vehicle);
        } finally {
            queueLock.unlock();
        }
    }

    private void addRandomVehicle() {
        queueLock.lock();
        try {
            String[] vehicleTypes = {"car", "bus", "truck"};
            String randomType = vehicleTypes[random.nextInt(vehicleTypes.length)];
            Vehicle vehicle = new Vehicle(randomType);
            
            if (vehicle.getPriority() < 5) {
                emergencyQueue.add(vehicle);
                updateStatus("Emergency vehicle auto-added: " + vehicle);
            } else {
                regularQueue.add(vehicle);
                updateStatus("Regular vehicle auto-added: " + vehicle);
            }
        } finally {
            queueLock.unlock();
        }
    }

    private void processVehicles() {
        queueLock.lock();
        try {
            Vehicle vehicle = getNextVehicle();
            if (vehicle != null) {
                updateStatus("Vehicle passing: " + vehicle);
            }
        } finally {
            queueLock.unlock();
        }
    }

    private Vehicle getNextVehicle() {
        if (!emergencyQueue.isEmpty()) {
            return emergencyQueue.poll();
        }
        
        if (emergencyMode) {
            return null;
        }
        
        return regularQueue.poll();
    }

    private void updateQueueDisplays() {
        SwingUtilities.invokeLater(() -> {
            // Update regular queue
            queueLock.lock();
            try {
                StringBuilder regularText = new StringBuilder();
                for (Vehicle vehicle : regularQueue) {
                    regularText.append(vehicle.toString()).append("\n");
                }
                regularQueueArea.setText(regularText.toString());

                // Update emergency queue
                StringBuilder emergencyText = new StringBuilder();
                for (Vehicle vehicle : emergencyQueue) {
                    emergencyText.append(vehicle.toString()).append(" (Priority: ").append(vehicle.getPriority()).append(")\n");
                }
                emergencyQueueArea.setText(emergencyText.toString());
            } finally {
                queueLock.unlock();
            }
        });
    }

    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + java.time.LocalTime.now().toString().substring(0, 8) + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // Vehicle class
    private static class Vehicle implements Comparable<Vehicle> {
        private String type;
        private int priority;
        private String id;
        private static int vehicleCounter = 0;

        public Vehicle(String type) {
            this.type = type;
            this.id = "V" + (++vehicleCounter);
            this.priority = switch (type.toLowerCase()) {
                case "ambulance" -> 1;
                case "firetruck" -> 2;
                case "police" -> 3;
                case "bus" -> 4;
                default -> 5;
            };
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public int compareTo(Vehicle other) {
            return Integer.compare(this.priority, other.priority);
        }

        @Override
        public String toString() {
            return id + " (" + type + ")";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleTrafficSystem().setVisible(true);
        });
    }
} 