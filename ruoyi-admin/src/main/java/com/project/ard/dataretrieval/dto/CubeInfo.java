package com.project.ard.dataretrieval.dto;

/**
 * 立方体信息
 */
public class CubeInfo {
    
    /**
     * 立方体ID
     */
    private String id;
    
    /**
     * 立方体业务ID
     */
    private String cubeId;
    
    /**
     * 立方体名称
     */
    private String cubeName;
    
    /**
     * 立方体描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 更新时间
     */
    private String updateTime;
    
    /**
     * 立方体状态
     */
    private String status;
    
    /**
     * 数据源
     */
    private String dataSource;
    
    /**
     * 时间范围开始
     */
    private String timeStart;
    
    /**
     * 时间范围结束
     */
    private String timeEnd;
    
    /**
     * 空间范围
     */
    private String spatialExtent;
    
    /**
     * 分辨率
     */
    private String resolution;
    
    /**
     * 波段信息
     */
    private String bands;
    
    /**
     * 立方体路径
     */
    private String cubePath;
    
    /**
     * 切片数量
     */
    private Integer sliceCount;
    
    /**
     * 立方体大小
     */
    private String cubeSize;
    
    // 构造函数
    public CubeInfo() {}
    
    public CubeInfo(String id, String cubeId, String cubeName) {
        this.id = id;
        this.cubeId = cubeId;
        this.cubeName = cubeName;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCubeId() {
        return cubeId;
    }
    
    public void setCubeId(String cubeId) {
        this.cubeId = cubeId;
    }
    
    public String getCubeName() {
        return cubeName;
    }
    
    public void setCubeName(String cubeName) {
        this.cubeName = cubeName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public String getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
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
    
    public String getSpatialExtent() {
        return spatialExtent;
    }
    
    public void setSpatialExtent(String spatialExtent) {
        this.spatialExtent = spatialExtent;
    }
    
    public String getResolution() {
        return resolution;
    }
    
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    
    public String getBands() {
        return bands;
    }
    
    public void setBands(String bands) {
        this.bands = bands;
    }
    
    public String getCubePath() {
        return cubePath;
    }
    
    public void setCubePath(String cubePath) {
        this.cubePath = cubePath;
    }
    
    public Integer getSliceCount() {
        return sliceCount;
    }
    
    public void setSliceCount(Integer sliceCount) {
        this.sliceCount = sliceCount;
    }
    
    public String getCubeSize() {
        return cubeSize;
    }
    
    public void setCubeSize(String cubeSize) {
        this.cubeSize = cubeSize;
    }
}