package loader.common;

public enum TaskState {
    PENDING,
    RUNNING,
    SUCCESS,
    FAILED,
    RETRYING,
    CANCELLED,
    TIMEOUT,
    PAUSED,
    TERMINATED,
    UNKNOWN
}
