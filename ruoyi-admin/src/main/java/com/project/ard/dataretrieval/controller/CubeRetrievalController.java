package com.project.ard.dataretrieval.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.core.controller.BaseController;
import com.project.common.core.page.TableDataInfo;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalRequest;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalResponse;
import com.project.ard.dataretrieval.domain.vo.CubeDetailResponse;
import com.project.ard.dataretrieval.domain.vo.CubeSliceResponse;
import com.project.ard.dataretrieval.service.CubeService;
import com.project.ard.dataretrieval.service.CubeSliceService;
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
    
    @Autowired
    private CubeSliceService cubeSliceService;

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

            // 创建TableDataInfo并正确设置total
            TableDataInfo tableDataInfo = new TableDataInfo();
            tableDataInfo.setCode(200); // HttpStatus.SUCCESS
            tableDataInfo.setMsg("查询成功");
            tableDataInfo.setRows(pageResult.getRecords());
            tableDataInfo.setTotal(pageResult.getTotal()); // 使用分页查询的真实total
            
            return tableDataInfo;

        } catch (Exception e) {
            logger.error("立方体数据搜索失败", e);
            // 异常时返回空列表，不返回模拟数据
            return getDataTable(new ArrayList<>());
            // 若需要明确提示错误，可自定义返回结构（需结合TableDataInfo的设计）
            // 例如：return TableDataInfo.error("数据搜索失败，请稍后重试");
        }
    }
    
    
    
    /**
     * 获取立方体详情，包含切片数据
     * 
     * @param cubeId 立方体ID
     * @return 立方体详情
     */
    @GetMapping("/detail/{cubeId}")
    public CubeDetailResponse getCubeDetail(@PathVariable String cubeId) {
        try {
            logger.info("收到立方体详情查询请求，立方体ID: {}", cubeId);
            
            CubeDetailResponse detail = cubeService.getCubeDetail(cubeId);
            if (detail == null) {
                logger.warn("未找到立方体详情，立方体ID: {}", cubeId);
                return null;
            }
            
            logger.info("立方体详情查询成功，包含 {} 条切片数据", 
                    detail.getSlices() != null ? detail.getSlices().size() : 0);
            return detail;
            
        } catch (Exception e) {
            logger.error("查询立方体详情失败，立方体ID: {}", cubeId, e);
            throw new RuntimeException("查询立方体详情失败", e);
        }
    }
    
    /**
     * 获取切片详情
     * 
     * @param sliceId 切片ID
     * @return 切片详情
     */
    @GetMapping("/slice/{sliceId}")
    public CubeSliceResponse getSliceDetail(@PathVariable Integer sliceId) {
        try {
            logger.info("收到切片详情查询请求，切片ID: {}", sliceId);
            
            CubeSliceResponse slice = cubeSliceService.getSliceById(sliceId);
            if (slice == null) {
                logger.warn("未找到切片详情，切片ID: {}", sliceId);
                return null;
            }
            
            logger.info("切片详情查询成功，切片ID: {}", sliceId);
            return slice;
            
        } catch (Exception e) {
            logger.error("查询切片详情失败，切片ID: {}", sliceId, e);
            throw new RuntimeException("查询切片详情失败", e);
        }
    }
    
    /**
     * 根据立方体ID获取所有切片信息
     * 
     * @param cubeId 立方体ID
     * @return 切片列表
     */
    @GetMapping("/slices/{cubeId}")
    public List<CubeSliceResponse> getCubeSlices(@PathVariable String cubeId) {
        try {
            logger.info("收到立方体切片查询请求，立方体ID: {}", cubeId);
            
            // 立方体ID是字符串格式，直接使用
            List<CubeSliceResponse> slices = cubeSliceService.getSlicesByCubeId(cubeId);
            logger.info("立方体切片查询成功，立方体ID: {}, 切片数量: {}", cubeId, slices.size());
            
            return slices;
            
        } catch (Exception e) {
            logger.error("查询立方体切片失败，立方体ID: {}", cubeId, e);
            throw new RuntimeException("查询立方体切片失败", e);
        }
    }
}