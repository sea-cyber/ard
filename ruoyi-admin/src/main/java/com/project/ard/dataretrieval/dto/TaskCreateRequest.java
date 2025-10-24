package com.project.ard.dataretrieval.dto;

import java.util.List;

/**
 * 任务创建请求DTO
 */
public class TaskCreateRequest {
    
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
     * 选中的数据
     */
    private List<SelectedData> selectedData;
    
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
     * 定时任务参数
     */
    private ScheduledParams scheduledParams;
    
    /**
     * 选中的立方体信息
     */
    private List<CubeInfo> selectedCubes;
    
    /**
     * 选中的切片信息
     */
    private List<SliceInfo> selectedSlices;
    
    // 构造函数
    public TaskCreateRequest() {}
    
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
    
    public List<SelectedData> getSelectedData() {
        return selectedData;
    }
    
    public void setSelectedData(List<SelectedData> selectedData) {
        this.selectedData = selectedData;
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
    
    public ScheduledParams getScheduledParams() {
        return scheduledParams;
    }
    
    public void setScheduledParams(ScheduledParams scheduledParams) {
        this.scheduledParams = scheduledParams;
    }
    
    public List<CubeInfo> getSelectedCubes() {
        return selectedCubes;
    }
    
    public void setSelectedCubes(List<CubeInfo> selectedCubes) {
        this.selectedCubes = selectedCubes;
    }
    
    public List<SliceInfo> getSelectedSlices() {
        return selectedSlices;
    }
    
    public void setSelectedSlices(List<SliceInfo> selectedSlices) {
        this.selectedSlices = selectedSlices;
    }
}

