package com.project.ard.dataretrieval.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 立方体数据检索响应VO
 * 
 * @author project
 */
@Data
public class CubeRetrievalResponse {
    
    /**
     * 立方体ID
     */
    @JsonProperty("cubeId")
    private String cubeId;
    
    /**
     * 格网ID
     */
    @JsonProperty("gridId")
    private String gridId;
    
    /**
     * 数据密级
     */
    @JsonProperty("secretLevel")
    private String secretLevel;
    
    /**
     * 立方体描述
     */
    @JsonProperty("description")
    private String description;
    
    /**
     * 省份
     */
    @JsonProperty("province")
    private String province;
    
    /**
     * 城市
     */
    @JsonProperty("city")
    private String city;
    
    /**
     * 县区
     */
    @JsonProperty("county")
    private String county;
    
    /**
     * 城市区域
     */
    @JsonProperty("cityDistrict")
    private String cityDistrict;
    
    /**
     * EPSG编码
     */
    @JsonProperty("epsg")
    private Integer epsg;
    
    /**
     * 边界信息 (GeoJSON格式)
     */
    @JsonProperty("boundary")
    private String boundary;
    
    /**
     * 格网类型
     */
    @JsonProperty("gridType")
    private String gridType;
    
    /**
     * 创建机构
     */
    @JsonProperty("organization")
    private String organization;
    
    /**
     * 所属部门
     */
    @JsonProperty("department")
    private String department;
    
    /**
     * 操作人员
     */
    @JsonProperty("operator")
    private String operator;
    
    /**
     * 联系人邮箱
     */
    @JsonProperty("email")
    private String email;
    
    /**
     * 角色
     */
    @JsonProperty("role")
    private String role;
    
    /**
     * 总文件数量
     */
    @JsonProperty("totalFiles")
    private Integer totalFiles;
    
    /**
     * 原始数据文件数量
     */
    @JsonProperty("originalFiles")
    private Integer originalFiles;
    
    /**
     * 衍生数据文件数量
     */
    @JsonProperty("derivedFiles")
    private Integer derivedFiles;
    
    /**
     * 覆盖的季节
     */
    @JsonProperty("seasonsCovered")
    private String[] seasonsCovered;
    
    /**
     * 时间跨度
     */
    @JsonProperty("timeSpan")
    private String timeSpan;
    
    /**
     * 分辨率等级
     */
    @JsonProperty("resolutionLevel")
    private String resolutionLevel;
    
    /**
     * 创建时间
     */
    @JsonProperty("created")
    private OffsetDateTime created;
    
    /**
     * 更新时间
     */
    @JsonProperty("updated")
    private OffsetDateTime updated;
    
    /**
     * 创建者
     */
    @JsonProperty("createdBy")
    private String createdBy;
}
