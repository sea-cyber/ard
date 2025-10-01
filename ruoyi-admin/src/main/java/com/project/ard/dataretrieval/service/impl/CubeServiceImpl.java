package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.Cube;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalRequest;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalResponse;
import com.project.ard.dataretrieval.mapper.CubeMapper;
import com.project.ard.dataretrieval.service.CubeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 立方体数据服务实现类
 * 
 * @author project
 */
@Slf4j
@Service
public class CubeServiceImpl extends ServiceImpl<CubeMapper, Cube> implements CubeService {
    
    @Autowired
    private CubeMapper cubeMapper;
    
    @Override
    public List<CubeRetrievalResponse> searchCubeData(CubeRetrievalRequest request) {
        try {
            log.info("开始查询立方体数据，请求参数: {}", request);
            
            // 构建查询条件
            QueryWrapper<Cube> queryWrapper = buildQueryWrapper(request);
            
            // 执行查询
            List<Cube> cubes = cubeMapper.selectList(queryWrapper);
            
            // 转换为响应对象
            List<CubeRetrievalResponse> results = cubes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            log.info("查询完成，返回 {} 条数据", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("查询立方体数据失败", e);
            throw new RuntimeException("查询立方体数据失败: " + e.getMessage());
        }
    }
    
    @Override
    public IPage<CubeRetrievalResponse> searchCubeDataPage(CubeRetrievalRequest request) {
        try {
            log.info("开始分页查询立方体数据，请求参数: {}", request);
            
            // 构建分页参数
            int current = request.getPage() != null ? request.getPage().getCurrent() : 1;
            int size = request.getPage() != null ? request.getPage().getSize() : 20;
            
            Page<Cube> page = new Page<>(current, size);
            
            // 构建查询条件
            QueryWrapper<Cube> queryWrapper = buildQueryWrapper(request);
            
            // 执行分页查询
            IPage<Cube> cubePage = cubeMapper.selectPage(page, queryWrapper);
            
            // 转换为响应对象
            List<CubeRetrievalResponse> records = cubePage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            // 创建响应分页对象
            Page<CubeRetrievalResponse> resultPage = new Page<>(current, size);
            resultPage.setRecords(records);
            resultPage.setTotal(cubePage.getTotal());
            resultPage.setPages(cubePage.getPages());
            
            log.info("分页查询完成，返回 {} 条数据，总计 {} 条", 
                records.size(), cubePage.getTotal());
            
            return resultPage;
            
        } catch (Exception e) {
            log.error("分页查询立方体数据失败", e);
            throw new RuntimeException("分页查询立方体数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建查询条件
     */
    private QueryWrapper<Cube> buildQueryWrapper(CubeRetrievalRequest request) {
        QueryWrapper<Cube> queryWrapper = new QueryWrapper<>();
        
        // 根据立方体名称筛选
        if (request.getCubeName() != null && !request.getCubeName().trim().isEmpty()) {
            queryWrapper.like("cube_name", request.getCubeName().trim());
            log.info("应用立方体名称筛选: {}", request.getCubeName());
        } else {
            log.info("立方体名称为空，跳过名称筛选");
        }
        
        // 根据Path编码筛选
        if (request.getPathCode() != null && !request.getPathCode().trim().isEmpty()) {
            queryWrapper.like("path_code", request.getPathCode().trim());
            log.info("应用Path编码筛选: {}", request.getPathCode());
        } else {
            log.info("Path编码为空，跳过Path编码筛选");
        }
        
        // 根据Row编码筛选
        if (request.getRowCode() != null && !request.getRowCode().trim().isEmpty()) {
            queryWrapper.like("row_code", request.getRowCode().trim());
            log.info("应用Row编码筛选: {}", request.getRowCode());
        } else {
            log.info("Row编码为空，跳过Row编码筛选");
        }
        
        // 注意：立方体查询不需要数据类型筛选，因为立方体本身就是数据存储格式
        
        // 根据时间范围筛选 - 使用time_range字段进行交集查询
        if (request.getTimeRange() != null && request.getTimeRange().getDateRange() != null) {
            List<String> dateRange = request.getTimeRange().getDateRange();
            if (dateRange.size() >= 2) {
                String beginTimeStr = dateRange.get(0);
                String endTimeStr = dateRange.get(1);
                
                if (beginTimeStr != null && !beginTimeStr.trim().isEmpty() && endTimeStr != null && !endTimeStr.trim().isEmpty()) {
                    try {
                        // 解析前端传来的日期字符串格式 "2025/08/28"
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        
                        // 转换为LocalDate，然后转换为LocalDateTime
                        LocalDate beginDate = LocalDate.parse(beginTimeStr.trim(), inputFormatter);
                        LocalDate endDate = LocalDate.parse(endTimeStr.trim(), inputFormatter);
                        
                        // 转换为LocalDateTime，开始时间设为00:00:00，结束时间设为23:59:59
                        LocalDateTime beginTime = beginDate.atStartOfDay();
                        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
                        
                        // 使用PostgreSQL的tstzrange交集查询
                        // 查询条件：time_range字段与查询时间范围有交集
                        // 使用&&操作符检查两个时间范围是否有交集
                        queryWrapper.apply("time_range && tstzrange({0}::timestamptz, {1}::timestamptz, '[]')", 
                            beginTime.toString(), endTime.toString());
                        
                        log.info("应用时间范围交集查询: {} 到 {}", beginTime, endTime);
                    } catch (Exception e) {
                        log.error("时间范围解析失败: {} 到 {}, 错误: {}", beginTimeStr, endTimeStr, e.getMessage());
                        log.info("跳过时间范围筛选");
                    }
                } else {
                    log.info("时间范围不完整，跳过时间筛选");
                }
            } else {
                log.info("时间范围为空，跳过时间筛选");
            }
        } else {
            log.info("没有提供时间范围，跳过时间筛选");
        }
        
        // 注意：Cube表没有行政区字段，跳过行政区筛选
        if (request.getRegion() != null) {
            log.info("Cube表没有行政区字段，跳过行政区筛选");
        } else {
            log.info("没有提供行政区信息，跳过行政区筛选");
        }
        
        // 根据边界信息进行空间相交查询
        if (request.getBoundary() != null) {
            log.info("开始处理边界查询条件");
            
            String geoJson = null;
            boolean hasValidBoundary = false;
            
            // 优先使用geometry.geom字段（与TifSearch一致）
            if (request.getBoundary().getGeometry() != null) {
                Object geometryObj = request.getBoundary().getGeometry();
                log.info("使用geometry字段进行查询: {}", geometryObj);
                
                if (geometryObj instanceof String) {
                    geoJson = (String) geometryObj;
                    hasValidBoundary = true;
                } else if (geometryObj instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> geometryMap = (java.util.Map<String, Object>) geometryObj;
                    if (geometryMap.containsKey("geom")) {
                        Object geomValue = geometryMap.get("geom");
                        if (geomValue instanceof String) {
                            geoJson = (String) geomValue;
                            hasValidBoundary = true;
                        }
                    }
                }
            }
            
            // 如果geometry字段无效，尝试使用coordinates字段构建GeoJSON
            if (!hasValidBoundary && request.getBoundary().getCoordinates() != null) {
                String coordinates = request.getBoundary().getCoordinates();
                String type = request.getBoundary().getType();
                
                if (coordinates != null && !coordinates.trim().isEmpty() && type != null) {
                    try {
                        geoJson = String.format("{\"type\":\"%s\",\"coordinates\":%s}", type, coordinates);
                        hasValidBoundary = true;
                        log.info("使用coordinates字段构建GeoJSON: {}", geoJson);
                    } catch (Exception e) {
                        log.warn("构建GeoJSON失败: {}", e.getMessage());
                    }
                }
            }
            
            // 如果以上都无效，尝试使用geoJson字段
            if (!hasValidBoundary && request.getBoundary().getGeoJson() != null) {
                geoJson = request.getBoundary().getGeoJson();
                hasValidBoundary = !geoJson.trim().isEmpty();
                log.info("使用geoJson字段: {}", geoJson);
            }
            
            if (hasValidBoundary && geoJson != null && !geoJson.trim().isEmpty()) {
                // 使用PostGIS的ST_Intersects函数进行空间相交查询
                // 方法1：将查询几何体从SRID 4326转换到SRID 4490
                // 方法2：如果转换失败，则强制设置两个几何体的SRID为0（无SRID）进行比较
                queryWrapper.apply("ST_Intersects(ST_SetSRID(boundary, 0), ST_SetSRID(ST_GeomFromGeoJSON({0}), 0))", geoJson);
                log.info("应用空间相交查询条件（强制设置SRID为0）");
            } else {
                log.info("边界信息为空或无效，跳过空间筛选，返回所有数据");
            }
        } else {
            log.info("没有提供边界信息，跳过空间筛选，返回所有数据");
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("create_time");
        
        return queryWrapper;
    }
    
    /**
     * 将Cube实体转换为CubeRetrievalResponse
     */
    private CubeRetrievalResponse convertToResponse(Cube cube) {
        CubeRetrievalResponse response = new CubeRetrievalResponse();
        response.setId(cube.getId());
        response.setCubeName(cube.getCubeName());
        response.setCreateUser(cube.getCreateUser());
        response.setCreateTime(cube.getCreateTime());
        response.setDataType(cube.getDataType());
        response.setDataDescribe(cube.getDataDescribe());
        response.setCompressionAlgorithm(cube.getCompressionAlgorithm());
        response.setPathCode(cube.getPathCode());
        response.setRowCode(cube.getRowCode());
        response.setTimeRange(cube.getTimeRange());
        
        // 处理边界数据 - 将PostGIS geometry字段转换为GeoJSON格式
        response.setBoundary(processBoundaryField(cube));
        
        return response;
    }
    
    /**
     * 处理boundary字段，将PostGIS geometry格式转换为GeoJSON格式
     */
    private String processBoundaryField(Cube cube) {
        try {
            // 如果boundary字段不为空，通过数据库查询获取GeoJSON格式
            if (cube.getBoundary() != null && !cube.getBoundary().trim().isEmpty()) {
                // 通过数据库查询获取GeoJSON格式的boundary
                Cube geoJsonCube = cubeMapper.selectByIdWithGeoJSON(cube.getId());
                if (geoJsonCube != null && geoJsonCube.getBoundary() != null) {
                    log.debug("已通过数据库转换boundary字段为GeoJSON格式: {}", cube.getId());
                    return geoJsonCube.getBoundary();
                } else {
                    log.warn("数据库查询boundary字段失败: {}", cube.getId());
                    return cube.getBoundary(); // 返回原始值
                }
            }
            return cube.getBoundary();
        } catch (Exception e) {
            log.error("处理boundary字段时发生错误: {}", e.getMessage(), e);
            return cube.getBoundary(); // 返回原始值
        }
    }
}