package com.project.ard.dataretrieval.dto;

/**
 * 定时任务参数
 */
public class ScheduledParams {
    
    /**
     * 执行时间
     */
    private String executionTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 时区
     */
    private String timezone;
    
    // 构造函数
    public ScheduledParams() {}
    
    // Getter和Setter方法
    public String getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}

