package com.project.ard.dataretrieval.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TIF数据检索请求参数
 * 
 * @author ard
 */
@ApiModel("TIF数据检索请求参数")
public class TifRetrievalRequest {

    @ApiModelProperty("区域信息")
    @NotNull(message = "区域信息不能为空")
    private Region region;

    @ApiModelProperty("时间范围")
    @NotNull(message = "时间范围不能为空")
    private TimeRange timeRange;

    @ApiModelProperty("云量信息")
    private CloudAmount cloudAmount;

    @ApiModelProperty("数据类型")
    @NotNull(message = "数据类型不能为空")
    private DataType dataType;

    @ApiModelProperty("边界信息")
    private Boundary boundary;

    @ApiModelProperty("分页信息")
    private PageInfo page;

    public Region getRegion() {
        return region;
    }
    
    public void setRegion(Region region) {
        this.region = region;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    public CloudAmount getCloudAmount() {
        return cloudAmount;
    }

    public void setCloudAmount(CloudAmount cloudAmount) {
        this.cloudAmount = cloudAmount;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Boundary getBoundary() {
        return boundary;
    }

    public void setBoundary(Boundary boundary) {
        this.boundary = boundary;
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }

    /**
     * 区域信息
     */
    @ApiModel("区域信息")
    public static class Region {
        @ApiModelProperty("国家")
        private String country;

        @ApiModelProperty("省份")
        private String province;

        @ApiModelProperty("城市")
        private String city;

        @ApiModelProperty("区县")
        private String district;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }
    }

    /**
     * 时间范围
     */
    @ApiModel("时间范围")
    public static class TimeRange {
        @ApiModelProperty("日期范围")
        private List<String> dateRange;

        @ApiModelProperty("快速选择范围")
        private String quickRange;

        public List<String> getDateRange() {
            return dateRange;
        }

        public void setDateRange(List<String> dateRange) {
            this.dateRange = dateRange;
        }

        public String getQuickRange() {
            return quickRange;
        }

        public void setQuickRange(String quickRange) {
            this.quickRange = quickRange;
        }
    }

    /**
     * 云量信息
     */
    @ApiModel("云量信息")
    public static class CloudAmount {
        @ApiModelProperty("最小云量")
        private String min;

        @ApiModelProperty("最大云量")
        private String max;

        @ApiModelProperty("云量范围")
        private List<Integer> range;

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public List<Integer> getRange() {
            return range;
        }

        public void setRange(List<Integer> range) {
            this.range = range;
        }
    }

    /**
     * 数据类型
     */
    @ApiModel("数据类型")
    public static class DataType {
        @ApiModelProperty("数据类型")
        private String type;

        @ApiModelProperty("卫星列表")
        private List<Satellite> satellites;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Satellite> getSatellites() {
            return satellites;
        }

        public void setSatellites(List<Satellite> satellites) {
            this.satellites = satellites;
        }
    }

    /**
     * 卫星信息
     */
    @ApiModel("卫星信息")
    public static class Satellite {
        @ApiModelProperty("卫星名称")
        private String name;

        @ApiModelProperty("卫星代码")
        private String code;

        @ApiModelProperty("卫星标识")
        private String satellite;

        @ApiModelProperty("传感器列表")
        private List<String> sensors;

        @ApiModelProperty("是否选中")
        private Boolean selected;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public String getSatellite() {
            return satellite;
        }

        public void setSatellite(String satellite) {
            this.satellite = satellite;
        }

        public List<String> getSensors() {
            return sensors;
        }

        public void setSensors(List<String> sensors) {
            this.sensors = sensors;
        }
    }

    /**
     * 边界信息
     */
    @ApiModel("边界信息")
    public static class Boundary {
        @ApiModelProperty("边界类型")
        private String type;

        @ApiModelProperty("边界坐标")
        private String coordinates;

        @ApiModelProperty("完整的GeoJSON字符串")
        private String geoJson;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(String coordinates) {
            this.coordinates = coordinates;
        }

        public String getGeoJson() {
            return geoJson;
        }

        public void setGeoJson(String geoJson) {
            this.geoJson = geoJson;
        }

        /**
         * 获取完整的GeoJSON字符串
         */
        public String getFullGeoJson() {
            if (geoJson != null && !geoJson.trim().isEmpty()) {
                return geoJson;
            }
            
            if (type != null && coordinates != null) {
                return String.format("{\"type\":\"%s\",\"coordinates\":%s}", type, coordinates);
            }
            
            return null;
        }
    }

    /**
     * 分页信息
     */
    @ApiModel("分页信息")
    public static class PageInfo {
        @ApiModelProperty("当前页码")
        private Integer current = 1;

        @ApiModelProperty("每页大小")
        private Integer size = 10;

        public Integer getCurrent() {
            return current;
        }

        public void setCurrent(Integer current) {
            this.current = current;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }
    }
}
