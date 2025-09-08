package com.project.ard.dataretrieval.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 遥感TIF文件实体类
 * 
 * @author ard
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RsTifFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 基础ID
     */
    private Long baseId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 卫星ID
     */
    private String satelliteId;

    /**
     * 传感器ID
     */
    private String sensorId;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 获取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acquisitionTime;

    /**
     * 输入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inputTime;

    /**
     * 云量百分比
     */
    private BigDecimal cloudPercent;

    /**
     * 轨道ID
     */
    private Integer orbitId;

    /**
     * 场景路径
     */
    private Integer scenePath;

    /**
     * 场景行
     */
    private Integer sceneRow;

    /**
     * 快视图URI
     */
    private String quickViewUri;

    /**
     * 是否有配对
     */
    private Integer hasPair;

    /**
     * 是否有实体
     */
    private Integer hasEntity;

    /**
     * 激光计数
     */
    private Integer laserCount;

    /**
     * 边界几何信息（PostGIS geometry类型）
     */
    private String boundary;

    /**
     * 购物车信息
     */
    private String inCart;

    /**
     * 目标输入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tarInputTime;

    // 手动添加getter/setter方法以确保IDE识别
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSatelliteId() {
        return satelliteId;
    }

    public void setSatelliteId(String satelliteId) {
        this.satelliteId = satelliteId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public LocalDateTime getAcquisitionTime() {
        return acquisitionTime;
    }

    public void setAcquisitionTime(LocalDateTime acquisitionTime) {
        this.acquisitionTime = acquisitionTime;
    }

    public BigDecimal getCloudPercent() {
        return cloudPercent;
    }

    public void setCloudPercent(BigDecimal cloudPercent) {
        this.cloudPercent = cloudPercent;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }
}
