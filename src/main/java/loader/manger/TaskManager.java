package loader.manger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import loader.common.TaskMetadata;
import loader.common.TaskState;
import loader.task.DataImportTask;
import loader.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskManager {

    private static final ExecutorService pool = Executors.newFixedThreadPool(4);
    private static final ConcurrentHashMap<String, TaskMetadata> taskMap = new ConcurrentHashMap<>();

    /**
     * 提交任务
     * @param task
     */
    public static void submit(DataImportTask task) {
        String taskId = task.getTaskId();
        TaskMetadata metadata = new TaskMetadata();
        metadata.setTaskId(taskId);
        metadata.setState(TaskState.PENDING);
        metadata.setCreateTime(System.currentTimeMillis());
        metadata.setTotalRecords(task.getTotalRecords());
        taskMap.put(taskId, metadata);

        log.info("初始任务信息:{}", metadata.toJson());
        SqlUtil.save(metadata);

        pool.submit(() -> {
            try {
                updateTaskState(taskId, TaskState.RUNNING);
                task.run();
                updateTaskState(taskId, TaskState.SUCCESS);
            } catch (Exception e) {
                System.err.println("Task " + taskId + " failed: " + e.getMessage());
                updateTaskState(taskId, TaskState.FAILED);
            } finally {
                updateTaskEndTime(taskId);
            }
        });
    }
    public static void cancelTask(String taskId) {
        TaskMetadata meta = taskMap.get(taskId);
        if (meta == null) return;

        meta.setState(TaskState.CANCELLED);
        meta.setEndTime(System.currentTimeMillis());
        SqlUtil.save(meta);
    }

    public static void updateTaskState(String taskId, TaskState state) {
        TaskMetadata meta = taskMap.get(taskId);
        if (meta != null) {
            meta.setState(state);
            meta.setStartTime(System.currentTimeMillis());
            taskMap.put(taskId, meta);
            SqlUtil.save(meta);
        }
    }


    public static void updateTaskEndTime(String taskId) {
        TaskMetadata meta = taskMap.get(taskId);
        if (meta != null) {
            meta.setEndTime(System.currentTimeMillis());
            SqlUtil.save(meta);
        }
    }
}


