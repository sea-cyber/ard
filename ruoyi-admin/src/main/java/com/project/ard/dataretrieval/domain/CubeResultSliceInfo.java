package com.project.ard.dataretrieval.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.OffsetDateTime;

@TableName("cube_result_slice_info")
public class CubeResultSliceInfo {

    @TableId(value = "result_slice_id", type = IdType.AUTO)
    private Long resultSliceId;

    private String cubeId;

    private Long userId;

    private Integer rawSliceId;

    private String analysisType;

    private String resultSlicePath;

    private String fileName;

    private String fileFormat;

    private String browseImagePath;

    private String browseFileName;

    private String browseFormat;

    private OffsetDateTime analysisTime;

    private String location;

    private String resultDesc;

    private String resolution;

    private OffsetDateTime created;

    private OffsetDateTime updated;

    private String createdBy;

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



