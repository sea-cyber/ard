package com.project.ard.dataretrieval.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalTime;

/**
 * 立方体数据检索响应VO
 * 
 * @author project
 */
@Data
public class CubeRetrievalResponse {
    
    /**
     * 数据ID
     */
    @JsonProperty("id")
    private Long id;
    
    /**
     * 立方体名称
     */
    @JsonProperty("cubeName")
    private String cubeName;
    
    /**
     * 创建用户
     */
    @JsonProperty("createUser")
    private String createUser;
    
    /**
     * 边界信息 (GeoJSON格式)
     */
    @JsonProperty("boundary")
    private String boundary;
    
    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private LocalTime createTime;
    
    /**
     * 数据类型
     */
    @JsonProperty("dataType")
    private String dataType;
    
    /**
     * 数据描述
     */
    @JsonProperty("dataDescribe")
    private String dataDescribe;
    
    /**
     * 压缩算法
     */
    @JsonProperty("compressionAlgorithm")
    private String compressionAlgorithm;
    
    /**
     * Path编码
     */
    @JsonProperty("pathCode")
    private String pathCode;
    
    /**
     * Row编码
     */
    @JsonProperty("rowCode")
    private String rowCode;
    
    /**
     * 时间范围
     */
    @JsonProperty("timeRange")
    private String timeRange;
}
