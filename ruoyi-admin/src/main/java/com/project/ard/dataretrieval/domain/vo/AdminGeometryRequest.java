package com.project.ard.dataretrieval.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("行政区几何查询请求")
public class AdminGeometryRequest {

    @ApiModelProperty("行政区类型：1-国家、2-省、3-市、4-区县")
    private Integer type;

    @ApiModelProperty("行政区名称，可选")
    private String name;

    @ApiModelProperty("行政区代码，必填")
    private String code;

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
















