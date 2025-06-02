package loader.task;

import java.util.HashMap;
import java.util.Map;
import loader.common.TaskState;
import loader.manger.TaskManager;
import loader.store.HStoreClient;
import lombok.Getter;

public class DataImportTask implements Runnable {
    @Getter
    private final String taskId;
    private final String data;
    private final HStoreClient hStoreClient = new HStoreClient();

    public DataImportTask(String taskId, String data) {
        this.taskId = taskId;
        this.data = data;
    }

    public int getTotalRecords() {
        return data.split("\n").length;
    }

    private volatile boolean cancelled = false;

    @Override
    public void run() {
        System.out.println("Task " + taskId + " started processing...");
        try {
            String[] rows = data.split("\n");
            int processed = 0;
            for (String row : rows) {
                if (cancelled) {
                    throw new RuntimeException("Task was cancelled");
                }
                Map<String, Object> vertex = parse(row);
                hStoreClient.writeVertex(vertex);
                processed++;
            }
            System.out.println("Task " + taskId + " completed.");
        } catch (Exception e) {
            System.err.println("Task " + taskId + " failed: " + e.getMessage());
            TaskManager.updateTaskState(taskId, TaskState.FAILED);
        }
    }

    public void cancel() {
        this.cancelled = true;
    }

    private Map<String, Object> parse(String row) {
        Map<String, Object> vertex = new HashMap<>();
        vertex.put("label", "person");
        vertex.put("name", row.trim());
        return vertex;
    }
}

