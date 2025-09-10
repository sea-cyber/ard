package com.project.ard.dataretrieval.controller;
import com.project.ard.dataretrieval.domain.vo.TifRetrievalRequest;
import com.project.ard.dataretrieval.domain.vo.TifRetrievalResponse;
import com.project.ard.dataretrieval.service.IRsTifFileService;
import com.project.ard.dataretrieval.domain.RsTifFile;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.common.annotation.Log;
import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import com.project.common.core.page.TableDataInfo;
import com.project.common.enums.BusinessType;
import com.project.common.utils.StringUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation. *;
import javax.validation.Valid;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
/**
 * TIF数据检索控制器
 * 
 * @author ard
 */
@Api(tags = "TIF数据检索")
@RestController
@RequestMapping("/ard/dataretrieval/tif")
@Validated
public class TifRetrievalController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TifRetrievalController.class);

    @Autowired
    private IRsTifFileService rsTifFileService;
    
    /**
     * 测试数据库连接和基本查询
     */
    @ApiOperation("测试数据库连接")
    @GetMapping("/test")
    public AjaxResult testDatabase() {
        try {
            // 查询所有记录
            List<RsTifFile> allFiles = rsTifFileService.list();
            logger.info("数据库测试 - 总记录数: {}", allFiles.size());
            
            // 查询前5条记录
            List<RsTifFile> sampleFiles = allFiles.stream().limit(5).collect(java.util.stream.Collectors.toList());
            
            for (RsTifFile file : sampleFiles) {
                logger.info("测试记录: ID={}, 文件名={}, 卫星ID={}, 传感器ID={}, 云量={}, 采集时间={}, 边界={}", 
                    file.getId(), file.getFilename(), file.getSatelliteId(), 
                    file.getSensorId(), file.getCloudPercent(), file.getAcquisitionTime(), file.getBoundary());
            }
            
            return success("数据库连接正常，总记录数: " + allFiles.size());
        } catch (Exception e) {
            logger.error("数据库测试失败", e);
            return error("数据库连接失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试特定条件的查询
     */
    @ApiOperation("测试特定条件查询")
    @PostMapping("/test-query")
    public AjaxResult testQuery(@RequestBody java.util.Map<String, Object> params) {
        try {
            QueryWrapper<RsTifFile> queryWrapper = new QueryWrapper<>();
            
            // 测试卫星筛选
            if (params.containsKey("satelliteId")) {
                String satelliteId = (String) params.get("satelliteId");
                queryWrapper.eq("satellite_id", satelliteId);
                logger.info("测试卫星筛选: {}", satelliteId);
            }
            
            // 测试传感器筛选
            if (params.containsKey("sensorId")) {
                String sensorId = (String) params.get("sensorId");
                queryWrapper.eq("sensor_id", sensorId);
                logger.info("测试传感器筛选: {}", sensorId);
            }
            
            // 测试云量筛选
            if (params.containsKey("cloudMin")) {
                Double cloudMin = Double.parseDouble(params.get("cloudMin").toString());
                queryWrapper.ge("cloud_percent", cloudMin);
                logger.info("测试云量最小值筛选: {}", cloudMin);
            }
            
            List<RsTifFile> results = rsTifFileService.list(queryWrapper);
            logger.info("测试查询结果数量: {}", results.size());
            
            return success("测试查询完成，结果数量: " + results.size());
        } catch (Exception e) {
            logger.error("测试查询失败", e);
            return error("测试查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试空间查询
     */
    @ApiOperation("测试空间查询")
    @PostMapping("/test-spatial")
    public AjaxResult testSpatialQuery(@RequestBody java.util.Map<String, Object> params) {
        try {
            String geoJson = (String) params.get("geoJson");
            String queryType = (String) params.getOrDefault("type", "intersects");
            
            if (geoJson == null || geoJson.trim().isEmpty()) {
                return error("GeoJSON参数不能为空");
            }
            
            logger.info("测试空间查询 - 类型: {}, GeoJSON: {}", queryType, geoJson);
            
            List<RsTifFile> results;
            switch (queryType.toLowerCase()) {
                case "intersects":
                    results = rsTifFileService.selectBySpatialIntersection(geoJson);
                    break;
                case "contains":
                    results = rsTifFileService.selectBySpatialContains(geoJson);
                    break;
                case "within":
                    results = rsTifFileService.selectBySpatialWithin(geoJson);
                    break;
                default:
                    return error("不支持的查询类型: " + queryType);
            }
            
            logger.info("空间查询结果数量: {}", results.size());
            
            return success("空间查询完成，结果数量: " + results.size());
        } catch (Exception e) {
            logger.error("空间查询失败", e);
            return error("空间查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 检索TIF数据
     */
    @ApiOperation("检索TIF数据")
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:tif:search')")
    @Log(title = "TIF数据检索", businessType = BusinessType.OTHER)
    @PostMapping("/search")
    public TableDataInfo searchTifData(@Valid @RequestBody TifRetrievalRequest request) {
        // 打印请求参数用于调试
        logger.info("=== TIF数据检索请求 ===");
        logger.info("请求参数: {}", request);
        
        startPage();
        
        // 构建查询条件
        QueryWrapper<RsTifFile> queryWrapper = new QueryWrapper<>();
        
        // 1. 根据卫星信息筛选
        if (request.getDataType() != null && request.getDataType().getSatellites() != null) {
            List<String> satelliteIds = new java.util.ArrayList<>();
            List<String> sensorIds = new java.util.ArrayList<>();
            
            logger.info("处理卫星筛选条件，卫星数量: {}", request.getDataType().getSatellites().size());
            
            for (TifRetrievalRequest.Satellite satellite : request.getDataType().getSatellites()) {
                // 优先使用satellite字段，如果没有则使用code字段
                String satelliteId = satellite.getSatellite() != null ? satellite.getSatellite() : satellite.getCode();
                if (satelliteId != null) {
                    satelliteIds.add(satelliteId);
                    logger.info("添加卫星ID: {}", satelliteId);
                }
                
                // 收集传感器信息
                if (satellite.getSensors() != null) {
                    sensorIds.addAll(satellite.getSensors());
                    logger.info("添加传感器: {}", satellite.getSensors());
                }
            }
            
            if (!satelliteIds.isEmpty()) {
                queryWrapper.in("satellite_id", satelliteIds);
                logger.info("应用卫星筛选条件: {}", satelliteIds);
            } else {
                logger.info("没有有效的卫星ID，跳过卫星筛选");
            }
            
            if (!sensorIds.isEmpty()) {
                queryWrapper.in("sensor_id", sensorIds);
                logger.info("应用传感器筛选条件: {}", sensorIds);
            } else {
                logger.info("没有有效的传感器ID，跳过传感器筛选");
            }
        } else {
            logger.info("没有卫星筛选条件");
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
        } else {
            logger.info("没有云量筛选条件");
        }
        
        // 3. 根据采集时间范围筛选
        if (request.getTimeRange() != null && request.getTimeRange().getDateRange() != null 
            && request.getTimeRange().getDateRange().size() >= 2) {
           String startStr = request.getTimeRange().getDateRange().get(0);
String endStr = request.getTimeRange().getDateRange().get(1);

if (StringUtils.isNotEmpty(startStr)) {
    String s = startStr.trim().replace('/', '-').replace('.', '-'); // 标准化分隔符
    LocalDate d = LocalDate.parse(s); // 默认按 yyyy-MM-dd 解析
    LocalDateTime startDt = d.atStartOfDay();
    queryWrapper.ge("acquisition_time", startDt); // 传 LocalDateTime，不传字符串
}

if (StringUtils.isNotEmpty(endStr)) {
    String s = endStr.trim().replace('/', '-').replace('.', '-');
    LocalDate d = LocalDate.parse(s);
    LocalDateTime endDt = d.atTime(LocalTime.MAX); // 23:59:59.999999999
    queryWrapper.le("acquisition_time", endDt);
}
        } else {
            logger.info("没有时间范围筛选条件");
        }
        
        // 4. 根据空间边界筛选
        if (request.getBoundary() != null) {
            String geoJson = request.getBoundary().getFullGeoJson();
            if (geoJson != null && !geoJson.trim().isEmpty()) {
                logger.info("处理空间边界筛选条件: {}", geoJson);
                
                try {
                    // 使用空间相交查询
                    List<RsTifFile> spatialResults = rsTifFileService.selectBySpatialIntersection(geoJson);
                    logger.info("空间查询结果数量: {}", spatialResults.size());
                    
                    // 如果已经有其他筛选条件，需要进一步过滤
                    if (queryWrapper.getExpression().getNormal().size() > 0) {
                        // 有其他筛选条件，需要组合查询
                        List<Integer> spatialIds = spatialResults.stream()
                                .map(RsTifFile::getId)
                                .collect(java.util.stream.Collectors.toList());
                        
                        if (!spatialIds.isEmpty()) {
                            queryWrapper.in("id", spatialIds);
                            logger.info("应用空间筛选条件，匹配的ID数量: {}", spatialIds.size());
                        } else {
                            logger.info("空间查询无结果，返回空列表");
                            return getDataTable(new java.util.ArrayList<>());
                        }
                    } else {
                        // 只有空间查询条件，直接返回结果
                        logger.info("只有空间查询条件，直接返回结果");
                        return getDataTable(spatialResults);
                    }
                } catch (Exception e) {
                    logger.error("空间查询失败: {}", e.getMessage(), e);
                    logger.warn("空间查询失败，跳过空间筛选条件");
                }
            } else {
                logger.info("没有有效的空间边界条件");
            }
        } else {
            logger.info("没有空间边界筛选条件");
        }
        
        // 5. 按采集时间倒序排列
        queryWrapper.orderByDesc("acquisition_time");
        
        // 打印最终的查询条件
        logger.info("最终查询条件: {}", queryWrapper.getTargetSql());
        logger.info("查询条件详情: {}", queryWrapper);
        
        // 先查询总数（不分页）
        List<RsTifFile> allList = rsTifFileService.list(queryWrapper);
        logger.info("筛选后总记录数: {}", allList.size());
        
        // 执行分页查询
        List<RsTifFile> list = rsTifFileService.list(queryWrapper);
        
        // 打印查询结果详情
        logger.info("=== 查询结果详情 ===");
        logger.info("分页查询结果数量: {}", list.size());
        
        for (int i = 0; i < Math.min(list.size(), 3); i++) { // 只打印前3条记录
            RsTifFile file = list.get(i);
            logger.info("记录 {}: ID={}, 文件名={}, 卫星ID={}, 传感器ID={}, 云量={}, 采集时间={}, 边界={}", 
                i + 1, file.getId(), file.getFilename(), file.getSatelliteId(), 
                file.getSensorId(), file.getCloudPercent(), file.getAcquisitionTime(), file.getBoundary());
        }
        
        if (list.size() > 3) {
            logger.info("... 还有 {} 条记录", list.size() - 3);
        }
        
        return getDataTable(list);
    }


    /**
     * 获取数据详情
     */

    /**
     * 下载数据
     */

    
    /**
     * 获取预览图
     */
//    @ApiOperation("获取预览图")
//    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:tif:preview')")
//    @Log(title = "TIF数据预览", businessType = BusinessType.OTHER)
//    @GetMapping("/preview/{dataId}")
//    public AjaxResult getPreviewImage(@ApiParam("数据ID") @PathVariable String dataId) {
//        if (StringUtils.isEmpty(dataId)) {
//            return error("数据ID不能为空");
//        }
//        String previewUrl = tifRetrievalService.getPreviewImage(dataId);
//        return success(previewUrl);
//    }

    /**
     * 批量下载数据
     */
    @ApiOperation("批量下载数据")
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:tif:batchDownload')")
    @Log(title = "TIF数据批量下载", businessType = BusinessType.EXPORT)
    @PostMapping("/batchDownload")
    public AjaxResult batchDownloadData(@ApiParam("数据ID列表") @RequestBody List<String> dataIds) {
        if (dataIds == null || dataIds.isEmpty()) {
            return error("数据ID列表不能为空");
        }
        // TODO: 实现批量下载逻辑
        return success("批量下载任务已提交，请稍后查看下载列表");
    }
    
        /**
     * 获取可用的卫星列表
     */
    @ApiOperation("获取可用的卫星列表")
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:tif:satellites')")
    @GetMapping("/satellites")
    public AjaxResult getAvailableSatellites() {
        // TODO: 实现获取可用卫星列表的逻辑
        return success("获取卫星列表成功");
    }

    /**
     * 获取数据类型列表
     */
    @ApiOperation("获取数据类型列表")
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:tif:dataTypes')")
    @GetMapping("/dataTypes")
    public AjaxResult getDataTypes() {
        // TODO: 实现获取数据类型列表的逻辑
        return success("获取数据类型列表成功");
    }
}
