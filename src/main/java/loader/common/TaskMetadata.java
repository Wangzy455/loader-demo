package loader.common;

import lombok.Data;

@Data
public class TaskMetadata {
    private String taskId;
    private String taskName;
    private TaskState state;
    private long createTime;
    private long startTime;
    private long endTime;
    private String sourceConfig;
    private String targetConfig;
    private String loadOptions;

    // 进度信息
    private int totalRecords;
    private int processedRecords;
    private int successRecords;
    private int failedRecords;
    private double progressPercentage;

    // 增量检查点
    private String lastProcessedTimestamp;
    private String checkpointData;

    public String toJson() {
        return "{"
            + "\"taskId\":\"" + escape(taskId) + "\","
            + "\"taskName\":\"" + escape(taskName) + "\","
            + "\"state\":\"" + (state != null ? state.name() : "UNKNOWN") + "\","
            + "\"createTime\":" + createTime + ","
            + "\"startTime\":" + startTime + ","
            + "\"endTime\":" + endTime + ","
            + "\"sourceConfig\":\"" + escape(sourceConfig) + "\","
            + "\"targetConfig\":\"" + escape(targetConfig) + "\","
            + "\"loadOptions\":\"" + escape(loadOptions) + "\","
            + "\"totalRecords\":" + totalRecords + ","
            + "\"processedRecords\":" + processedRecords + ","
            + "\"successRecords\":" + successRecords + ","
            + "\"failedRecords\":" + failedRecords + ","
            + "\"progressPercentage\":" + progressPercentage + ","
            + "\"lastProcessedTimestamp\":\"" + escape(lastProcessedTimestamp) + "\","
            + "\"checkpointData\":\"" + escape(checkpointData) + "\""
            + "}";
    }

    private String escape(String str) {
        return str == null ? "" : str.replace("\"", "\\\"");
    }

}
