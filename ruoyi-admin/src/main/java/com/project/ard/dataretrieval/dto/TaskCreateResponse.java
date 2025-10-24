package com.project.ard.dataretrieval.dto;

/**
 * 任务创建响应DTO
 */
public class TaskCreateResponse {
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 错误代码
     */
    private String errorCode;
    
    // 构造函数
    public TaskCreateResponse() {}
    
    public TaskCreateResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public TaskCreateResponse(Boolean success, String message, String taskId) {
        this.success = success;
        this.message = message;
        this.taskId = taskId;
    }
    
    // Getter和Setter方法
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}


