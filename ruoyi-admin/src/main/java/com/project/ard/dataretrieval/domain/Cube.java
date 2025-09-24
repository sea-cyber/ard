package com.project.ard.dataretrieval.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.ibatis.type.JdbcType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;
/**
 * 立方体数据实体类
 * 
 * @author project
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cube")
public class Cube {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 立方体名称
     */
    @TableField("cube_name")
    private String cubeName;
    
    /**
     * 创建用户
     */
    @TableField("create_user")
    private String createUser;
    
    /**
     * 边界几何信息 (PostGIS geometry类型)
     */
    @TableField("boundary")
    private String boundary;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", jdbcType = JdbcType.TIME)
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime createTime;
    
    /**
     * 数据类型 (ARD, HDF5, TIF, NETCDF, ZARR)
     */
    @TableField("data_type")
    private String dataType;
    
    /**
     * 数据描述
     */
    @TableField("data_describe")
    private String dataDescribe;
    
    /**
     * 压缩算法 (LZW, DEFLATE, JPEG2000, ZSTD, LZ4, NONE)
     */
    @TableField("compression_algorithm")
    private String compressionAlgorithm;
    
    /**
     * Path编码
     */
    @TableField("path_code")
    private String pathCode;
    
    /**
     * Row编码
     */
    @TableField("row_code")
    private String rowCode;
    
    /**
     * 时间范围 (PostgreSQL tstzrange类型)
     */
    @TableField("time_range")
    private String timeRange;
}
