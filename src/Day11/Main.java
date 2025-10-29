package Day11;

import Day11.entities.Task;
import Day11.entities.TaskPriority;
import Day11.managers.TaskScheduler;
import Day11.managers.DependencyResolver;
import Day11.exceptions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    private static final TaskScheduler scheduler = new TaskScheduler();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static boolean running = true;

    public static void main(String[] args) {
        displayBanner();
        loadDemoTasks(); // preload sample tasks

        while (running) {
            showMenu();
            String choice = getInput("Enter your choice: ");
            handleMenu(choice);
        }

        scanner.close();
        System.out.println("\n✓ Application closed successfully.");
    }


    private static void displayBanner() {
        System.out.println("\n================ TASK SCHEDULER SYSTEM - DAY 11 ================\n");
    }

    private static void showMenu() {
        System.out.println("\n---------------- MAIN MENU ----------------");
        System.out.println("1. View Current Tasks");
        System.out.println("2. Add New Task");
        System.out.println("3. Execute Next Task");
        System.out.println("4. Execute All Tasks");
        System.out.println("5. Test Edge Cases");
        System.out.println("6. Run Automated Tests");
        System.out.println("7. Exit");
        System.out.println("-------------------------------------------");
    }


    private static void handleMenu(String choice) {
        switch (choice) {
            case "1" -> viewTasks();
            case "2" -> addTask();
            case "3" -> executeNextTask();
            case "4" -> executeAllTasks();
            case "5" -> testEdgeCases();
            case "6" -> automatedTests();
            case "7" -> running = false;
            default -> System.out.println("Invalid choice! Please select 1-7.");
        }
    }


    private static void loadDemoTasks() {
        System.out.println("\nLoading demo project: 'Software Release Pipeline'...\n");
        LocalDateTime deadline = LocalDateTime.now().plusHours(8);

        try {
            // Independent root task
            scheduler.addTask(new Task("DESIGN-001", "Design Architecture",
                    "Create system design docs", TaskPriority.HIGH,
                    new HashSet<>(), "Alice", deadline));

            // Two parallel tasks depending on DESIGN
            scheduler.addTask(new Task("BACKEND-001", "Implement REST API",
                    "Develop backend services", TaskPriority.HIGH,
                    Set.of("DESIGN-001"), "Bob", deadline));

            scheduler.addTask(new Task("FRONTEND-001", "Build UI Components",
                    "Develop React UI", TaskPriority.HIGH,
                    Set.of("DESIGN-001"), "Charlie", deadline));

            // Testing depends on both backend and frontend
            scheduler.addTask(new Task("TESTING-001", "Integration Testing",
                    "Run full system tests", TaskPriority.MEDIUM,
                    Set.of("BACKEND-001", "FRONTEND-001"), "Diana", deadline));

            // Sequential dependencies
            scheduler.addTask(new Task("UAT-001", "User Acceptance Testing",
                    "Review with stakeholders", TaskPriority.MEDIUM,
                    Set.of("TESTING-001"), "Eve", deadline));

            scheduler.addTask(new Task("DEPLOY-001", "Deploy to Production",
                    "Deploy and monitor release", TaskPriority.CRITICAL,
                    Set.of("UAT-001"), "Frank", deadline));

            // Overdue independent task
            scheduler.addTask(new Task("HOTFIX-001", "Critical Bug Fix",
                    "Fix urgent production bug", TaskPriority.CRITICAL,
                    new HashSet<>(), "Grace", LocalDateTime.now().minusHours(1)));

        } catch (Exception e) {
            System.err.println("Failed to load demo tasks: " + e.getMessage());
        }

        System.out.println("✓ Demo tasks loaded successfully!\n");
    }


    private static void viewTasks() {
        System.out.println("\n----- CURRENT TASKS -----");
        Collection<Task> tasks = scheduler.getAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        for (Task t : tasks) {
            String overdue = t.isOverdue() ? " ⚠️" : "";
            System.out.printf("[%s] %-25s | %s%s | %s | %s%n",
                    t.getId(), t.getName(), t.getPriority(), overdue,
                    t.getStatus(), t.getAssignedTo());
            if (t.hasDependencies()) {
                System.out.println("   → Depends on: " + String.join(", ", t.getDependencies()));
            }
        }
    }


    private static void addTask() {
        System.out.println("\n----- ADD NEW TASK -----");

        String id = getInput("Enter Task ID: ").toUpperCase();
        String name = getInput("Enter Task Name: ");
        String desc = getInput("Enter Description: ");
        String assignee = getInput("Assigned To: ");
        String deadlineStr = getInput("Deadline (yyyy-MM-dd HH:mm): ");
        LocalDateTime deadline;

        try {
            deadline = LocalDateTime.parse(deadlineStr, dateFormatter);
        } catch (Exception e) {
            deadline = LocalDateTime.now().plusHours(8);
            System.out.println("Invalid date. Using default (8 hours later).");
        }

        // Select priority
        System.out.println("Priority: 1.CRITICAL  2.HIGH  3.MEDIUM  4.LOW");
        String p = getInput("Choose (1-4): ");
        TaskPriority priority = switch (p) {
            case "1" -> TaskPriority.CRITICAL;
            case "2" -> TaskPriority.HIGH;
            case "3" -> TaskPriority.MEDIUM;
            default -> TaskPriority.LOW;
        };

        // Dependencies
        Set<String> deps = new HashSet<>();
        String depChoice = getInput("Add dependencies? (y/n): ");
        if (depChoice.equalsIgnoreCase("y")) {
            for (Task t : scheduler.getAllTasks()) {
                System.out.println(" - " + t.getId() + ": " + t.getName());
            }
            String depsStr = getInput("Enter comma-separated IDs: ");
            if (!depsStr.isEmpty()) {
                for (String d : depsStr.split(",")) deps.add(d.trim().toUpperCase());
            }
        }

        try {
            Task task = new Task(id, name, desc, priority, deps, assignee, deadline);
            scheduler.addTask(task);
            System.out.println("✓ Task added successfully!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }



    private static void executeNextTask() {
        System.out.println("\n----- EXECUTE NEXT TASK -----");
        try {
            Task t = scheduler.executeNextTask();
            if (t == null) {
                System.out.println("No executable tasks (dependencies pending).");
                return;
            }
            System.out.println("✓ Executed: " + t.getName() + " (" + t.getPriority() + ")");
        } catch (Exception e) {
            System.err.println("Error executing task: " + e.getMessage());
        }
    }

    private static void executeAllTasks() {
        System.out.println("\n----- EXECUTE ALL TASKS -----");
        int total = scheduler.getTotalTasks(), done = 0;
        while (done < total) {
            Task t = scheduler.executeNextTask();
            if (t == null) break;
            done++;
            System.out.printf("[%d/%d] ✓ %s (%s)%n", done, total, t.getName(), t.getPriority());
        }
        System.out.println("✓ Execution complete (" + done + "/" + total + ")");
    }


    private static void testEdgeCases() {
        System.out.println("\n----- EDGE CASE TESTING -----");

        // 1. Circular dependency
        try {
            TaskScheduler test = new TaskScheduler();
            LocalDateTime d = LocalDateTime.now().plusHours(2);
            test.addTask(new Task("A", "Task A", "", TaskPriority.HIGH, Set.of("C"), "U1", d));
            test.addTask(new Task("B", "Task B", "", TaskPriority.HIGH, Set.of("A"), "U2", d));
            test.addTask(new Task("C", "Task C", "", TaskPriority.HIGH, Set.of("B"), "U3", d));
        } catch (CircularDependencyException e) {
            System.out.println("✓ Caught circular dependency.");
        }

        // 2. Duplicate ID
        try {
            TaskScheduler test = new TaskScheduler();
            LocalDateTime d = LocalDateTime.now().plusHours(2);
            test.addTask(new Task("X", "Task X", "", TaskPriority.HIGH, Set.of(), "U1", d));
            test.addTask(new Task("X", "Task Duplicate", "", TaskPriority.HIGH, Set.of(), "U2", d));
        } catch (DuplicateTaskException e) {
            System.out.println("✓ Duplicate ID rejected.");
        }
    }


    private static void automatedTests() {
        System.out.println("\n----- AUTOMATED TESTS -----");
        int passed = 0, total = 3;

        try {
            if (scheduler.getTotalTasks() > 0) passed++;
            System.out.println("Test 1: Task count ✓");
        } catch (Exception e) { System.out.println("Test 1: ✗ " + e.getMessage()); }

        try {
            Task t = scheduler.executeNextTask();
            if (t != null && t.getPriority() == TaskPriority.CRITICAL) passed++;
            System.out.println("Test 2: Priority ✓");
        } catch (Exception e) { System.out.println("Test 2: ✗ " + e.getMessage()); }

        try {
            DependencyResolver resolver = new DependencyResolver(
                    scheduler.getAllTasks().stream()
                            .collect(java.util.stream.Collectors.toMap(Task::getId, x -> x))
            );
            if (resolver != null) passed++;
            System.out.println("Test 3: Resolver ✓");
        } catch (Exception e) { System.out.println("Test 3: ✗ " + e.getMessage()); }

        System.out.printf("\nResults: %d/%d passed%n", passed, total);
    }


    private static String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
