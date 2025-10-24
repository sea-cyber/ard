package com.project.ard.dataretrieval.dto;

import java.util.List;
import java.util.Map;

/**
 * 简化的任务创建请求DTO
 */
public class SimpleTaskCreateRequest {
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 时间开始
     */
    private String timeStart;
    
    /**
     * 时间结束
     */
    private String timeEnd;
    
    /**
     * 时间间隔
     */
    private String timeInterval;
    
    /**
     * 分析类型
     */
    private String analysisType;
    
    /**
     * 处理中心
     */
    private String processingCenter;
    
    /**
     * 任务级别
     */
    private String taskLevel;
    
    /**
     * 工作流
     */
    private String workflow;
    
    /**
     * 任务类型
     */
    private String taskType;
    
    /**
     * 是否定时任务
     */
    private Boolean isScheduled;
    
    /**
     * 定时任务参数（使用Map存储）
     */
    private Map<String, Object> scheduledParams;
    
    /**
     * 选中的数据（使用Map存储）
     */
    private List<Map<String, Object>> selectedData;
    
    /**
     * 选中的立方体信息（使用Map存储）
     */
    private List<Map<String, Object>> selectedCubes;
    
    /**
     * 选中的切片信息（使用Map存储）
     */
    private List<Map<String, Object>> selectedSlices;
    
    /**
     * 创建用户ID
     */
    private Long createUserId;
    
    // 构造函数
    public SimpleTaskCreateRequest() {}
    
    // Getter和Setter方法
    public String getTaskName() {
        return taskName;
    }
    
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getTimeStart() {
        return timeStart;
    }
    
    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }
    
    public String getTimeEnd() {
        return timeEnd;
    }
    
    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }
    
    public String getTimeInterval() {
        return timeInterval;
    }
    
    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }
    
    public String getAnalysisType() {
        return analysisType;
    }
    
    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }
    
    public String getProcessingCenter() {
        return processingCenter;
    }
    
    public void setProcessingCenter(String processingCenter) {
        this.processingCenter = processingCenter;
    }
    
    public String getTaskLevel() {
        return taskLevel;
    }
    
    public void setTaskLevel(String taskLevel) {
        this.taskLevel = taskLevel;
    }
    
    public String getWorkflow() {
        return workflow;
    }
    
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }
    
    public String getTaskType() {
        return taskType;
    }
    
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    
    public Boolean getIsScheduled() {
        return isScheduled;
    }
    
    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }
    
    public Map<String, Object> getScheduledParams() {
        return scheduledParams;
    }
    
    public void setScheduledParams(Map<String, Object> scheduledParams) {
        this.scheduledParams = scheduledParams;
    }
    
    public List<Map<String, Object>> getSelectedData() {
        return selectedData;
    }
    
    public void setSelectedData(List<Map<String, Object>> selectedData) {
        this.selectedData = selectedData;
    }
    
    public List<Map<String, Object>> getSelectedCubes() {
        return selectedCubes;
    }
    
    public void setSelectedCubes(List<Map<String, Object>> selectedCubes) {
        this.selectedCubes = selectedCubes;
    }
    
    public List<Map<String, Object>> getSelectedSlices() {
        return selectedSlices;
    }
    
    public void setSelectedSlices(List<Map<String, Object>> selectedSlices) {
        this.selectedSlices = selectedSlices;
    }
    
    public Long getCreateUserId() {
        return createUserId;
    }
    
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
}
