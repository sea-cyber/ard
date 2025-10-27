package com.project.ard.dataretrieval.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * 场景实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("scene")
public class Scene implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonProperty("id")
    private Integer id;

    /**
     * 场景名称
     */
    @TableField("scene_name")
    @JsonProperty("sceneName")
    private String sceneName;

    /**
     * 创建用户
     */
    @TableField("create_user")
    @JsonProperty("createUser")
    private String createUser;

    /**
     * 边界几何信息（GeoJSON格式）
     */
    @TableField("boundary")
    @JsonProperty("boundary")
    private String boundary;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private OffsetDateTime createTime;

    /**
     * 数据类型
     */
    @TableField("data_type")
    @JsonProperty("dataType")
    private String dataType;

    /**
     * 数据描述
     */
    @TableField("data_describe")
    @JsonProperty("dataDescribe")
    private String dataDescribe;

    /**
     * 是否分析（0-未分析，1-已分析）
     */
    @TableField("is_analysis")
    @JsonProperty("isAnalysis")
    private Integer isAnalysis;

    /**
     * 分析类型
     */
    @TableField("analysis_type")
    @JsonProperty("analysisType")
    private String analysisType;

    /**
     * 分析结果ID（JSON格式）
     */
    @TableField("analysis_result_id")
    @JsonProperty("analysisResultId")
    private String analysisResultId;

    /**
     * Path编码
     */
    @TableField("path_code")
    @JsonProperty("pathCode")
    private String pathCode;

    /**
     * Row编码
     */
    @TableField("row_code")
    @JsonProperty("rowCode")
    private String rowCode;

    /**
     * 时间范围（tstzrange格式）
     */
    @TableField("time_range")
    @JsonProperty("dbTimeRange")
    private String timeRange;

    /**
     * 浏览图片路径
     */
    @TableField("browse_image_path")
    @JsonProperty("browseImagePath")
    private String browseImagePath;

    // 分页相关字段（不映射到数据库）
    @TableField(exist = false)
    @JsonProperty("pageNum")
    private Long pageNum;

    @TableField(exist = false)
    @JsonProperty("pageSize")
    private Long pageSize;

    // 查询条件字段（不映射到数据库）
    @TableField(exist = false)
    @JsonProperty("region")
    private Region region;

    @TableField(exist = false)
    @JsonProperty("queryTimeRange")
    private TimeRange queryTimeRange;

    @TableField(exist = false)
    @JsonProperty("queryBoundary")
    private Boundary queryBoundary;

    // 内部类定义
    @Data
    public static class Region {
        @JsonProperty("country")
        private String country;
        
        @JsonProperty("province")
        private String province;
        
        @JsonProperty("city")
        private String city;
        
        @JsonProperty("district")
        private String district;
    }

    @Data
    public static class TimeRange {
        @JsonProperty("beginTime")
        private String beginTime;
        
        @JsonProperty("endTime")
        private String endTime;
    }

    @Data
    public static class Boundary {
        @JsonProperty("geometry")
        private Object geometry;
        
        @JsonProperty("coordinates")
        private String coordinates;
        
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("geoJson")
        private String geoJson;
    }
}
