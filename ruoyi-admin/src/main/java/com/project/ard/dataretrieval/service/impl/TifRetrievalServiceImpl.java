package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.ard.dataretrieval.domain.RsTifFile;
import com.project.ard.dataretrieval.domain.vo.TifRetrievalRequest;
import com.project.ard.dataretrieval.domain.vo.TifRetrievalResponse;
import com.project.ard.dataretrieval.service.IRsTifFileService;
import com.project.ard.dataretrieval.service.ITifRetrievalService;
import com.project.common.core.page.TableDataInfo;
import com.project.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/**
 * TIF数据检索服务实现
 */
@Service
public class TifRetrievalServiceImpl implements ITifRetrievalService {

    private static final Logger logger = LoggerFactory.getLogger(TifRetrievalServiceImpl.class);

    @Autowired
    private IRsTifFileService rsTifFileService;

    @Override
    public TableDataInfo searchTifData(TifRetrievalRequest request) {
        logger.info("=== TIF数据检索服务 ===");
        logger.info("请求参数: {}", request);
        
        // 构建查询条件
        QueryWrapper<RsTifFile> queryWrapper = buildQueryWrapper(request);
        
        // 执行分页查询
        Page<RsTifFile> page = new Page<>(request.getPage().getCurrent(), request.getPage().getSize());
        Page<RsTifFile> resultPage = rsTifFileService.page(page, queryWrapper);
        
        // 处理查询结果，将boundary字段转换为GeoJSON格式
        List<RsTifFile> processedRecords = resultPage.getRecords().stream()
            .map(this::processBoundaryFieldWithDatabase)
            .collect(java.util.stream.Collectors.toList());
        
        // 构建返回结果
        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setCode(200);
        dataInfo.setMsg("查询成功");
        dataInfo.setRows(processedRecords);
        dataInfo.setTotal(resultPage.getTotal());
        
        logger.info("查询结果: 总数={}, 当前页数据量={}", resultPage.getTotal(), processedRecords.size());
        return dataInfo;
    }

