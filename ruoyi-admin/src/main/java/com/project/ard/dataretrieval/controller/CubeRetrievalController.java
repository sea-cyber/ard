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
import com.project.ard.dataretrieval.config.VizConfig;
import com.project.common.utils.SecurityUtils;
import com.project.common.annotation.Anonymous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    
    @Autowired
    private VizConfig vizConfig;

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
            
            // 详细记录请求参数
            if (request != null) {
                logger.info("===== 立方体搜索请求参数详情 =====");
                logger.info("cubeName: {}", request.getCubeName());
                logger.info("region: {}", request.getRegion());
                logger.info("timeRange: {}", request.getTimeRange());
                logger.info("boundary: {}", request.getBoundary());
                logger.info("page: {}", request.getPage());
                logger.info("===================================");
            }

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
    
    /**
     * 获取原始数据切片的预览图URL
     * 保持原始路径格式，不做路径转换
     * 
     * @param sliceId 切片ID
     * @return 预览图URL
     */
    @GetMapping("/slice/browse/{sliceId}")
    public AjaxResult getSliceBrowseImage(@PathVariable Integer sliceId) {
        try {
            logger.info("收到切片预览图请求，切片ID: {}", sliceId);
            
            // 获取切片信息
            CubeSliceResponse slice = cubeSliceService.getSliceById(sliceId);
            if (slice == null) {
                logger.warn("未找到切片信息，切片ID: {}", sliceId);
                return AjaxResult.error("未找到切片信息，切片ID: " + sliceId);
            }
            
            // 检查是否有浏览图路径
            if (slice.getBrowseImagePath() == null || slice.getBrowseImagePath().isEmpty()) {
                logger.warn("切片无浏览图路径，切片ID: {}", sliceId);
                return AjaxResult.error("该切片暂无预览图");
            }
            
            // 直接使用原始路径，不做任何转换
            // 保持原有格式：/default_user/ARD_CUB_GRIDT0_default_user_VIZ/...
            String originalPath = slice.getBrowseImagePath();
            logger.info("使用原始预览图路径 - 切片ID: {}, cubeId: {}, browseImagePath: {}", 
                    sliceId, slice.getCubeId(), originalPath);
            
            // 构建访问URL（使用common/view/resource接口）
            String baseUrl = "/dev-api/common/view/resource";
            String browseImageUrl = baseUrl + "?resource=" + java.net.URLEncoder.encode(originalPath, "UTF-8");
            
            Map<String, Object> result = new HashMap<>();
            result.put("browseImageUrl", browseImageUrl);
            result.put("browseImagePath", originalPath);
            result.put("sliceId", sliceId);
            result.put("cubeId", slice.getCubeId());
            
            logger.info("预览图URL构建成功，切片ID: {}, URL: {}", sliceId, browseImageUrl);
            return AjaxResult.success(result);
            
        } catch (Exception e) {
            logger.error("获取切片预览图失败，切片ID: {}", sliceId, e);
            return AjaxResult.error("获取切片预览图失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取预览图文件
     * 从 ard.viz.root-path 根目录读取文件
     * 
     * @param imagePath 预览图相对路径（例如：GRID_CUBE_T0_J49E016017/ndvi_result_1761808377681.jpg）
     * @param response HTTP响应
     */
    @Anonymous
    @GetMapping("/viz")
    public void getVizImage(@RequestParam("imagePath") String imagePath, HttpServletResponse response) {
        try {
            logger.info("收到预览图请求，原始imagePath参数: {}", imagePath);
            
            if (imagePath == null || imagePath.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                logger.warn("预览图路径不能为空");
                return;
            }
            
            // URL解码路径参数（处理 %2F 等编码字符）
            String decodedPath = java.net.URLDecoder.decode(imagePath, "UTF-8");
            logger.info("URL解码后的路径: {}", decodedPath);
            
            // 获取预览图根目录
            String vizRootPath = vizConfig.getRootPath();
            logger.info("预览图根目录: {}", vizRootPath);
            
            // 处理路径，统一路径分隔符，去掉开头的斜杠
            String cleanPath = decodedPath.replace('\\', '/');
            if (cleanPath.startsWith("/")) {
                cleanPath = cleanPath.substring(1);
            }
            logger.info("清理后的路径: {}", cleanPath);
            
            // 构建完整文件路径
            String fullPath = vizRootPath.replace('\\', '/') + "/" + cleanPath;
            logger.info("完整文件路径: {}", fullPath);
            
            // 检查文件是否存在
            File file = new File(fullPath);
            if (!file.exists() || !file.isFile()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                logger.warn("预览图文件不存在: {}", fullPath);
                return;
            }
            
            // 设置响应头 - 根据文件扩展名设置Content-Type
            response.setContentType("image/jpeg"); // 默认jpeg
            String lowerPath = cleanPath.toLowerCase();
            if (lowerPath.endsWith(".png")) {
                response.setContentType("image/png");
            } else if (lowerPath.endsWith(".gif")) {
                response.setContentType("image/gif");
            } else if (lowerPath.endsWith(".bmp")) {
                response.setContentType("image/bmp");
            } else if (lowerPath.endsWith(".webp")) {
                response.setContentType("image/webp");
            } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
                response.setContentType("image/jpeg");
            }
            
            response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
            response.setHeader("Cache-Control", "public, max-age=604800");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET,OPTIONS");
            response.setContentLengthLong(file.length());
            
            // 输出文件内容
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
            
            logger.info("预览图文件成功返回: {}", fullPath);
            
        } catch (Exception e) {
            logger.error("获取预览图失败，路径: {}", imagePath, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 导出立方体数据
     * 生成包含模拟文件的ZIP压缩包
     * 
     * @param request 导出请求参数，包含格式、立方体ID列表等
     * @param response HTTP响应
     */
    @PostMapping("/export")
    public void exportCubeData(@RequestBody Map<String, Object> request, HttpServletResponse response) {
        try {
            logger.info("收到立方体数据导出请求: {}", request);
            
            // 获取导出参数
            String format = (String) request.get("format"); // "hdf5" 或 "cub"
            String imageFormat = (String) request.get("imageFormat"); // "tif" 或 "cog"（仅当format为cub时有效）
            @SuppressWarnings("unchecked")
            List<String> selectedResults = (List<String>) request.get("selectedResults"); // 立方体ID列表
            
            if (selectedResults == null || selectedResults.isEmpty()) {
                logger.warn("导出请求中立方体ID列表为空");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            logger.info("导出参数 - 格式: {}, 内部影像格式: {}, 立方体数量: {}", 
                    format, imageFormat, selectedResults.size());
            
            // 确定文件扩展名
            String fileExtension = "hdf5".equalsIgnoreCase(format) ? "hdf5" : "cub";
            
            // 创建ZIP压缩包
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                // 为每个选中的立方体创建一个模拟文件
                for (String cubeId : selectedResults) {
                    // 创建ZIP条目
                    String fileName = cubeId + "." + fileExtension;
                    ZipEntry entry = new ZipEntry(fileName);
                    zos.putNextEntry(entry);
                    
                    // 生成模拟文件内容（这里只是示例数据）
                    // 实际应用中，这里应该读取真实的文件内容
                    String mockContent = "模拟立方体数据文件 - " + cubeId + "\n";
                    mockContent += "格式: " + format.toUpperCase() + "\n";
                    if ("cub".equalsIgnoreCase(format) && imageFormat != null) {
                        mockContent += "内部影像格式: " + imageFormat.toUpperCase() + "\n";
                    }
                    mockContent += "生成时间: " + new java.util.Date() + "\n";
                    mockContent += "这是一个模拟的" + fileExtension.toUpperCase() + "格式文件\n";
                    mockContent += "实际使用时，这里应该是真实的立方体数据内容\n";
                    
                    // 写入模拟内容
                    zos.write(mockContent.getBytes("UTF-8"));
                    zos.closeEntry();
                    
                    logger.info("已添加文件到ZIP: {}", fileName);
                }
            }
            
            // 设置响应头
            String zipFileName = "cube_export_" + System.currentTimeMillis() + ".zip";
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");
            response.setHeader("Content-Length", String.valueOf(baos.size()));
            
            // 输出ZIP文件
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();
            
            logger.info("立方体数据导出成功 - 格式: {}, 文件数量: {}, ZIP文件名: {}", 
                    format, selectedResults.size(), zipFileName);
            
        } catch (Exception e) {
            logger.error("导出立方体数据失败", e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("导出失败: " + e.getMessage());
            } catch (IOException ioException) {
                logger.error("写入错误响应失败", ioException);
            }
        }
    }
}