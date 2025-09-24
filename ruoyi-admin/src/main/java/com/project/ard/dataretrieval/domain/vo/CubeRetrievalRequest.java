package com.project.ard.dataretrieval.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 立方体数据检索请求参数
 * 
 * @author project
 */
@ApiModel("立方体数据检索请求参数")
public class CubeRetrievalRequest {

    @ApiModelProperty("区域信息")
    private Region region;

    @ApiModelProperty("时间范围")
    private TimeRange timeRange;

    @ApiModelProperty("边界信息")
    private Boundary boundary;

    @ApiModelProperty("分页信息")
    private PageInfo page;

    @ApiModelProperty("立方体名称")
    private String cubeName;

    @ApiModelProperty("Path编码")
    private String pathCode;

    @ApiModelProperty("Row编码")
    private String rowCode;

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

    public String getCubeName() {
        return cubeName;
    }

    public void setCubeName(String cubeName) {
        this.cubeName = cubeName;
    }

    public String getPathCode() {
        return pathCode;
    }

    public void setPathCode(String pathCode) {
        this.pathCode = pathCode;
    }

    public String getRowCode() {
        return rowCode;
    }

    public void setRowCode(String rowCode) {
        this.rowCode = rowCode;
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

        @ApiModelProperty("几何体信息")
        private Object geometry;

        @ApiModelProperty("边界范围")
        private Object extent;

        @ApiModelProperty("边界来源")
        private String source;

        @ApiModelProperty("行政区类型")
        private Integer regionType;

        @ApiModelProperty("行政区代码")
        private String regionCode;

        @ApiModelProperty("行政区名称")
        private String regionName;

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

        public Object getGeometry() {
            return geometry;
        }

        public void setGeometry(Object geometry) {
            this.geometry = geometry;
        }

        public Object getExtent() {
            return extent;
        }

        public void setExtent(Object extent) {
            this.extent = extent;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public Integer getRegionType() {
            return regionType;
        }

        public void setRegionType(Integer regionType) {
            this.regionType = regionType;
        }

        public String getRegionCode() {
            return regionCode;
        }

        public void setRegionCode(String regionCode) {
            this.regionCode = regionCode;
        }

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
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




