package com.project.ard.dataretrieval.dto;

/**
 * 切片信息
 */
public class SliceInfo {
    
    /**
     * 切片ID
     */
    private String id;
    
    /**
     * 切片业务ID
     */
    private String sliceId;
    
    /**
     * 所属立方体ID
     */
    private String cubeId;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件格式
     */
    private String fileFormat;
    
    /**
     * 成像时间
     */
    private String imagingTime;
    
    /**
     * 季度
     */
    private String quarter;
    
    /**
     * 切片描述
     */
    private String sliceDesc;
    
    /**
     * 切片路径
     */
    private String slicePath;
    
    /**
     * 分辨率
     */
    private String resolution;
    
    /**
     * 位置信息
     */
    private String location;
    
    // 构造函数
    public SliceInfo() {}
    
    public SliceInfo(String id, String sliceId, String cubeId, String fileName, String fileFormat) {
        this.id = id;
        this.sliceId = sliceId;
        this.cubeId = cubeId;
        this.fileName = fileName;
        this.fileFormat = fileFormat;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getSliceId() {
        return sliceId;
    }
    
    public void setSliceId(String sliceId) {
        this.sliceId = sliceId;
    }
    
    public String getCubeId() {
        return cubeId;
    }
    
    public void setCubeId(String cubeId) {
        this.cubeId = cubeId;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileFormat() {
        return fileFormat;
    }
    
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }
    
    public String getImagingTime() {
        return imagingTime;
    }
    
    public void setImagingTime(String imagingTime) {
        this.imagingTime = imagingTime;
    }
    
    public String getQuarter() {
        return quarter;
    }
    
    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }
    
    public String getSliceDesc() {
        return sliceDesc;
    }
    
    public void setSliceDesc(String sliceDesc) {
        this.sliceDesc = sliceDesc;
    }
    
    public String getSlicePath() {
        return slicePath;
    }
    
    public void setSlicePath(String slicePath) {
        this.slicePath = slicePath;
    }
    
    public String getResolution() {
        return resolution;
    }
    
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
}


