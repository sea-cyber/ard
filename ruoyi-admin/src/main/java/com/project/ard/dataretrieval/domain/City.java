package com.project.ard.dataretrieval.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("city")
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "\"regionId\"")
    private String regionId;

    @TableField(value = "\"parentRegionId\"")
    private String parentRegionId;

    @TableField(value = "\"regionName\"")
    private String regionName;

    @TableField(value = "\"regionEnName\"")
    private String regionEnName;

    @TableField(value = "\"regionAlias\"")
    private String regionAlias;
}



