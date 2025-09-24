package com.project.ard.dataretrieval.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.ard.dataretrieval.domain.vo.TifRetrievalRequest;
import com.project.ard.dataretrieval.service.IRsTifFileService;
import com.project.ard.dataretrieval.service.ITifRetrievalService;
import com.project.ard.dataretrieval.domain.RsTifFile;
import com.project.common.annotation.Log;
import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import com.project.common.core.page.TableDataInfo;
import com.project.common.enums.BusinessType;
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
    
    @Autowired
    private ITifRetrievalService tifRetrievalService;
    
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
        
        // 调用service层进行查询
        return tifRetrievalService.searchTifData(request);
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
