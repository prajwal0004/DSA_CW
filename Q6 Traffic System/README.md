# Traffic Signal Management System

A complete, multithreaded traffic signal management system with GUI implementation.

## Features

### ✅ Core Functionality
- **Queue (FIFO Scheduling)**: Regular vehicles managed in first-in-first-out order
- **Priority Queue (Emergency Vehicles)**: Emergency vehicles get priority based on type
- **Multithreading**: Multiple timers handle different aspects of the system
- **Real-time GUI**: Animated traffic intersection with live updates

### ✅ GUI Components
- **Animated Traffic Intersection**: Real-time visual representation of traffic lights
- **Vehicle Queue Displays**: Shows both regular and emergency vehicle queues
- **System Log**: Real-time logging of all traffic events
- **Control Panel**: Interactive buttons for system control

### ✅ Control Buttons
- **Change Traffic Signal**: Manually toggle between red, yellow, and green lights
- **Toggle Emergency Mode**: Enable/disable emergency vehicle priority
- **Add Emergency Vehicles**: Manually add ambulances, fire trucks, and police cars
- **Add Regular Vehicles**: Manually add cars and buses

## Data Structures Used

1. **Queue (LinkedList)**: For regular vehicle management (FIFO)
2. **PriorityQueue**: For emergency vehicle handling (priority-based)
3. **ReentrantLock**: Thread-safe queue operations
4. **Swing Timer**: For GUI updates and system timing

## Vehicle Priority System

1. **Ambulance** (Priority 1): Highest priority
2. **Fire Truck** (Priority 2): Second highest priority
3. **Police Car** (Priority 3): Third highest priority
4. **Bus** (Priority 4): Medium priority
5. **Regular Cars** (Priority 5): Lowest priority

## System Architecture

### Timers (Multithreading)
- **Signal Timer**: Changes traffic signals every 5 seconds
- **Vehicle Timer**: Processes vehicles when green light is active
- **Animation Timer**: Updates traffic light display
- **Queue Timer**: Updates queue displays
- **Auto Vehicle Timer**: Adds random vehicles to the system

### Thread Safety
- All queue operations are protected with ReentrantLock
- GUI updates use SwingUtilities.invokeLater()
- Volatile variables for signal state management

## How to Run

1. Compile the system:
   ```bash
   javac SimpleTrafficSystem.java
   ```

2. Run the application:
   ```bash
   java SimpleTrafficSystem
   ```

## System Requirements

- Java 17 or higher (for switch expressions)
- Swing/AWT support
- Minimum 1000x700 screen resolution

## Key Benefits

- **Real-time Operation**: Vehicles move without blocking GUI updates
- **Independent Traffic Lights**: Traffic signals operate on their own timer
- **Emergency Priority**: Emergency vehicles get immediate priority when needed
- **Visual Feedback**: Clear visual representation of system state
- **Thread Safety**: All shared resources are properly synchronized
- **User Control**: Manual override capabilities for testing and emergency situations

## System Behavior

- **Automatic Signal Changes**: Traffic lights cycle automatically (RED → GREEN → YELLOW → RED)
- **Vehicle Processing**: Vehicles only pass when the light is GREEN
- **Emergency Mode**: When enabled, only emergency vehicles can pass
- **Priority Handling**: Emergency vehicles always get priority over regular vehicles
- **Auto Vehicle Generation**: Random vehicles are added to the system automatically
- **Real-time Logging**: All events are logged with timestamps

The system demonstrates advanced Java concepts including multithreading, GUI programming, data structures, and thread safety. 