    /**
     * 构建查询条件
     */
    private QueryWrapper<RsTifFile> buildQueryWrapper(TifRetrievalRequest request) {
        QueryWrapper<RsTifFile> queryWrapper = new QueryWrapper<>();
        
        // 1. 根据卫星信息筛选
        if (request.getDataType() != null && request.getDataType().getSatellites() != null) {
            List<String> satelliteIds = new java.util.ArrayList<>();
            List<String> sensorIds = new java.util.ArrayList<>();
            
            logger.info("处理卫星筛选条件，卫星数量: {}", request.getDataType().getSatellites().size());
            
            for (TifRetrievalRequest.Satellite satellite : request.getDataType().getSatellites()) {
                String satelliteId = satellite.getSatellite() != null ? satellite.getSatellite() : satellite.getCode();
                if (satelliteId != null) {
                    satelliteIds.add(satelliteId);
                    logger.info("添加卫星ID: {}", satelliteId);
                }
                
                if (satellite.getSensors() != null) {
                    sensorIds.addAll(satellite.getSensors());
                    logger.info("添加传感器: {}", satellite.getSensors());
                }
            }
            
            if (!satelliteIds.isEmpty()) {
                queryWrapper.in("satellite_id", satelliteIds);
                logger.info("应用卫星筛选条件: {}", satelliteIds);
            }
            
            if (!sensorIds.isEmpty()) {
                queryWrapper.in("sensor_id", sensorIds);
                logger.info("应用传感器筛选条件: {}", sensorIds);
            }
        }
        
        // 2. 根据云量范围筛选
        if (request.getCloudAmount() != null) {
            logger.info("处理云量筛选条件: min={}, max={}", 
                request.getCloudAmount().getMin(), request.getCloudAmount().getMax());
            
            if (StringUtils.isNotEmpty(request.getCloudAmount().getMin())) {
                try {
                    Double minCloud = Double.parseDouble(request.getCloudAmount().getMin());
                    queryWrapper.ge("cloud_percent", minCloud);
                    logger.info("应用云量最小值筛选: {}", minCloud);
                } catch (NumberFormatException e) {
                    logger.warn("云量最小值格式错误: {}", request.getCloudAmount().getMin());
                }
            }
            if (StringUtils.isNotEmpty(request.getCloudAmount().getMax())) {
                try {
                    Double maxCloud = Double.parseDouble(request.getCloudAmount().getMax());
                    queryWrapper.le("cloud_percent", maxCloud);
                    logger.info("应用云量最大值筛选: {}", maxCloud);
                } catch (NumberFormatException e) {
                    logger.warn("云量最大值格式错误: {}", request.getCloudAmount().getMax());
                }
            }
        }
        
        // 3. 根据采集时间范围筛选
        if (request.getTimeRange() != null && request.getTimeRange().getDateRange() != null 
            && request.getTimeRange().getDateRange().size() >= 2) {
            String startStr = request.getTimeRange().getDateRange().get(0);
            String endStr = request.getTimeRange().getDateRange().get(1);

            if (StringUtils.isNotEmpty(startStr)) {
                String s = startStr.trim().replace('/', '-').replace('.', '-');
                LocalDate d = LocalDate.parse(s);
                LocalDateTime startDt = d.atStartOfDay();
                queryWrapper.ge("acquisition_time", startDt);
            }

            if (StringUtils.isNotEmpty(endStr)) {
                String s = endStr.trim().replace('/', '-').replace('.', '-');
                LocalDate d = LocalDate.parse(s);
                LocalDateTime endDt = d.atTime(LocalTime.MAX);
                queryWrapper.le("acquisition_time", endDt);
            }
        }

        // 4. 根据空间边界筛选 - 使用PostGIS函数
        if (request.getBoundary() != null) {
            System.out.println("处理空间边界筛选条件: " + request.getBoundary());
            TifRetrievalRequest.Boundary boundary = request.getBoundary();
            logger.info("处理空间边界筛选条件: type={}, source={}", boundary.getType(), boundary.getSource());
            
            String geoJson = null;
            boolean hasValidBoundary = false;
            
            // 根据边界来源处理不同的GeoJSON数据
            if ("region_selection".equals(boundary.getSource())) {
                // 行政区边界 - 从geometry.geom字段获取GeoJSON
                if (boundary.getGeometry() != null) {
                    try {
                        // 使用反射或JSON解析来获取geom字段
                        geoJson = extractGeomFromGeometry(boundary.getGeometry());
                        if (geoJson != null && !geoJson.trim().isEmpty()) {
                            hasValidBoundary = true;
                            logger.info("使用行政区边界geometry.geom: {}", geoJson);
                        }
                    } catch (Exception e) {
                        logger.error("解析geometry.geom字段失败: {}", e.getMessage());
                        // 降级处理：直接使用geometry.toString()
                        geoJson = boundary.getGeometry().toString();
                        if (geoJson != null && !geoJson.trim().isEmpty()) {
                            hasValidBoundary = true;
                            logger.info("降级使用行政区边界geometry: {}", geoJson);
                        }
                    }
                }
            } else if ("box_draw".equals(boundary.getSource()) || "polygon_draw".equals(boundary.getSource())) {
                // 自定义绘制边界 - 从geometry.geom字段获取GeoJSON
                if (boundary.getGeometry() != null) {
                    try {
                        // 使用extractGeomFromGeometry方法获取GeoJSON
                        geoJson = extractGeomFromGeometry(boundary.getGeometry());
                        if (geoJson != null && !geoJson.trim().isEmpty()) {
                            hasValidBoundary = true;
                            logger.info("使用自定义绘制边界geometry.geom: {}", geoJson);
                        }
                    } catch (Exception e) {
                        logger.error("解析绘制边界geometry.geom字段失败: {}", e.getMessage());
                        // 降级处理：使用coordinates字段构建GeoJSON
                        if (boundary.getCoordinates() != null && !boundary.getCoordinates().trim().isEmpty()) {
                            geoJson = String.format("{\"type\":\"%s\",\"coordinates\":%s}", 
                                boundary.getType(), boundary.getCoordinates());
                            hasValidBoundary = true;
                            logger.info("降级使用自定义绘制边界coordinates: {}", geoJson);
                        }
                    }
                }
            } else {
                // 兼容原有的getFullGeoJson方法
                geoJson = boundary.getFullGeoJson();
                if (geoJson != null && !geoJson.trim().isEmpty()) {
                    hasValidBoundary = true;
                    logger.info("使用兼容模式边界: {}", geoJson);
                }
            }
            
            if (hasValidBoundary && geoJson != null && !geoJson.trim().isEmpty()) {
                // 使用PostGIS的ST_Intersects函数进行空间相交查询
                // 方法1：将查询几何体从SRID 4326转换到SRID 4490
                // 方法2：如果转换失败，则强制设置两个几何体的SRID为0（无SRID）进行比较
                queryWrapper.apply("ST_Intersects(ST_SetSRID(boundary, 0), ST_SetSRID(ST_GeomFromGeoJSON({0}), 0))", geoJson);
                logger.info("应用空间相交查询条件（强制设置SRID为0）");
            } else {
                logger.info("边界信息为空或无效，跳过空间筛选，返回所有数据");
            }
        } else {
            logger.info("没有提供边界信息，跳过空间筛选，返回所有数据");
        }
        
        // 5. 按采集时间倒序排列
        queryWrapper.orderByDesc("acquisition_time");
        
        return queryWrapper;
    }

