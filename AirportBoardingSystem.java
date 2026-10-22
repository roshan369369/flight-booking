import java.util.*;

/**
 * Airport Passenger Boarding System
 * Demonstrates Queue (FIFO) and PriorityQueue data structures.
 * Run: javac AirportBoardingSystem.java && java AirportBoardingSystem
 */
public class AirportBoardingSystem {
    static class Passenger {
        String name;
        String ticketId;
        int priority;
        boolean isPriority;
        long sequence;

        Passenger(String name, String ticketId, int priority, boolean isPriority, long sequence) {
            this.name = name;
            this.ticketId = ticketId;
            this.priority = priority;
            this.isPriority = isPriority;
            this.sequence = sequence;
        }

        @Override
        public String toString() {
            if (isPriority) {
                return String.format("%-20s | %-14s | Priority: %d", name, ticketId, priority);
            }
            return String.format("%-20s | %-14s | Normal queue", name, ticketId);
        }
    }

    private final Queue<Passenger> normalQueue = new LinkedList<>();
    private final PriorityQueue<Passenger> priorityQueue = new PriorityQueue<>(
        Comparator.comparingInt((Passenger p) -> p.priority)
                  .thenComparingLong(p -> p.sequence)
    );
    private final List<Passenger> boardingHistory = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);
    private long sequence = 1;

    public static void main(String[] args) {
        new AirportBoardingSystem().run();
    }

    private void run() {
        System.out.println("\n==============================================");
        System.out.println("     AIRPORT PASSENGER BOARDING SYSTEM");
        System.out.println("==============================================");
        System.out.println("Queue + Priority Queue | Java Data Structures");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1: addNormalPassenger(); break;
                case 2: addPriorityPassenger(); break;
                case 3: boardNextPassenger(); break;
                case 4: displayWaitingPassengers(); break;
                case 5: displayBoardingHistory(); break;
                case 6: displaySummary(); break;
                case 0:
                    running = false;
                    System.out.println("\nSystem closed. Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select from the menu.");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n--------------- MAIN MENU ----------------");
        System.out.println("1. Add normal passenger");
        System.out.println("2. Add priority passenger");
        System.out.println("3. Board next passenger");
        System.out.println("4. Display waiting passengers");
        System.out.println("5. Display boarding history");
        System.out.println("6. Display summary");
        System.out.println("0. Exit");
        System.out.println("------------------------------------------");
    }

    private void addNormalPassenger() {
        String name = readNonEmpty("Enter passenger name: ");
        String id = readUniqueTicketId();
        normalQueue.offer(new Passenger(name, id, 0, false, sequence++));
        System.out.println("Passenger added to the normal queue.");
    }

    private void addPriorityPassenger() {
        String name = readNonEmpty("Enter passenger name: ");
        String id = readUniqueTicketId();
        int priority;
        do {
            priority = readInt("Enter priority (1 = highest, 4 = lowest): ");
            if (priority < 1 || priority > 4) {
                System.out.println("Priority must be between 1 and 4.");
            }
        } while (priority < 1 || priority > 4);

        priorityQueue.offer(new Passenger(name, id, priority, true, sequence++));
        System.out.println("Passenger added to the priority queue.");
    }

    private void boardNextPassenger() {
        Passenger passenger;
        if (!priorityQueue.isEmpty()) {
            passenger = priorityQueue.poll();
        } else {
            passenger = normalQueue.poll();
        }

        if (passenger == null) {
            System.out.println("No passengers are waiting to board.");
            return;
        }

        boardingHistory.add(passenger);
        System.out.println("\nBOARDING CONFIRMED");
        System.out.println("------------------------------------------");
        System.out.println("Name       : " + passenger.name);
        System.out.println("Ticket ID  : " + passenger.ticketId);
        System.out.println("Queue type : " + (passenger.isPriority ? "Priority" : "Normal"));
        if (passenger.isPriority) {
            System.out.println("Priority   : " + passenger.priority);
        }
        System.out.println("------------------------------------------");
    }

    private void displayWaitingPassengers() {
        System.out.println("\n========== NORMAL QUEUE (FIFO) ===========");
        if (normalQueue.isEmpty()) {
            System.out.println("No normal passengers waiting.");
        } else {
            int position = 1;
            for (Passenger passenger : normalQueue) {
                System.out.println(position++ + ". " + passenger);
            }
        }

        System.out.println("\n========== PRIORITY QUEUE ================");
        if (priorityQueue.isEmpty()) {
            System.out.println("No priority passengers waiting.");
        } else {
            List<Passenger> sorted = new ArrayList<>(priorityQueue);
            sorted.sort(Comparator.comparingInt((Passenger p) -> p.priority)
                                  .thenComparingLong(p -> p.sequence));
            int position = 1;
            for (Passenger passenger : sorted) {
                System.out.println(position++ + ". " + passenger);
            }
        }
        System.out.println("------------------------------------------");
        System.out.println("Waiting total: " + (normalQueue.size() + priorityQueue.size()));
    }

    private void displayBoardingHistory() {
        System.out.println("\n============= BOARDING HISTORY ===========");
        if (boardingHistory.isEmpty()) {
            System.out.println("No passengers have boarded yet.");
            return;
        }
        for (int i = 0; i < boardingHistory.size(); i++) {
            System.out.println((i + 1) + ". " + boardingHistory.get(i));
        }
    }

    private void displaySummary() {
        System.out.println("\n=============== SUMMARY ==================");
        System.out.println("Normal passengers waiting   : " + normalQueue.size());
        System.out.println("Priority passengers waiting : " + priorityQueue.size());
        System.out.println("Total passengers waiting    : " +
                           (normalQueue.size() + priorityQueue.size()));
        System.out.println("Passengers boarded          : " + boardingHistory.size());
        System.out.println("==========================================");
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    private String readUniqueTicketId() {
        while (true) {
            String id = readNonEmpty("Enter passenger/ticket ID: ");
            if (!ticketIdExists(id)) return id;
            System.out.println("That ticket ID is already in use. Enter a unique ID.");
        }
    }

    private boolean ticketIdExists(String id) {
        for (Passenger p : normalQueue) {
            if (p.ticketId.equalsIgnoreCase(id)) return true;
        }
        for (Passenger p : priorityQueue) {
            if (p.ticketId.equalsIgnoreCase(id)) return true;
        }
        for (Passenger p : boardingHistory) {
            if (p.ticketId.equalsIgnoreCase(id)) return true;
        }
        return false;
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
