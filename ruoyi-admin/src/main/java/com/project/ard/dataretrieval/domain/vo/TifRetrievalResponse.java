package com.project.ard.dataretrieval.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * TIF数据检索响应结果
 * 
 * @author ard
 */
@ApiModel("TIF数据检索响应结果")
public class TifRetrievalResponse {

    @ApiModelProperty("数据ID")
    private String dataId;

    @ApiModelProperty("数据名称")
    private String dataName;

    @ApiModelProperty("卫星名称")
    private String satelliteName;

    @ApiModelProperty("数据类型")
    private String dataType;

    @ApiModelProperty("采集时间")
    private Date acquisitionTime;

    @ApiModelProperty("云量百分比")
    private Double cloudCoverage;

    @ApiModelProperty("数据大小(MB)")
    private Double dataSize;

    @ApiModelProperty("数据状态")
    private String status;

    @ApiModelProperty("下载链接")
    private String downloadUrl;

    @ApiModelProperty("预览图链接")
    private String previewUrl;

    @ApiModelProperty("空间分辨率")
    private String spatialResolution;

    @ApiModelProperty("光谱范围")
    private String spectralRange;

    @ApiModelProperty("覆盖区域")
    private String coverageArea;

    @ApiModelProperty("质量等级")
    private String qualityLevel;

    @ApiModelProperty("处理级别")
    private String processingLevel;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getSatelliteName() {
        return satelliteName;
    }

    public void setSatelliteName(String satelliteName) {
        this.satelliteName = satelliteName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Date getAcquisitionTime() {
        return acquisitionTime;
    }

    public void setAcquisitionTime(Date acquisitionTime) {
        this.acquisitionTime = acquisitionTime;
    }

    public Double getCloudCoverage() {
        return cloudCoverage;
    }

    public void setCloudCoverage(Double cloudCoverage) {
        this.cloudCoverage = cloudCoverage;
    }

    public Double getDataSize() {
        return dataSize;
    }

    public void setDataSize(Double dataSize) {
        this.dataSize = dataSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getSpatialResolution() {
        return spatialResolution;
    }

    public void setSpatialResolution(String spatialResolution) {
        this.spatialResolution = spatialResolution;
    }

    public String getSpectralRange() {
        return spectralRange;
    }

    public void setSpectralRange(String spectralRange) {
        this.spectralRange = spectralRange;
    }

    public String getCoverageArea() {
        return coverageArea;
    }

    public void setCoverageArea(String coverageArea) {
        this.coverageArea = coverageArea;
    }

    public String getQualityLevel() {
        return qualityLevel;
    }

    public void setQualityLevel(String qualityLevel) {
        this.qualityLevel = qualityLevel;
    }

    public String getProcessingLevel() {
        return processingLevel;
    }

    public void setProcessingLevel(String processingLevel) {
        this.processingLevel = processingLevel;
    }
}
