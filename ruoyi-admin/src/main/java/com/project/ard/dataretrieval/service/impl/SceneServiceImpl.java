package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.Scene;
import com.project.ard.dataretrieval.mapper.SceneMapper;
import com.project.ard.dataretrieval.service.SceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 场景服务实现类
 * 
 * @author system
 * @date 2024-01-01
 */
@Slf4j
@Service
public class SceneServiceImpl extends ServiceImpl<SceneMapper, Scene> implements SceneService {

    @Autowired
    private SceneMapper sceneMapper;

    @Override
    public List<Scene> searchSceneData(Scene scene) {
        try {
            log.info("开始查询场景数据，请求参数: {}", scene);
            
            // 构建查询条件
            QueryWrapper<Scene> queryWrapper = buildQueryWrapper(scene);
            
            // 执行查询
            List<Scene> scenes = sceneMapper.selectList(queryWrapper);
            
            // 处理边界数据
            List<Scene> results = scenes.stream()
                .map(this::processSceneData)
                .collect(java.util.stream.Collectors.toList());
            
            log.info("查询完成，返回 {} 条数据", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("查询场景数据失败", e);
            throw new RuntimeException("查询场景数据失败: " + e.getMessage());
        }
    }

    @Override
    public IPage<Scene> searchSceneDataPage(Scene scene) {
        try {
            log.info("开始分页查询场景数据，请求参数: {}", scene);
            
            // 构建分页参数
            int current = scene.getPageNum() != null ? scene.getPageNum().intValue() : 1;
            int size = scene.getPageSize() != null ? scene.getPageSize().intValue() : 20;
            
            Page<Scene> page = new Page<>(current, size);
            
            // 构建查询条件
            QueryWrapper<Scene> queryWrapper = buildQueryWrapper(scene);
            
            // 执行分页查询
            IPage<Scene> scenePage = sceneMapper.selectPage(page, queryWrapper);
            
            // 处理边界数据
            List<Scene> records = scenePage.getRecords().stream()
                .map(this::processSceneData)
                .collect(java.util.stream.Collectors.toList());
            
            // 创建响应分页对象
            Page<Scene> resultPage = new Page<>(current, size);
            resultPage.setRecords(records);
            resultPage.setTotal(scenePage.getTotal());
            resultPage.setPages(scenePage.getPages());
            
            log.info("分页查询完成，返回 {} 条数据，总计 {} 条", 
                records.size(), scenePage.getTotal());
            
            return resultPage;
            
        } catch (Exception e) {
            log.error("分页查询场景数据失败", e);
            throw new RuntimeException("分页查询场景数据失败: " + e.getMessage());
        }
    }

    /**
     * 构建查询条件
     * 
     * @param scene 查询条件
     * @return 查询包装器
     */
    private QueryWrapper<Scene> buildQueryWrapper(Scene scene) {
        QueryWrapper<Scene> queryWrapper = new QueryWrapper<>();
        
        // 场景名称模糊查询
        if (scene.getSceneName() != null && !scene.getSceneName().trim().isEmpty()) {
            queryWrapper.like("scene_name", scene.getSceneName().trim());
            log.info("应用场景名称筛选: {}", scene.getSceneName());
        } else {
            log.info("场景名称为空，跳过名称筛选");
        }
        
        // 创建用户查询
        if (scene.getCreateUser() != null && !scene.getCreateUser().trim().isEmpty()) {
            queryWrapper.eq("create_user", scene.getCreateUser().trim());
            log.info("应用创建用户筛选: {}", scene.getCreateUser());
        } else {
            log.info("创建用户为空，跳过用户筛选");
        }
        
        // 数据类型查询
        if (scene.getDataType() != null && !scene.getDataType().trim().isEmpty()) {
            queryWrapper.eq("data_type", scene.getDataType().trim());
            log.info("应用数据类型筛选: {}", scene.getDataType());
        } else {
            log.info("数据类型为空，跳过数据类型筛选");
        }
        
        // 是否分析查询
        if (scene.getIsAnalysis() != null) {
            queryWrapper.eq("is_analysis", scene.getIsAnalysis());
            log.info("应用分析状态筛选: {}", scene.getIsAnalysis());
        } else {
            log.info("分析状态为空，跳过分析状态筛选");
        }
        
        // 分析类型查询
        if (scene.getAnalysisType() != null && !scene.getAnalysisType().trim().isEmpty()) {
            queryWrapper.eq("analysis_type", scene.getAnalysisType().trim());
            log.info("应用分析类型筛选: {}", scene.getAnalysisType());
        } else {
            log.info("分析类型为空，跳过分析类型筛选");
        }
        
        // Path编码查询
        if (scene.getPathCode() != null && !scene.getPathCode().trim().isEmpty()) {
            queryWrapper.like("path_code", scene.getPathCode().trim());
            log.info("应用Path编码筛选: {}", scene.getPathCode());
        } else {
            log.info("Path编码为空，跳过Path编码筛选");
        }
        
        // Row编码查询
        if (scene.getRowCode() != null && !scene.getRowCode().trim().isEmpty()) {
            queryWrapper.like("row_code", scene.getRowCode().trim());
            log.info("应用Row编码筛选: {}", scene.getRowCode());
        } else {
            log.info("Row编码为空，跳过Row编码筛选");
        }
        
        // 根据时间范围筛选 - 使用create_time字段
        if (scene.getQueryTimeRange() != null) {
            String beginTime = scene.getQueryTimeRange().getBeginTime();
            String endTime = scene.getQueryTimeRange().getEndTime();
            
            if (beginTime != null && !beginTime.trim().isEmpty() && endTime != null && !endTime.trim().isEmpty()) {
                // 使用create_time字段进行时间范围筛选
                queryWrapper.ge("create_time", beginTime);
                queryWrapper.le("create_time", endTime);
                log.info("应用时间范围筛选: {} 到 {}", beginTime, endTime);
            } else {
                log.info("时间范围不完整，跳过时间筛选");
            }
        } else {
            log.info("没有提供时间范围，跳过时间筛选");
        }
        
        // 注意：Scene表没有行政区字段，跳过行政区筛选
        if (scene.getRegion() != null) {
            log.info("Scene表没有行政区字段，跳过行政区筛选");
        } else {
            log.info("没有提供行政区信息，跳过行政区筛选");
        }
        
        // 根据边界信息进行空间相交查询
        if (scene.getQueryBoundary() != null) {
            log.info("开始处理边界查询条件");
            
            String geoJson = null;
            boolean hasValidBoundary = false;
            
            // 优先使用geometry.geom字段（与TifSearch一致）
            if (scene.getQueryBoundary().getGeometry() != null) {
                Object geometryObj = scene.getQueryBoundary().getGeometry();
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
            if (!hasValidBoundary && scene.getQueryBoundary().getCoordinates() != null) {
                String coordinates = scene.getQueryBoundary().getCoordinates();
                String type = scene.getQueryBoundary().getType();
                
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
            if (!hasValidBoundary && scene.getQueryBoundary().getGeoJson() != null) {
                geoJson = scene.getQueryBoundary().getGeoJson();
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
     * 处理场景数据，将PostGIS geometry格式转换为GeoJSON格式
     */
    private Scene processSceneData(Scene scene) {
        try {
            // 如果boundary字段不为空，通过数据库查询获取GeoJSON格式
            if (scene.getBoundary() != null && !scene.getBoundary().trim().isEmpty()) {
                // 通过数据库查询获取GeoJSON格式的boundary
                Scene geoJsonScene = sceneMapper.selectByIdWithGeoJSON(scene.getId());
                if (geoJsonScene != null && geoJsonScene.getBoundary() != null) {
                    log.debug("已通过数据库转换boundary字段为GeoJSON格式: {}", scene.getId());
                    scene.setBoundary(geoJsonScene.getBoundary());
                } else {
                    log.warn("数据库查询boundary字段失败: {}", scene.getId());
                }
            }
            return scene;
        } catch (Exception e) {
            log.error("处理boundary字段时发生错误: {}", e.getMessage(), e);
            return scene; // 返回原始值
        }
    }
}
