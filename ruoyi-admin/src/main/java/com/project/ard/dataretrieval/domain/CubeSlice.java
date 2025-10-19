package com.project.ard.dataretrieval.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.ibatis.type.JdbcType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

/**
 * 立方体切片数据实体类
 * 对应 cube_slice_info 表
 * 
 * @author project
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cube_slice_info")
public class CubeSlice {

    /**
     * 切片ID
     */
    @TableId(value = "slice_id", type = IdType.AUTO)
    private Integer sliceId;
    
    /**
     * 立方体ID
     */
    @TableField("cube_id")
    private String cubeId;
    
    /**
     * 季度
     */
    @TableField("quarter")
    private String quarter;
    
    /**
     * 切片路径
     */
    @TableField("slice_path")
    private String slicePath;
    
    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;
    
    /**
     * 文件格式
     */
    @TableField("file_format")
    private String fileFormat;
    
    /**
     * 浏览图路径
     */
    @TableField("browse_image_path")
    private String browseImagePath;
    
    /**
     * 浏览图文件名
     */
    @TableField("browse_file_name")
    private String browseFileName;
    
    /**
     * 浏览图格式
     */
    @TableField("browse_format")
    private String browseFormat;
    
    /**
     * 成像时间
     */
    @TableField(value = "imaging_time", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime imagingTime;
    
    /**
     * 位置
     */
    @TableField("location")
    private String location;
    
    /**
     * 切片描述
     */
    @TableField("slice_desc")
    private String sliceDesc;
    
    /**
     * 分辨率
     */
    @TableField("resolution")
    private String resolution;
    
    /**
     * 创建时间
     */
    @TableField(value = "created", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime created;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime updated;
    
    /**
     * 创建者
     */
    @TableField("created_by")
    private String createdBy;
}

