package com.project.ard.dataretrieval.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.project.common.annotation.Excel;
import com.project.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

/**
 * 立方体任务步骤信息
 * 
 * @author project
 */
@TableName("cube_task_step")
public class CubeTaskStep extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long stepId;
    
    @Excel(name = "任务ID")
    private String taskId;
    
    @Excel(name = "步骤名称")
    private String stepName;
    
    @Excel(name = "步骤顺序")
    private Integer stepOrder;
    
    @Excel(name = "步骤状态")
    private String stepStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime endTime;
    
    @Excel(name = "步骤说明")
    private String stepDesc;
    
    @Excel(name = "错误详情")
    private String errorDetails;
    
    @Excel(name = "立方体数量")
    private Integer cubeCount;
    
    @Excel(name = "执行中心")
    private String processingCenter;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime created;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime updated;
    
    // 构造函数
    public CubeTaskStep() {}
    
    public CubeTaskStep(String taskId, String stepName, Integer stepOrder, String stepStatus) {
        this.taskId = taskId;
        this.stepName = stepName;
        this.stepOrder = stepOrder;
        this.stepStatus = stepStatus;
    }
    
    // Getters and Setters
    public Long getStepId() {
        return stepId;
    }
    
    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getStepName() {
        return stepName;
    }
    
    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
    
    public Integer getStepOrder() {
        return stepOrder;
    }
    
    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }
    
    public String getStepStatus() {
        return stepStatus;
    }
    
    public void setStepStatus(String stepStatus) {
        this.stepStatus = stepStatus;
    }
    
    public OffsetDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }
    
    public OffsetDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getStepDesc() {
        return stepDesc;
    }
    
    public void setStepDesc(String stepDesc) {
        this.stepDesc = stepDesc;
    }
    
    public String getErrorDetails() {
        return errorDetails;
    }
    
    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }
    
    public Integer getCubeCount() {
        return cubeCount;
    }
    
    public void setCubeCount(Integer cubeCount) {
        this.cubeCount = cubeCount;
    }
    
    public String getProcessingCenter() {
        return processingCenter;
    }
    
    public void setProcessingCenter(String processingCenter) {
        this.processingCenter = processingCenter;
    }
    
    public OffsetDateTime getCreated() {
        return created;
    }
    
    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }
    
    public OffsetDateTime getUpdated() {
        return updated;
    }
    
    public void setUpdated(OffsetDateTime updated) {
        this.updated = updated;
    }
}
