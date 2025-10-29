package Day11.managers;

import Day11.entities.Task;
import Day11.entities.TaskStatus;
import Day11.exceptions.*;
import java.util.*;

public class TaskScheduler {
    private final PriorityQueue<Task> taskQueue;
    private final Map<String, Task> allTasks;
    private final Set<String> completedTasks;
    private final DependencyResolver resolver;
    private final List<String> executionLog;
    private final Map<String, Long> teamWorkload;

    // uses priorityQueue to prioritise the task in order
    public TaskScheduler() {
        // Priority Higher priority + Non overdue first and then by deadline
        this.taskQueue = new PriorityQueue<>((t1, t2) -> {
            // Critical tasks first
            int priorityCmp = t2.getPriority().getValue() - t1.getPriority().getValue();
            if (priorityCmp != 0) return priorityCmp;

            // Nonoverdue before overdue
            boolean t1Overdue = t1.isOverdue();
            boolean t2Overdue = t2.isOverdue();
            if (t1Overdue != t2Overdue) {
                return t1Overdue ? 1 : -1;
            }

            // Earlier deadline first
            if (t1.getDeadline() != null && t2.getDeadline() != null) {
                return t1.getDeadline().compareTo(t2.getDeadline());
            }

            return 0;
        });

        this.allTasks = new HashMap<>();
        this.completedTasks = new HashSet<>();
        this.resolver = new DependencyResolver(allTasks);
        this.executionLog = new ArrayList<>();
        this.teamWorkload = new HashMap<>();
    }


    // add a task and validate with dependency
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        // Check duplicate
        if (allTasks.containsKey(task.getId())) {
            throw new DuplicateTaskException(task.getId());
        }

        // Add to map first
        allTasks.put(task.getId(), task);

        // Check for cycles
        if (resolver.cyclicDependency(task.getId())) {
            //if cycle then remove it from map
            allTasks.remove(task.getId());
            throw new CircularDependencyException(task.getId());
        }

        // Validate dependencies exist
        List<String> errors = resolver.validateAllDependencies();
        if (!errors.isEmpty()) {
            allTasks.remove(task.getId());
            throw new TaskNotFoundException("Validation errors: " + String.join(", ", errors));
        }

        // Add to queue if ready
        if (resolver.dependenciesSatisfied(task.getId(), completedTasks)) {
            task.setReady();
            taskQueue.offer(task);
        } else {
            // if not all dependencies are not satisfied then add in queue
            task.setReady();
        }
        System.out.println("TASK_ADDED"+ task.getId()+ task.getAssignedTo());
    }

    // Execute the next available task with highest priority.
    public Task executeNextTask() {
        Task nextTask = getNextExecutableTask();
        if (nextTask == null) {
            return null;
        }

        try {
            nextTask.markStarted();
            System.out.println("TASK_STARTED"+ nextTask.getId() + nextTask.getAssignedTo());

            nextTask.markCompleted();
            completedTasks.add(nextTask.getId());
            updateTeamWorkload(nextTask);
            System.out.println("TASK_COMPLETED "+ nextTask.getId()+ nextTask.getAssignedTo());

            // Update dependent tasks
            updateReadyTasks(nextTask.getId());

            return nextTask;
        } catch (Exception e) {
            nextTask.markFailed(e.getMessage());
            System.out.println("TASK_FAILED"+ nextTask.getId() + nextTask.getAssignedTo());
            throw new TaskExecutionException(nextTask.getId(), e.getMessage());
        }
    }

    //gives next priority task
    private Task getNextExecutableTask() {
        while (!taskQueue.isEmpty()) {
            Task candidate = taskQueue.peek();

            // Check if dependencies are satisfied
            if (resolver.dependenciesSatisfied(candidate.getId(), completedTasks)) {
                return taskQueue.poll();
            } else {
                taskQueue.poll();
            }
        }

        return null;
    }


    // updates task for check next task that is pending and all its dependencies are completed.
    private void updateReadyTasks(String completedTaskId) {
        Set<String> dependents = resolver.getDependentTasks(completedTaskId);

        for (String depId : dependents) {
            Task depTask = allTasks.get(depId);
            if (depTask != null &&
                    depTask.getStatus() == TaskStatus.PENDING &&
                    resolver.dependenciesSatisfied(depId, completedTasks)) {

                depTask.setReady();
                taskQueue.offer(depTask);
                System.out.println("TASK_READY"+ depId + depTask.getAssignedTo());
            }
        }
    }

    // Update team member workload.
    private void updateTeamWorkload(Task task) {
        String assignee = task.getAssignedTo();
        teamWorkload.put(assignee, teamWorkload.getOrDefault(assignee, 0L) + task.getExecutionTimeMs());
    }



    public int getTotalTasks() { return allTasks.size(); }
    public int getCompletedTasks() { return completedTasks.size(); }
    public int getPendingTasks() { return taskQueue.size(); }
    public List<String> getExecutionLog() { return new ArrayList<>(executionLog); }
    public Map<String, Long> getTeamWorkload() { return new HashMap<>(teamWorkload); }


    public Task getTask(String taskId) {
        return allTasks.get(taskId);
    }

    public Collection<Task> getAllTasks() {
        return new ArrayList<>(allTasks.values());
    }

    public Set<String> getCompletedTaskIds() {
        return new HashSet<>(completedTasks);
    }

}