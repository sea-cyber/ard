package com.project.ard.dataretrieval.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.core.controller.BaseController;
import com.project.common.core.page.TableDataInfo;
import com.project.common.core.domain.AjaxResult;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalRequest;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalResponse;
import com.project.ard.dataretrieval.domain.vo.CubeDetailResponse;
import com.project.ard.dataretrieval.domain.vo.CubeSliceResponse;
import com.project.ard.dataretrieval.service.CubeService;
import com.project.ard.dataretrieval.service.CubeSliceService;
import com.project.ard.dataretrieval.service.CubeResultSliceInfoService;
import com.project.ard.dataretrieval.domain.CubeResultSliceInfo;
import com.project.common.utils.SecurityUtils;
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
    
    @Autowired
    private CubeResultSliceInfoService cubeResultSliceInfoService;

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
    public AjaxResult getCubeDetail(@PathVariable String cubeId) {
        try {
            logger.info("收到立方体详情查询请求，立方体ID: {}", cubeId);
            
            CubeDetailResponse detail = cubeService.getCubeDetail(cubeId);
            if (detail == null) {
                logger.warn("未找到立方体详情，立方体ID: {}", cubeId);
                return AjaxResult.error("未找到立方体详情，立方体ID: " + cubeId);
            }
            
            logger.info("立方体详情查询成功，包含 {} 条切片数据，边界信息: {}", 
                    detail.getSlices() != null ? detail.getSlices().size() : 0,
                    detail.getBoundary() != null ? "存在" : "不存在");
            return AjaxResult.success(detail);
            
        } catch (Exception e) {
            logger.error("查询立方体详情失败，立方体ID: {}", cubeId, e);
            return AjaxResult.error("查询立方体详情失败: " + e.getMessage());
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
    
    /**
     * 根据立方体ID获取切片信息（来自cube_slice_info表）
     * 
     * @param cubeId 立方体ID
     * @return 切片信息
     */
    @GetMapping("/slice-info/{cubeId}")
    public CubeSliceResponse getCubeSliceInfo(@PathVariable String cubeId) {
        try {
            logger.info("收到立方体切片信息查询请求，立方体ID: {}", cubeId);
            
            // 获取第一个切片作为代表
            List<CubeSliceResponse> slices = cubeSliceService.getSlicesByCubeId(cubeId);
            if (slices != null && !slices.isEmpty()) {
                logger.info("立方体切片信息查询成功，立方体ID: {}", cubeId);
                return slices.get(0); // 返回第一个切片作为代表
            } else {
                logger.warn("未找到立方体切片信息，立方体ID: {}", cubeId);
                return null;
            }
            
        } catch (Exception e) {
            logger.error("查询立方体切片信息失败，立方体ID: {}", cubeId, e);
            throw new RuntimeException("查询立方体切片信息失败", e);
        }
    }
    
    /**
     * 根据立方体ID获取当前用户的结果切片信息（来自cube_result_slice_info表）
     * 
     * @param cubeId 立方体ID
     * @return 结果切片信息列表
     */
    @GetMapping("/result-slice-info/{cubeId}")
    public List<CubeResultSliceInfo> getUserResultSliceInfo(@PathVariable String cubeId) {
        try {
            // 获取当前用户ID
            Long userId = SecurityUtils.getUserId();
            logger.info("收到用户结果切片信息查询请求，用户ID: {}, 立方体ID: {}", userId, cubeId);
            
            List<CubeResultSliceInfo> resultSlices = cubeResultSliceInfoService.getResultSliceInfoByUserIdAndCubeId(userId, cubeId);
            logger.info("用户结果切片信息查询成功，用户ID: {}, 立方体ID: {}, 结果数量: {}", userId, cubeId, resultSlices.size());
            
            return resultSlices;
            
        } catch (Exception e) {
            logger.error("查询用户结果切片信息失败，立方体ID: {}", cubeId, e);
            throw new RuntimeException("查询用户结果切片信息失败", e);
        }
    }
}