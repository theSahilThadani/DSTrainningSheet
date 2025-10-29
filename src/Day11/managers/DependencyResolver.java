package Day11.managers;

import Day11.entities.Task;
import Day11.exceptions.TaskNotFoundException;
import java.util.*;

public class DependencyResolver {
    private final Map<String, Task> taskMap;

    public DependencyResolver(Map<String, Task> taskMap) {
        this.taskMap = taskMap != null ? taskMap : new HashMap<>();
    }

    // Detects circular dependencies cycles in the graph
    public boolean cyclicDependency(String taskId) {
        if (!taskMap.containsKey(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        return dfs(taskId, visited, recursionStack);
    }

    // This is the recursive graph traversal function.Time Complexity O(V + E)
    private boolean dfs(String taskId, Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(taskId)) {
            System.err.println("Cycle detected at task: " + taskId);
            return true;
        }

        if (visited.contains(taskId)) {
            return false;
        }

        visited.add(taskId);
        recursionStack.add(taskId);

        Task task = taskMap.get(taskId);
        if (task != null) {
            for (String dependencyId : task.getDependencies()) {
                if (!taskMap.containsKey(dependencyId)) {
                    System.err.println("Missing dependency: " + dependencyId);
                    continue;
                }
                if (dfs(dependencyId, visited, recursionStack)) {
                    return true;
                }
            }
        }

        recursionStack.remove(taskId);
        return false;
    }


    // Finds tasks that dont depend on anyone time Complexity O(n)
    public Set<String> getIndependentTasks() {
        Set<String> independent = new HashSet<>();
        for (String taskId : taskMap.keySet()) {
            Task task = taskMap.get(taskId);
            if (!task.hasDependencies()) {
                independent.add(taskId);
            }
        }
        return independent;
    }


    // Computes the correct execution order of tasks so dependencies are executed in manner.
    public List<String> getTopologicalOrder() {
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, List<String>> adjList = new HashMap<>();

        // Initialize
        for (String taskId : taskMap.keySet()) {
            inDegree.put(taskId, 0);
            adjList.put(taskId, new ArrayList<>());
        }

        // Build adjacency list
        for (String taskId : taskMap.keySet()) {
            Task task = taskMap.get(taskId);
            for (String depId : task.getDependencies()) {
                if (taskMap.containsKey(depId)) {
                    adjList.get(depId).add(taskId);
                    inDegree.put(taskId, inDegree.get(taskId) + 1);
                }
            }
        }

        // Kahns algorithm
        Queue<String> queue = new LinkedList<>();
        for (String taskId : taskMap.keySet()) {
            if (inDegree.get(taskId) == 0) {
                queue.add(taskId);
            }
        }

        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String taskId = queue.poll();
            result.add(taskId);

            for (String neighbor : adjList.get(taskId)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return result;
    }

    //Find all tasks that depend on a given task.
    public Set<String> getDependentTasks(String taskId) {
        Set<String> dependents = new HashSet<>();
        for (String id : taskMap.keySet()) {
            Task task = taskMap.get(id);
            if (task.dependsOn(taskId)) {
                dependents.add(id);
            }
        }
        return dependents;
    }

    // Checks if a tasks dependencies are already completed
    public boolean dependenciesSatisfied(String taskId, Set<String> completedTasks) {
        Task task = taskMap.get(taskId);
        if (task == null) {
            throw new TaskNotFoundException(taskId);
        }

        for (String depId : task.getDependencies()) {
            if (!completedTasks.contains(depId)) {
                return false;
            }
        }
        return true;
    }

    //Validates that all dependencies actually exist in the system.
    public List<String> validateAllDependencies() {
        List<String> errors = new ArrayList<>();

        for (String taskId : taskMap.keySet()) {
            Task task = taskMap.get(taskId);
            for (String depId : task.getDependencies()) {
                if (!taskMap.containsKey(depId)) {
                    errors.add("Task '" + taskId + "' depends on non-existent task '" + depId + "'");
                }
            }
        }

        return errors;
    }
}