    @Override
    public TifRetrievalResponse getDataDetail(String dataId) {
        // 简单的模拟数据
        TifRetrievalResponse response = new TifRetrievalResponse();
        response.setId(Long.parseLong(dataId));
        response.setCubeName("模拟文件_" + dataId + ".tif");
        response.setCreateUser("admin");
        response.setDataType("TIF");
        response.setDataDescribe("模拟数据描述");
        response.setCompressionAlgorithm("LZW");
        response.setBoundary("{\"type\":\"Polygon\",\"coordinates\":[[[116.0,39.0],[116.1,39.0],[116.1,39.1],[116.0,39.1],[116.0,39.0]]]}");
        return response;
    }

    @Override
    public String downloadData(String dataId) {
        return "http://localhost:8080/download/" + dataId;
    }

    @Override
    public String getPreviewImage(String dataId) {
        return "http://localhost:8080/preview/" + dataId;
    }

    @Override
    public List<String> getSatelliteList() {
        return Arrays.asList("LANDSAT8", "SENTINEL2", "MODIS");
    }

    @Override
    public List<String> getProductTypeList() {
        return Arrays.asList("L1T", "L2A", "L3A");
    }

    /**
     * 处理boundary字段，通过数据库查询将二进制格式转换为GeoJSON格式
     * @param rsTifFile 原始数据对象
     * @return 处理后的数据对象
     */
    private RsTifFile processBoundaryFieldWithDatabase(RsTifFile rsTifFile) {
        try {
            // 如果boundary字段不为空，通过数据库查询获取GeoJSON格式
            if (rsTifFile.getBoundary() != null && !rsTifFile.getBoundary().trim().isEmpty()) {
                // 通过数据库查询获取GeoJSON格式的boundary
                RsTifFile geoJsonFile = rsTifFileService.getByIdWithGeoJSON(rsTifFile.getId().longValue());
                if (geoJsonFile != null && geoJsonFile.getBoundary() != null) {
                    rsTifFile.setBoundary(geoJsonFile.getBoundary());
                    logger.debug("已通过数据库转换boundary字段为GeoJSON格式: {}", rsTifFile.getId());
                } else {
                    logger.warn("数据库查询boundary字段失败: {}", rsTifFile.getId());
                }
            }
            return rsTifFile;
        } catch (Exception e) {
            logger.error("处理boundary字段时发生错误: {}", e.getMessage(), e);
            return rsTifFile; // 返回原始对象
        }
    }



    /**
     * 从geometry对象中提取geom字段的GeoJSON字符串
     * @param geometry 前端传递的geometry对象
     * @return GeoJSON字符串
     */
    private String extractGeomFromGeometry(Object geometry) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            
            // 将geometry对象转换为Map
            Map<?, ?> rawMap = objectMapper.convertValue(geometry, Map.class);
            Map<String, Object> geometryMap = new java.util.HashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                geometryMap.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            
            // 从Map中获取geom字段
            Object geomObj = geometryMap.get("geom");
            if (geomObj != null) {
                // 如果geom是字符串，直接返回
                if (geomObj instanceof String) {
                    return (String) geomObj;
                } else {
                    // 如果geom是对象，转换为JSON字符串
                    return objectMapper.writeValueAsString(geomObj);
                }
            }
            
            logger.warn("geometry对象中没有找到geom字段: {}", geometryMap);
            return null;
            
        } catch (Exception e) {
            logger.error("解析geometry对象失败: {}", e.getMessage(), e);
            throw new RuntimeException("解析geometry对象失败", e);
        }
    }
}
