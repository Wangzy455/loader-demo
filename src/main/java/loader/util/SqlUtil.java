package loader.util;

import java.util.ArrayList;
import java.util.List;
import loader.common.TaskMetadata;
import loader.common.TaskState;

import java.sql.*;
import java.util.Optional;

public class SqlUtil {
    private static final String DB_URL = "jdbc:sqlite:tasks.db";
    private static Connection conn;

    static {
        initialize();
    }

    private static void initialize() {
        try {
            conn = DriverManager.getConnection(DB_URL);

            String createTableSQL = "CREATE TABLE IF NOT EXISTS task_metadata (" +
                "task_id TEXT PRIMARY KEY, " +
                "task_name TEXT, " +
                "state TEXT, " +
                "create_time INTEGER, " +
                "start_time INTEGER, " +
                "end_time INTEGER, " +
                "source_config TEXT, " +
                "target_config TEXT, " +
                "load_options TEXT, " +
                "total_records INTEGER, " +
                "processed_records INTEGER, " +
                "success_records INTEGER, " +
                "failed_records INTEGER, " +
                "progress_percentage REAL, " +
                "last_processed_timestamp TEXT, " +
                "checkpoint_data TEXT" +
                ")";
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 存储任务信息（更新也用这个方法）
     * @param metadata
     */
    public static void save(TaskMetadata metadata) {
        String sql = "REPLACE INTO task_metadata (" +
            "task_id, task_name, state, create_time, start_time, end_time, " +
            "source_config, target_config, load_options, " +
            "total_records, processed_records, success_records, failed_records, progress_percentage, " +
            "last_processed_timestamp, checkpoint_data) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, metadata.getTaskId());
            pstmt.setString(2, metadata.getTaskName());
            pstmt.setString(3, metadata.getState().name());
            pstmt.setLong(4, metadata.getCreateTime());
            pstmt.setLong(5, metadata.getStartTime());
            pstmt.setLong(6, metadata.getEndTime());
            pstmt.setString(7, metadata.getSourceConfig());
            pstmt.setString(8, metadata.getTargetConfig());
            pstmt.setString(9, metadata.getLoadOptions());
            pstmt.setInt(10, metadata.getTotalRecords());
            pstmt.setInt(11, metadata.getProcessedRecords());
            pstmt.setInt(12, metadata.getSuccessRecords());
            pstmt.setInt(13, metadata.getFailedRecords());
            pstmt.setDouble(14, metadata.getProgressPercentage());
            pstmt.setString(15, metadata.getLastProcessedTimestamp());
            pstmt.setString(16, metadata.getCheckpointData());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据任务ID查询元数据信息
     * @param taskId
     * @return
     */
    public static Optional<TaskMetadata> load(String taskId) {
        String sql = "SELECT * FROM task_metadata WHERE task_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, taskId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                TaskMetadata meta = new TaskMetadata();
                meta.setTaskId(rs.getString("task_id"));
                meta.setTaskName(rs.getString("task_name"));
                meta.setState(TaskState.valueOf(rs.getString("state")));
                meta.setCreateTime(rs.getLong("create_time"));
                meta.setStartTime(rs.getLong("start_time"));
                meta.setEndTime(rs.getLong("end_time"));
                meta.setSourceConfig(rs.getString("source_config"));
                meta.setTargetConfig(rs.getString("target_config"));
                meta.setLoadOptions(rs.getString("load_options"));
                meta.setTotalRecords(rs.getInt("total_records"));
                meta.setProcessedRecords(rs.getInt("processed_records"));
                meta.setSuccessRecords(rs.getInt("success_records"));
                meta.setFailedRecords(rs.getInt("failed_records"));
                meta.setProgressPercentage(rs.getDouble("progress_percentage"));
                meta.setLastProcessedTimestamp(rs.getString("last_processed_timestamp"));
                meta.setCheckpointData(rs.getString("checkpoint_data"));

                return Optional.of(meta);
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * 查询所有任务的元数据信息
     * @return
     */
    public static List<TaskMetadata> listAll() {
        List<TaskMetadata> taskList = new ArrayList<>();
        String sql = "SELECT * FROM task_metadata ORDER BY create_time DESC";

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TaskMetadata meta = new TaskMetadata();
                meta.setTaskId(rs.getString("task_id"));
                meta.setTaskName(rs.getString("task_name"));
                meta.setState(TaskState.valueOf(rs.getString("state")));
                meta.setCreateTime(rs.getLong("create_time"));
                meta.setStartTime(rs.getLong("start_time"));
                meta.setEndTime(rs.getLong("end_time"));
                meta.setSourceConfig(rs.getString("source_config"));
                meta.setTargetConfig(rs.getString("target_config"));
                meta.setLoadOptions(rs.getString("load_options"));
                meta.setTotalRecords(rs.getInt("total_records"));
                meta.setProcessedRecords(rs.getInt("processed_records"));
                meta.setSuccessRecords(rs.getInt("success_records"));
                meta.setFailedRecords(rs.getInt("failed_records"));
                meta.setProgressPercentage(rs.getDouble("progress_percentage"));
                meta.setLastProcessedTimestamp(rs.getString("last_processed_timestamp"));
                meta.setCheckpointData(rs.getString("checkpoint_data"));

                taskList.add(meta);
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return taskList;
    }

}
