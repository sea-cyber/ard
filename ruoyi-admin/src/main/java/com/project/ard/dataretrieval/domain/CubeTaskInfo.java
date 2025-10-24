package com.project.ard.dataretrieval.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

/**
 * 立方体任务信息实体类
 * 对应 cube_task_info 表
 * 
 * @author project
 */
@TableName("cube_task_info")
public class CubeTaskInfo {

    /**
     * 任务ID
     */
    @TableId(value = "task_id", type = IdType.INPUT)
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String taskDescription;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime created;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime updated;

    /**
     * 分析开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime timeStart;

    /**
     * 分析结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime timeEnd;

    /**
     * 处理中心
     */
    private String processingCenter;

    /**
     * 输出分辨率
     */
    private String outputResolution;

    /**
     * 输出格式
     */
    private String outputFormat;

    /**
     * 工作流ID
     */
    private String workflowId;

    /**
     * 工作流名称
     */
    private String workflowName;

    /**
     * 工作流描述
     */
    private String workflowDescription;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 进度百分比
     */
    private Integer progress;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 结果文件数量
     */
    private Integer resultCount;

    /**
     * 结果目录路径
     */
    private String resultDirectory;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime completionTime;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 预计耗时（秒）
     */
    private Integer estimatedDuration;

    /**
     * 实际耗时（秒）
     */
    private Integer actualDuration;

    /**
     * 资源使用情况
     */
    private String resourceUsage;

    // Getter and Setter methods
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public OffsetDateTime getCreated() { return created; }
    public void setCreated(OffsetDateTime created) { this.created = created; }
    
    public OffsetDateTime getUpdated() { return updated; }
    public void setUpdated(OffsetDateTime updated) { this.updated = updated; }
    
    public OffsetDateTime getTimeStart() { return timeStart; }
    public void setTimeStart(OffsetDateTime timeStart) { this.timeStart = timeStart; }
    
    public OffsetDateTime getTimeEnd() { return timeEnd; }
    public void setTimeEnd(OffsetDateTime timeEnd) { this.timeEnd = timeEnd; }
    
    public String getProcessingCenter() { return processingCenter; }
    public void setProcessingCenter(String processingCenter) { this.processingCenter = processingCenter; }
    
    public String getOutputResolution() { return outputResolution; }
    public void setOutputResolution(String outputResolution) { this.outputResolution = outputResolution; }
    
    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
    
    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }
    
    public String getWorkflowName() { return workflowName; }
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }
    
    public String getWorkflowDescription() { return workflowDescription; }
    public void setWorkflowDescription(String workflowDescription) { this.workflowDescription = workflowDescription; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Integer getResultCount() { return resultCount; }
    public void setResultCount(Integer resultCount) { this.resultCount = resultCount; }
    
    public String getResultDirectory() { return resultDirectory; }
    public void setResultDirectory(String resultDirectory) { this.resultDirectory = resultDirectory; }
    
    public OffsetDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(OffsetDateTime completionTime) { this.completionTime = completionTime; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    
    public Integer getActualDuration() { return actualDuration; }
    public void setActualDuration(Integer actualDuration) { this.actualDuration = actualDuration; }
    
    public String getResourceUsage() { return resourceUsage; }
    public void setResourceUsage(String resourceUsage) { this.resourceUsage = resourceUsage; }
}

