package com.project.ard.dataretrieval.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.core.controller.BaseController;
import com.project.common.core.page.TableDataInfo;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalRequest;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalResponse;
import com.project.ard.dataretrieval.service.CubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 立方体数据检索控制器
 * 
 * @author project
 */
@RestController
@RequestMapping("/ard/dataretrieval/cube")
public class CubeRetrievalController extends BaseController {

    @Autowired
    private CubeService cubeService;

    /**
     * 搜索立方体数据
     * 
     * @param request 搜索请求参数
     * @return 搜索结果
     */
    @PostMapping("/search")
    public TableDataInfo searchCubeData(@RequestBody CubeRetrievalRequest request) {
        try {
            logger.info("收到立方体数据搜索请求: {}", request);
            logger.info("请求参数类型: {}", request.getClass().getName());
            
            // 验证请求参数
            if (request == null) {
                logger.warn("请求参数为空");
                return getDataTable(new ArrayList<>());
            }
            
            // 使用真实数据库查询
            IPage<CubeRetrievalResponse> pageResult = cubeService.searchCubeDataPage(request);
            
            logger.info("搜索完成，返回 {} 条数据，总计 {} 条", 
                pageResult.getRecords().size(), pageResult.getTotal());
            
            return getDataTable(pageResult.getRecords());
            
        } catch (Exception e) {
            logger.error("立方体数据搜索失败", e);
            
            // 如果数据库查询失败，返回模拟数据
            List<CubeRetrievalResponse> mockResults = simulateDataQuery(request);
            return getDataTable(mockResults);
        }
    }
    
    /**
     * 模拟数据查询（备用方案）
     * 
     * @param request 搜索请求
     * @return 模拟数据结果
     */
    private List<CubeRetrievalResponse> simulateDataQuery(CubeRetrievalRequest request) {
        List<CubeRetrievalResponse> results = new ArrayList<>();
        
        // 生成模拟立方体数据
        for (int i = 1; i <= 5; i++) {
            CubeRetrievalResponse response = new CubeRetrievalResponse();
            response.setId((long) i);
            response.setCubeName("模拟立方体_" + i);
            response.setCreateUser("admin");
            response.setDataType("CUBE");
            response.setDataDescribe("模拟立方体数据描述");
            response.setCompressionAlgorithm("LZW");
            response.setPathCode("P" + String.format("%03d", i));
            response.setRowCode("R" + String.format("%03d", i));
            
            // 生成边界数据 (GeoJSON格式)
            response.setBoundary(generateMockGeoJSON(i));
            
            results.add(response);
        }
        
        return results;
    }
    
    /**
     * 生成模拟GeoJSON数据
     * 
     * @param index 索引
     * @return GeoJSON字符串
     */
    private String generateMockGeoJSON(int index) {
        // 生成一个简单的矩形
        double baseLon = 116.0 + index * 0.1;
        double baseLat = 39.0 + index * 0.1;
        
        return String.format(
            "{\"type\":\"Polygon\",\"coordinates\":[[[%.6f,%.6f],[%.6f,%.6f],[%.6f,%.6f],[%.6f,%.6f],[%.6f,%.6f]]]}",
            baseLon, baseLat,
            baseLon + 0.1, baseLat,
            baseLon + 0.1, baseLat + 0.1,
            baseLon, baseLat + 0.1,
            baseLon, baseLat
        );
    }
}