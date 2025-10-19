package com.project.ard.dataretrieval.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 立方体切片数据响应VO
 * 
 * @author project
 */
@Data
public class CubeSliceResponse {
    
    /**
     * 切片ID
     */
    @JsonProperty("sliceId")
    private Integer sliceId;
    
    /**
     * 立方体ID
     */
    @JsonProperty("cubeId")
    private String cubeId;
    
    /**
     * 季度
     */
    @JsonProperty("quarter")
    private String quarter;
    
    /**
     * 切片路径
     */
    @JsonProperty("slicePath")
    private String slicePath;
    
    /**
     * 文件名
     */
    @JsonProperty("fileName")
    private String fileName;
    
    /**
     * 文件格式
     */
    @JsonProperty("fileFormat")
    private String fileFormat;
    
    /**
     * 浏览图路径
     */
    @JsonProperty("browseImagePath")
    private String browseImagePath;
    
    /**
     * 浏览图文件名
     */
    @JsonProperty("browseFileName")
    private String browseFileName;
    
    /**
     * 浏览图格式
     */
    @JsonProperty("browseFormat")
    private String browseFormat;
    
    /**
     * 成像时间
     */
    @JsonProperty("imagingTime")
    private OffsetDateTime imagingTime;
    
    /**
     * 位置
     */
    @JsonProperty("location")
    private String location;
    
    /**
     * 切片描述
     */
    @JsonProperty("sliceDesc")
    private String sliceDesc;
    
    /**
     * 分辨率
     */
    @JsonProperty("resolution")
    private String resolution;
    
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

