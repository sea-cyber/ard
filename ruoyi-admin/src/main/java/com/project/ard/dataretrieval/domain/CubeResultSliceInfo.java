package com.project.ard.dataretrieval.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@TableName("cube_result_slice_info")
public class CubeResultSliceInfo {

    @TableId(value = "result_slice_id", type = IdType.AUTO)
    private Long resultSliceId;

    @TableField("cube_id")
    private String cubeId;

    @TableField("user_id")
    private Long userId;

    @TableField("raw_slice_id")
    private Integer rawSliceId;

    @TableField("analysis_type")
    private String analysisType;

    @TableField("result_slice_path")
    private String resultSlicePath;

    @TableField("file_name")
    private String fileName;

    @TableField("file_format")
    private String fileFormat;

    @TableField("browse_image_path")
    @JsonProperty("browse_image_path")
    private String browseImagePath;

    @TableField("browse_file_name")
    private String browseFileName;

    @TableField("browse_format")
    private String browseFormat;

    @TableField("analysis_time")
    private OffsetDateTime analysisTime;

    @TableField("location")
    private String location;

    @TableField("result_desc")
    private String resultDesc;

    @TableField("resolution")
    private String resolution;

    @TableField("created")
    private OffsetDateTime created;

    @TableField("updated")
    private OffsetDateTime updated;

    @TableField("created_by")
    private String createdBy;

    @TableField("task_id")
    private String taskId;

    public Long getResultSliceId() { return resultSliceId; }
    public void setResultSliceId(Long resultSliceId) { this.resultSliceId = resultSliceId; }
    public String getCubeId() { return cubeId; }
    public void setCubeId(String cubeId) { this.cubeId = cubeId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getRawSliceId() { return rawSliceId; }
    public void setRawSliceId(Integer rawSliceId) { this.rawSliceId = rawSliceId; }
    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }
    public String getResultSlicePath() { return resultSlicePath; }
    public void setResultSlicePath(String resultSlicePath) { this.resultSlicePath = resultSlicePath; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
    public String getBrowseImagePath() { return browseImagePath; }
    public void setBrowseImagePath(String browseImagePath) { this.browseImagePath = browseImagePath; }
    public String getBrowseFileName() { return browseFileName; }
    public void setBrowseFileName(String browseFileName) { this.browseFileName = browseFileName; }
    public String getBrowseFormat() { return browseFormat; }
    public void setBrowseFormat(String browseFormat) { this.browseFormat = browseFormat; }
    public OffsetDateTime getAnalysisTime() { return analysisTime; }
    public void setAnalysisTime(OffsetDateTime analysisTime) { this.analysisTime = analysisTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getResultDesc() { return resultDesc; }
    public void setResultDesc(String resultDesc) { this.resultDesc = resultDesc; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public OffsetDateTime getCreated() { return created; }
    public void setCreated(OffsetDateTime created) { this.created = created; }
    public OffsetDateTime getUpdated() { return updated; }
    public void setUpdated(OffsetDateTime updated) { this.updated = updated; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
}



