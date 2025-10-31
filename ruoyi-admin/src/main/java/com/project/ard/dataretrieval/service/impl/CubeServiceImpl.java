package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.Cube;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalRequest;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalResponse;
import com.project.ard.dataretrieval.domain.vo.CubeDetailResponse;
import com.project.ard.dataretrieval.domain.vo.CubeSliceResponse;
import com.project.ard.dataretrieval.mapper.CubeMapper;
import com.project.ard.dataretrieval.service.CubeService;
import com.project.ard.dataretrieval.service.CubeSliceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    @Autowired
    private CubeSliceService cubeSliceService;
    
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
            log.info("执行数据库查询，当前页: {}, 每页大小: {}", current, size);
            IPage<Cube> cubePage = cubeMapper.selectPage(page, queryWrapper);
            
            log.info("数据库查询完成，查询到 {} 条记录", cubePage.getRecords().size());
            if (cubePage.getRecords().size() == 0 && request.getCubeName() != null && !request.getCubeName().trim().isEmpty()) {
                log.warn("⚠️ 警告：使用cubeName='{}'进行搜索，但未查询到任何结果", request.getCubeName().trim());
                log.warn("建议检查：1) 数据库表中是否存在该cube_id或grid_id 2) 字段名是否正确 3) 数据是否匹配");
            }
            
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
        
        log.info("===== 开始构建查询条件 =====");
        log.info("请求参数 - cubeName: {}", request.getCubeName());
        
        // 根据立方体ID或格网ID筛选（优先处理，确保ID搜索能正常工作）
        if (request.getCubeName() != null && !request.getCubeName().trim().isEmpty()) {
            String searchKeyword = request.getCubeName().trim();
            log.info("检测到立方体ID/格网ID搜索关键词: '{}'", searchKeyword);
            
            // 同时支持cube_id和grid_id的搜索：精确匹配cube_id，模糊匹配grid_id和cube_id
            // 使用nested包装确保条件组合正确
            queryWrapper.and(wrapper -> {
                wrapper.eq("cube_id", searchKeyword)  // 精确匹配cube_id（优先）
                      .or()
                      .like("cube_id", searchKeyword)  // 模糊匹配cube_id（部分ID）
                      .or()
                      .like("grid_id", searchKeyword); // 模糊匹配grid_id
            });
            
            log.info("✓ 已应用立方体ID/格网ID筛选条件");
            log.info("查询条件SQL片段: (cube_id = '{}' OR cube_id LIKE '%{}%' OR grid_id LIKE '%{}%')", 
                    searchKeyword, searchKeyword, searchKeyword);
        } else {
            log.info("✗ 立方体ID/格网ID为空或null，跳过ID筛选");
            if (request.getCubeName() == null) {
                log.info("  - cubeName字段为null");
            } else {
                log.info("  - cubeName字段值为空字符串或仅包含空格");
            }
        }
        
        
        // 注意：立方体查询不需要数据类型筛选，因为立方体本身就是数据存储格式
        
        // 注意：Cube表没有time_range字段，跳过时间范围筛选
        if (request.getTimeRange() != null) {
            log.info("Cube表没有time_range字段，跳过时间范围筛选");
        } else {
            log.info("没有提供时间范围，跳过时间筛选");
        }
        
        // 根据行政区筛选
        if (request.getRegion() != null) {
            log.info("Cube表有行政区字段，可以添加行政区筛选");
            // 可以根据需要添加province、city、county字段的筛选
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
                queryWrapper.apply("ST_Intersects(ST_SetSRID(bbox, 0), ST_SetSRID(ST_GeomFromGeoJSON({0}), 0))", geoJson);
                log.info("应用空间相交查询条件（强制设置SRID为0）");
            } else {
                log.info("边界信息为空或无效，跳过空间筛选，返回所有数据");
            }
        } else {
            log.info("没有提供边界信息，跳过空间筛选，返回所有数据");
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("created");
        
        // 输出查询条件摘要（用于调试）
        log.info("===== 查询条件构建完成 =====");
        log.info("查询条件摘要: {}", queryWrapper.toString());
        log.info("============================");
        
        return queryWrapper;
    }
    
    /**
     * 将Cube实体转换为CubeRetrievalResponse
     */
    private CubeRetrievalResponse convertToResponse(Cube cube) {
        CubeRetrievalResponse response = new CubeRetrievalResponse();
        response.setCubeId(cube.getCubeId());
        response.setGridId(cube.getGridId());
        response.setSecretLevel(cube.getSecretLevel());
        response.setDescription(cube.getDescription());
        response.setProvince(cube.getProvince());
        response.setCity(cube.getCity());
        response.setCounty(cube.getCounty());
        response.setCityDistrict(cube.getCityDistrict());
        response.setEpsg(cube.getEpsg());
        response.setGridType(cube.getGridType());
        response.setOrganization(cube.getOrganization());
        response.setDepartment(cube.getDepartment());
        response.setOperator(cube.getOperator());
        response.setEmail(cube.getEmail());
        response.setRole(cube.getRole());
        response.setTotalFiles(cube.getTotalFiles());
        response.setOriginalFiles(cube.getOriginalFiles());
        response.setDerivedFiles(cube.getDerivedFiles());
        response.setSeasonsCovered(cube.getSeasonsCovered());
        response.setTimeSpan(cube.getTimeSpan());
        response.setResolutionLevel(cube.getResolutionLevel());
        response.setCreated(cube.getCreated());
        response.setUpdated(cube.getUpdated());
        response.setCreatedBy(cube.getCreatedBy());
        
        // 处理边界数据 - 将PostGIS geometry字段转换为GeoJSON格式
        response.setBoundary(processBoundaryField(cube));
        
        return response;
    }
    
    /**
     * 处理bbox字段，将PostGIS geometry格式转换为GeoJSON格式
     */
    private String processBoundaryField(Cube cube) {
        try {
            // 如果bbox字段不为空，通过数据库查询获取GeoJSON格式
            if (cube.getBbox() != null && !cube.getBbox().trim().isEmpty()) {
                // 通过数据库查询获取GeoJSON格式的bbox
                Cube geoJsonCube = cubeMapper.selectByIdWithGeoJSON(cube.getCubeId());
                if (geoJsonCube != null && geoJsonCube.getBbox() != null) {
                    log.debug("已通过数据库转换bbox字段为GeoJSON格式: {}", cube.getCubeId());
                    return geoJsonCube.getBbox();
                } else {
                    log.warn("数据库查询bbox字段失败: {}", cube.getCubeId());
                    return cube.getBbox(); // 返回原始值
                }
            }
            return cube.getBbox();
        } catch (Exception e) {
            log.error("处理bbox字段时发生错误: {}", e.getMessage(), e);
            return cube.getBbox(); // 返回原始值
        }
    }
    
    @Override
    public CubeDetailResponse getCubeDetail(String cubeId) {
        try {
            log.info("查询立方体详情，立方体ID: {}", cubeId);
            
            // 查询立方体基本信息
            Cube cube = cubeMapper.selectByIdWithGeoJSON(cubeId);
            if (cube == null) {
                log.warn("未找到立方体数据，立方体ID: {}", cubeId);
                return null;
            }
            
            // 转换为详情响应对象
            CubeDetailResponse response = convertToDetailResponse(cube);
            
            // 查询相关的切片数据
            // 立方体ID是字符串格式，直接使用
            List<CubeSliceResponse> slices = cubeSliceService.getSlicesByCubeId(cubeId);
            response.setSlices(slices);
            log.info("查询到 {} 条切片数据", slices.size());
            
            log.info("立方体详情查询完成");
            return response;
            
        } catch (Exception e) {
            log.error("查询立方体详情时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("查询立方体详情失败", e);
        }
    }
    
    /**
     * 将Cube实体转换为CubeDetailResponse
     */
    private CubeDetailResponse convertToDetailResponse(Cube cube) {
        CubeDetailResponse response = new CubeDetailResponse();
        response.setCubeId(cube.getCubeId());
        response.setGridId(cube.getGridId());
        response.setSecretLevel(cube.getSecretLevel());
        response.setDescription(cube.getDescription());
        response.setProvince(cube.getProvince());
        response.setCity(cube.getCity());
        response.setCounty(cube.getCounty());
        response.setCityDistrict(cube.getCityDistrict());
        response.setEpsg(cube.getEpsg());
        response.setGridType(cube.getGridType());
        response.setOrganization(cube.getOrganization());
        response.setDepartment(cube.getDepartment());
        response.setOperator(cube.getOperator());
        response.setEmail(cube.getEmail());
        response.setRole(cube.getRole());
        response.setTotalFiles(cube.getTotalFiles());
        response.setOriginalFiles(cube.getOriginalFiles());
        response.setDerivedFiles(cube.getDerivedFiles());
        response.setSeasonsCovered(cube.getSeasonsCovered());
        response.setTimeSpan(cube.getTimeSpan());
        response.setResolutionLevel(cube.getResolutionLevel());
        response.setCreated(cube.getCreated());
        response.setUpdated(cube.getUpdated());
        response.setCreatedBy(cube.getCreatedBy());
        
        // 处理边界数据
        response.setBoundary(cube.getBbox());
        
        return response;
    }
}