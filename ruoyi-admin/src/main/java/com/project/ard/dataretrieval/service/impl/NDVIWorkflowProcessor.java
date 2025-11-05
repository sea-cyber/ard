package com.project.ard.dataretrieval.service.impl;

import com.project.ard.dataretrieval.annotation.WorkflowType;
import com.project.ard.dataretrieval.entity.CubeWorkflow;
import com.project.ard.dataretrieval.service.SliceFileReaderService;
import com.project.ard.dataretrieval.service.WorkflowProcessor;
import com.project.ard.dataretrieval.config.UserDataConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import javax.imageio.ImageIO;
import com.project.ard.dataretrieval.mapper.CubeMapper;
import com.project.ard.dataretrieval.domain.Cube;

/**
 * NDVI植被指数工作流处理器
 */
@Component
@WorkflowType(value = "flow_0001", description = "NDVI植被指数分析")
public class NDVIWorkflowProcessor implements WorkflowProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(NDVIWorkflowProcessor.class);
    
    @Autowired
    private SliceFileReaderService sliceFileReaderService;
    
    @Autowired
    private UserDataConfig userDataConfig;
    
    @Autowired
    private com.project.ard.dataretrieval.service.CubeTaskStepService cubeTaskStepService;
    
    @Autowired
    private CubeMapper cubeMapper;
    
    @Value("${ard.cube.root-path}")
    private String cubeRootPath;
    
    @Override
    public String getSupportedWorkflowType() {
        return getClass().getAnnotation(WorkflowType.class).value();
    }
    
    @Override
    public Map<String, Object> processWorkflow(CubeWorkflow workflow, Map<String, Object> parameters, List<String> sliceFiles) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取任务ID
            String taskId = (String) parameters.get("taskId");
            
            // 开始算法初始化步骤
            if (taskId != null) {
                cubeTaskStepService.startStep(taskId, 3, "开始NDVI植被指数算法初始化，工作流: " + workflow.getWorkflowName());
            }
            
            result.put("status", "success");
            result.put("message", "NDVI植被指数计算完成");
            result.put("workflowType", getSupportedWorkflowType());
            result.put("workflowName", workflow.getWorkflowName());
            
            // 获取切片文件路径列表
            @SuppressWarnings("unchecked")
            List<String> sliceFilePaths = (List<String>) parameters.get("sliceFiles");
            
            if (sliceFilePaths == null || sliceFilePaths.isEmpty()) {
                result.put("status", "error");
                result.put("message", "缺少切片文件路径参数");
                return result;
            }
            
            logger.info("开始NDVI植被指数计算 - 工作流: {}, 切片数量: {}", 
                       workflow.getWorkflowName(), sliceFilePaths.size());
            
            int successfulFiles = 0;
            List<String> processedFiles = new java.util.ArrayList<>();
            List<String> outputPaths = new java.util.ArrayList<>();
            // 保存输出路径到原始切片信息的映射，确保保存数据库时使用正确的cubeId
            Map<String, String> outputPathToCubeId = new java.util.HashMap<>();
            Map<String, String> outputPathToQuarter = new java.util.HashMap<>();
            // 保存输出路径到预览图路径的映射
            Map<String, String> outputPathToBrowseImagePath = new java.util.HashMap<>();
            
            // 开始结果输出步骤（只执行一次）
            if (taskId != null) {
                cubeTaskStepService.startStep(taskId, 4, "开始保存NDVI结果文件");
            }
            
            // 处理每个切片文件
            for (String sliceFile : sliceFilePaths) {
                try {
                    logger.info("处理切片文件: {}", sliceFile);
                    
                    // 解析切片标识符格式: sliceFileName|cubeId|quarter
                    String[] parts = sliceFile.split("\\|");
                    if (parts.length != 3) {
                        logger.warn("无效的切片标识符格式: {}", sliceFile);
                        continue;
                    }
                    
                    String sliceFileName = parts[0];
                    String cubeId = parts[1];  // 从切片标识符中获取cubeId（这是原始切片的cubeId）
                    String quarter = parts[2];
                    
                    logger.info("解析切片信息 - 标识符: {}, 立方体: {}, 季度: {}", sliceFileName, cubeId, quarter);
                    
                    String gridId = null;
                    Cube cube = cubeMapper.selectById(cubeId);
                    if (cube != null) {
                        gridId = cube.getGridId();
                    }
                    if (gridId == null) gridId = cubeId; // fallback警告
                    
                    if (cubeId != null && quarter != null) {
                        // 构建波段文件路径 - 使用配置的原始数据路径：ARD_CUB_GRIDT0_OFF_RAW/grid_id/quarter
                        // 使用 Paths 来正确处理路径，自动适配不同操作系统的路径分隔符
                        java.nio.file.Path basePath = java.nio.file.Paths.get(cubeRootPath);
                        java.nio.file.Path redBandPath = basePath.resolve(cubeId).resolve(quarter)
                                .resolve(cubeId + "_" + quarter + "_B4.TIF");
                        java.nio.file.Path nirBandPath = basePath.resolve(cubeId).resolve(quarter)
                                .resolve(cubeId + "_" + quarter + "_B5.TIF");
                        String redBandFile = redBandPath.toString();
                        String nirBandFile = nirBandPath.toString();
                        
                        logger.info("红光波段文件: {}", redBandFile);
                        logger.info("近红外波段文件: {}", nirBandFile);
                        
                        // 检查文件是否存在
                        File redFile = new File(redBandFile);
                        File nirFile = new File(nirBandFile);
                        
                        if (!redFile.exists()) {
                            logger.warn("红光波段文件不存在: {}", redBandFile);
                            continue;
                        }
                        
                        if (!nirFile.exists()) {
                            logger.warn("近红外波段文件不存在: {}", nirBandFile);
                            continue;
                        }
                        
                        // 计算NDVI（使用原始切片的cubeId）
                        Map<String, Object> ndviResult = calculateNDVIForSlice(redBandFile, nirBandFile, cubeId, quarter, taskId);
                        
                        if ("success".equals(ndviResult.get("status"))) {
                            successfulFiles++;
                            processedFiles.add(sliceFile);
                            
                            // 收集输出路径，并保存对应的原始切片信息
                            String outputPath = (String) ndviResult.get("outputPath");
                            if (outputPath != null) {
                                outputPaths.add(outputPath);
                                
                                // 标准化输出路径（统一路径分隔符），确保后续匹配时能正确找到
                                String normalizedPath = outputPath.replace("\\", "/");
                                
                                // 保存输出路径到原始切片cubeId和quarter的映射
                                // 同时保存原始路径和标准化路径，确保后续能匹配到
                                // 这样在保存数据库时，可以确保使用正确的cubeId
                                outputPathToCubeId.put(outputPath, cubeId);
                                outputPathToCubeId.put(normalizedPath, cubeId);  // 也保存标准化版本
                                outputPathToQuarter.put(outputPath, quarter);
                                outputPathToQuarter.put(normalizedPath, quarter);  // 也保存标准化版本
                                
                                // 获取并保存预览图路径
                                String browseImagePath = (String) ndviResult.get("browseImagePath");
                                if (browseImagePath != null) {
                                    outputPathToBrowseImagePath.put(outputPath, browseImagePath);
                                    outputPathToBrowseImagePath.put(normalizedPath, browseImagePath);
                                    logger.info("保存预览图路径映射 - 输出路径: {}, 预览图路径: {}", outputPath, browseImagePath);
                                }
                                
                                logger.info("保存输出路径映射 - 输出路径: {}, 标准化路径: {}, 原始切片cubeId: {}, quarter: {}", 
                                          outputPath, normalizedPath, cubeId, quarter);
                            }
                            
                            logger.info("切片NDVI计算成功: {}", sliceFile);
                        } else {
                            logger.error("切片NDVI计算失败: {}, 错误: {}", sliceFile, ndviResult.get("message"));
                        }
                    }
                } catch (Exception e) {
                    logger.error("处理切片文件异常: {}, 错误: {}", sliceFile, e.getMessage(), e);
                }
            }
            
            // 设置结果
            result.put("successfulFiles", successfulFiles);
            result.put("processedFiles", processedFiles);
            result.put("totalFiles", sliceFilePaths.size());
            result.put("outputPaths", outputPaths);
            // 保存输出路径到原始切片信息的映射，供保存数据库时使用
            result.put("outputPathToCubeId", outputPathToCubeId);
            result.put("outputPathToQuarter", outputPathToQuarter);
            result.put("outputPathToBrowseImagePath", outputPathToBrowseImagePath);
            
            if (successfulFiles == 0) {
                result.put("status", "error");
                result.put("message", "没有成功处理任何切片文件");
                logger.error("没有成功处理任何切片文件");
                return result;
            }
            
            logger.info("NDVI计算完成 - 成功处理: {}/{} 个切片", successfulFiles, sliceFilePaths.size());
            
            // 完成算法初始化步骤
            if (taskId != null) {
                cubeTaskStepService.completeStep(taskId, 3, "算法初始化完成，处理了 " + successfulFiles + " 个切片");
            }
            
            // 完成结果输出步骤
            if (taskId != null) {
                cubeTaskStepService.completeStep(taskId, 4, "结果输出完成，成功处理了 " + successfulFiles + " 个切片");
            }
            
            // 添加计算结果信息，包含输出路径
            Map<String, Object> calculationResult = new java.util.HashMap<>();
            calculationResult.put("outputPath", outputPaths.isEmpty() ? null : outputPaths.get(0)); // 使用第一个输出路径
            calculationResult.put("outputPaths", outputPaths);
            calculationResult.put("successfulFiles", successfulFiles);
            // 重要：将映射关系也放入calculationResult，确保保存结果时能获取到
            calculationResult.put("outputPathToCubeId", outputPathToCubeId);
            calculationResult.put("outputPathToQuarter", outputPathToQuarter);
            calculationResult.put("outputPathToBrowseImagePath", outputPathToBrowseImagePath);
            result.put("calculationResult", calculationResult);
            
        } catch (Exception e) {
            logger.error("NDVI计算失败: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "NDVI计算失败: " + e.getMessage());
            
            // 失败算法初始化步骤
            String taskId = (String) parameters.get("taskId");
            if (taskId != null) {
                cubeTaskStepService.failStep(taskId, 3, "算法初始化失败: " + e.getMessage());
            }
        }
        
        return result;
    }
    
    /**
     * 处理单个切片文件
     */
    private Map<String, Object> processSliceFile(String sliceFile, Map<String, Object> parameters) {
        Map<String, Object> fileResult = new HashMap<>();
        
        try {
            logger.info("开始处理切片文件: {}", sliceFile);
            
            // 读取文件信息
            Map<String, Object> fileInfo = sliceFileReaderService.readSliceFileInfo(sliceFile);
            
            if (!"success".equals(fileInfo.get("status"))) {
                logger.error("读取文件失败: {} - {}", sliceFile, fileInfo.get("message"));
                fileResult.put("status", "error");
                fileResult.put("message", "读取文件失败: " + fileInfo.get("message"));
                return fileResult;
            }
            
            // 获取波段信息
            String redBand = (String) parameters.get("redBand");
            String nirBand = (String) parameters.get("nirBand");
            
            logger.info("使用波段 - 红波段: {}, 近红外波段: {}", redBand, nirBand);
            
            // 根据波段名称确定波段号
            int redBandNumber = getBandNumber(redBand, "B4"); // 红波段
            int nirBandNumber = getBandNumber(nirBand, "B8"); // 近红外波段
            
            logger.info("波段号映射 - 红波段号: {}, 近红外波段号: {}", redBandNumber, nirBandNumber);
            
            // 读取红波段数据
            logger.info("读取红波段数据 - 文件: {}, 波段号: {}", sliceFile, redBandNumber);
            Map<String, Object> redBandData = sliceFileReaderService.readBandData(sliceFile, redBandNumber);
            
            if (!"success".equals(redBandData.get("status"))) {
                logger.error("读取红波段数据失败: {} - {}", sliceFile, redBandData.get("message"));
                fileResult.put("status", "error");
                fileResult.put("message", "读取红波段数据失败: " + redBandData.get("message"));
                return fileResult;
            }
            
            // 读取近红外波段数据
            logger.info("读取近红外波段数据 - 文件: {}, 波段号: {}", sliceFile, nirBandNumber);
            Map<String, Object> nirBandData = sliceFileReaderService.readBandData(sliceFile, nirBandNumber);
            
            if (!"success".equals(nirBandData.get("status"))) {
                logger.error("读取近红外波段数据失败: {} - {}", sliceFile, nirBandData.get("message"));
                fileResult.put("status", "error");
                fileResult.put("message", "读取近红外波段数据失败: " + nirBandData.get("message"));
                return fileResult;
            }
            
            logger.info("波段数据读取成功 - 红波段数据长度: {}, 近红外波段数据长度: {}", 
                       ((int[]) redBandData.get("data")).length, 
                       ((int[]) nirBandData.get("data")).length);
            
            // 计算NDVI
            logger.info("开始计算NDVI - 文件: {}", sliceFile);
            Map<String, Object> ndviResult = calculateNDVI(redBandData, nirBandData);
            
            if ("success".equals(ndviResult.get("status"))) {
                logger.info("NDVI计算成功 - 文件: {}, 有效像素: {}, 平均值: {}", 
                           sliceFile, ndviResult.get("validPixels"), ndviResult.get("meanValue"));
                
                // 保存NDVI结果为TIFF文件
                String outputPath = saveNDVIToTiff(ndviResult, parameters, redBandData);
                
                if (outputPath != null) {
                    fileResult.put("status", "success");
                    fileResult.put("fileName", fileInfo.get("fileName"));
                    fileResult.put("filePath", sliceFile);
                    fileResult.put("ndviResult", ndviResult);
                    fileResult.put("outputPath", outputPath);
                    logger.info("NDVI结果已保存到: {}", outputPath);
                } else {
                    logger.error("保存NDVI文件失败 - 文件: {}", sliceFile);
                    fileResult.put("status", "error");
                    fileResult.put("message", "保存NDVI文件失败");
                }
            } else {
                logger.error("NDVI计算失败 - 文件: {}, 错误: {}", sliceFile, ndviResult.get("message"));
                fileResult.put("status", "error");
                fileResult.put("message", "NDVI计算失败: " + ndviResult.get("message"));
            }
            
        } catch (Exception e) {
            logger.error("处理文件异常 - 文件: {}, 错误: {}", sliceFile, e.getMessage(), e);
            fileResult.put("status", "error");
            fileResult.put("message", "处理文件失败: " + e.getMessage());
        }
        
        return fileResult;
    }
    
    /**
     * 使用GDAL计算NDVI
     * NDVI = (NIR - RED) / (NIR + RED)
     */
    private Map<String, Object> calculateNDVI(Map<String, Object> redBandData, Map<String, Object> nirBandData) {
        Map<String, Object> ndviResult = new HashMap<>();
        
        try {
            // 获取波段数据
            int[] redData = (int[]) redBandData.get("data");
            int[] nirData = (int[]) nirBandData.get("data");
            
            if (redData == null || nirData == null || redData.length != nirData.length) {
                ndviResult.put("status", "error");
                ndviResult.put("message", "波段数据不匹配");
                return ndviResult;
            }
            
            // 使用GDAL进行NDVI计算
            double[] ndviData = new double[redData.length];
            double sum = 0.0;
            int validPixels = 0;
            double minNDVI = Double.MAX_VALUE;
            double maxNDVI = Double.MIN_VALUE;
            
            // 计算NDVI
            for (int i = 0; i < redData.length; i++) {
                double red = redData[i];
                double nir = nirData[i];
                
                // 避免除零和无效值，使用GDAL标准处理方式
                if (red > 0 && nir > 0 && (nir + red) != 0) {
                    // NDVI计算公式: (NIR - RED) / (NIR + RED)
                    double ndvi = (nir - red) / (nir + red);
                    
                    // 限制NDVI值在-1到1之间
                    if (ndvi >= -1.0 && ndvi <= 1.0) {
                        ndviData[i] = ndvi;
                        sum += ndvi;
                        validPixels++;
                        
                        if (ndvi < minNDVI) minNDVI = ndvi;
                        if (ndvi > maxNDVI) maxNDVI = ndvi;
                    } else {
                        ndviData[i] = -9999; // 无效值标记
                    }
                } else {
                    ndviData[i] = -9999; // 无效值标记
                }
            }
            
            // 计算统计信息
            double meanNDVI = validPixels > 0 ? sum / validPixels : 0.0;
            
            ndviResult.put("status", "success");
            ndviResult.put("minValue", minNDVI == Double.MAX_VALUE ? 0.0 : minNDVI);
            ndviResult.put("maxValue", maxNDVI == Double.MIN_VALUE ? 0.0 : maxNDVI);
            ndviResult.put("meanValue", meanNDVI);
            ndviResult.put("validPixels", validPixels);
            ndviResult.put("totalPixels", redData.length);
            ndviResult.put("data", ndviData);
            
        } catch (Exception e) {
            ndviResult.put("status", "error");
            ndviResult.put("message", "NDVI计算失败: " + e.getMessage());
        }
        
        return ndviResult;
    }
    
    /**
     * 计算总体NDVI结果
     */
    private Map<String, Object> calculateOverallNDVI(List<Map<String, Object>> processingResults, Map<String, Object> parameters) {
        Map<String, Object> overallResult = new HashMap<>();
        
        double totalMin = Double.MAX_VALUE;
        double totalMax = Double.MIN_VALUE;
        double totalSum = 0.0;
        int totalValidPixels = 0;
        int successfulFiles = 0;
        
        for (Map<String, Object> fileResult : processingResults) {
            if ("success".equals(fileResult.get("status"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> ndviResult = (Map<String, Object>) fileResult.get("ndviResult");
                
                if (ndviResult != null && "success".equals(ndviResult.get("status"))) {
                    double minValue = (Double) ndviResult.get("minValue");
                    double maxValue = (Double) ndviResult.get("maxValue");
                    double meanValue = (Double) ndviResult.get("meanValue");
                    int validPixels = (Integer) ndviResult.get("validPixels");
                    
                    if (minValue < totalMin) totalMin = minValue;
                    if (maxValue > totalMax) totalMax = maxValue;
                    totalSum += meanValue * validPixels;
                    totalValidPixels += validPixels;
                    successfulFiles++;
                }
            }
        }
        
        // 构建保存路径: 用户数据根目录/username/ARD_CUB_GRIDT0_username_RAW/grid_id/analysis_type
        String cubeId = (String) parameters.get("cubeId");
        String algorithmCode = (String) parameters.get("algorithmCode");
        String username = (String) parameters.get("username"); // 从参数中获取用户名
        String outputFormat = (String) parameters.get("outputFormat");
        if (outputFormat == null) outputFormat = "tif";
        
        // 如果没有用户名，使用默认值
        if (username == null || username.isEmpty()) {
            username = "default_user";
        }
        
        // 使用新的用户目录结构：用户数据根目录/username/grid_id/quarter/analysis_type
        String resultDirectory = userDataConfig.buildUserAnalysisPath(username, cubeId, algorithmCode);
        String vizDirectory = userDataConfig.buildUserVizPath(username, cubeId);
        // 元数据路径示例：
        String metadataPath = resultDirectory + "/../metadata.json";
        
        // 创建目录
        try {
            File dir = new File(resultDirectory);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    logger.info("创建结果目录成功: {}", resultDirectory);
                } else {
                    logger.warn("创建结果目录失败: {}", resultDirectory);
                }
            }
        } catch (Exception e) {
            logger.error("创建结果目录异常: {}", e.getMessage(), e);
        }
        
        logger.info("NDVI结果保存路径: {}", resultDirectory);
        
        overallResult.put("totalFiles", processingResults.size());
        overallResult.put("successfulFiles", successfulFiles);
        overallResult.put("minValue", totalMin == Double.MAX_VALUE ? 0.0 : totalMin);
        overallResult.put("maxValue", totalMax == Double.MIN_VALUE ? 0.0 : totalMax);
        overallResult.put("meanValue", totalValidPixels > 0 ? totalSum / totalValidPixels : 0.0);
        overallResult.put("totalValidPixels", totalValidPixels);
        overallResult.put("outputPath", resultDirectory);
        overallResult.put("resultDirectory", resultDirectory);
        
        return overallResult;
    }
    
    @Override
    public boolean validateParameters(CubeWorkflow workflow, Map<String, Object> parameters) {
        // 验证NDVI计算所需的参数
        if (parameters == null) {
            return false;
        }
        
        // 检查是否包含必要的波段信息
        return parameters.containsKey("redBand") && parameters.containsKey("nirBand");
    }
    
    /**
     * 根据波段名称获取波段号
     * 支持格式: B4, B8, B2, B5 等
     */
    private int getBandNumber(String bandName, String defaultBand) {
        if (bandName == null || bandName.isEmpty()) {
            bandName = defaultBand;
        }
        
        // 提取波段号，如 B4 -> 4, B8 -> 8
        if (bandName.startsWith("B") && bandName.length() > 1) {
            try {
                return Integer.parseInt(bandName.substring(1));
            } catch (NumberFormatException e) {
                // 如果解析失败，使用默认值
            }
        }
        
        // 默认值
        return defaultBand.startsWith("B") ? Integer.parseInt(defaultBand.substring(1)) : 4;
    }
    
    /**
     * 保存NDVI结果为TIFF文件
     */
    private String saveNDVIToTiff(Map<String, Object> ndviResult, Map<String, Object> parameters, Map<String, Object> redBandData) {
        try {
            String taskId = (String) parameters.get("taskId");
            String cubeId = (String) parameters.get("cubeId");
            String algorithmCode = (String) parameters.get("algorithmCode");
            String username = (String) parameters.get("username");
            if (username == null || username.isEmpty()) {
                Object uid = parameters.get("userId");
                username = (uid != null) ? String.valueOf(uid) : "unnamed";
            }
            String outputFormat = (String) parameters.get("outputFormat");
            if (outputFormat == null) outputFormat = "tif";

            logger.info("=== 构建输出路径 ===");
            logger.info("username: {}", username);
            logger.info("cubeId: {}", cubeId);
            logger.info("algorithmCode: {}", algorithmCode);
            logger.info("dataRootPath: {}", userDataConfig.getDataRootPath());

            String resultDirectory = userDataConfig.buildUserAnalysisPath(username, cubeId, algorithmCode); // 没有quarter
            String vizDirectory = userDataConfig.buildUserVizPath(username, cubeId);
            // 元数据路径示例：
            String metadataPath = resultDirectory + "/../metadata.json";

            String outputPath = resultDirectory + "/ndvi_result_" + System.currentTimeMillis() + "." + outputFormat;

            logger.info("resultDirectory: {}", resultDirectory);
            logger.info("vizDirectory: {}", vizDirectory);
            logger.info("outputPath: {}", outputPath);
            logger.info("==================");

            File dir = new File(resultDirectory);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    logger.info("创建结果目录成功: {}", resultDirectory);
                } else {
                    logger.warn("创建结果目录失败: {}", resultDirectory);
                }
            }

            double[] ndviData = (double[]) ndviResult.get("data");
            int width = (Integer) redBandData.get("width");
            int height = (Integer) redBandData.get("height");
            double[] geotransform = (double[]) redBandData.get("geotransform");
            String projection = (String) redBandData.get("projection");
            saveNDVITiffWithGDAL(ndviData, width, height, geotransform, projection, outputPath);

            // 生成JPG预览图
            logger.info("开始生成NDVI预览图 - cubeId: {}, username: {}, outputPath: {}", cubeId, username, outputPath);
            String jpgPath = generatePreviewImage(ndviData, width, height, cubeId, username, ndviResult, outputPath);
            if (jpgPath != null) {
                logger.info("NDVI预览图已生成，相对路径: {}", jpgPath);
                parameters.put("browseImagePath", jpgPath);
                logger.info("预览图路径已保存到parameters: {}", jpgPath);
            } else {
                logger.warn("NDVI预览图生成失败或返回null");
            }

            logger.info("NDVI结果已保存到: {}", outputPath);
            return outputPath;

        } catch (Exception e) {
            String taskId = (String) parameters.get("taskId");
            if (taskId != null) {
                cubeTaskStepService.failStep(taskId, 4, "结果输出失败: " + e.getMessage());
            }
            logger.error("保存NDVI文件失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 使用GDAL保存NDVI数据为TIFF文件
     */
    private void saveNDVITiffWithGDAL(double[] ndviData, int width, int height, double[] geotransform, String projection, String outputPath) {
        try {
            // 设置GDAL配置选项，确保与初始化时一致
            org.gdal.gdal.gdal.SetConfigOption("GTIFF_SRS_SOURCE", "EPSG");
            // 使用GDAL保存包含地理空间信息的TIFF文件
            org.gdal.gdal.gdal.AllRegister();
            
            // 创建内存数据集
            org.gdal.gdal.Dataset dataset = org.gdal.gdal.gdal.GetDriverByName("GTiff").Create(
                outputPath, width, height, 1, 6); // 6 = GDT_Float32
            
            if (dataset == null) {
                throw new RuntimeException("无法创建GDAL数据集");
            }
            
            // 设置地理变换参数（从原始波段文件获取）
            if (geotransform != null && geotransform.length == 6) {
                dataset.SetGeoTransform(geotransform);
                logger.info("使用原始文件的地理变换参数: {}", java.util.Arrays.toString(geotransform));
            } else {
                // 使用默认的地理变换参数
                double[] defaultGeotransform = {
                    114.0,  // 左上角X坐标
                    0.0001, // 像素宽度
                    0.0,    // 旋转参数
                    22.5,   // 左上角Y坐标
                    0.0,    // 旋转参数
                    -0.0001 // 像素高度（负值表示Y轴向下）
                };
                dataset.SetGeoTransform(defaultGeotransform);
                logger.warn("使用默认地理变换参数");
            }
            
            // 设置投影信息
            if (projection != null && !projection.isEmpty()) {
                dataset.SetProjection(projection);
                logger.info("使用原始文件的投影信息");
            } else {
                // 使用WGS84地理坐标系
                String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
                dataset.SetProjection(wkt);
                logger.warn("使用默认投影信息（WGS84）");
            }
            
            // 设置NoData值
            dataset.GetRasterBand(1).SetNoDataValue(-9999.0);
            
            // 将NDVI数据写入栅格
            float[] floatData = new float[ndviData.length];
            for (int i = 0; i < ndviData.length; i++) {
                floatData[i] = (float) ndviData[i];
            }
            
            // 写入数据
            int result = dataset.GetRasterBand(1).WriteRaster(0, 0, width, height, width, height, 
                6, floatData); // 6 = GDT_Float32
            
            if (result != 0) { // 0 = CE_None
                throw new RuntimeException("写入栅格数据失败");
            }
            
            // 刷新缓存并关闭数据集
            dataset.FlushCache();
            dataset.delete();
            
            logger.info("NDVI TIFF文件保存成功: {}", outputPath);
            
        } catch (Exception e) {
            logger.error("保存NDVI TIFF文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("保存NDVI文件失败", e);
        }
    }
    
    /**
     * 从文件路径中提取cubeId
     */
    private String extractCubeIdFromPath(String filePath) {
        try {
            // 从路径中提取cubeId，例如：D:\GISER\ard\development\cubedata\GRID_CUBE_T0_N51E016010\raw\2025q1\...
            String[] parts = filePath.split("\\\\");
            for (int i = 0; i < parts.length; i++) {
                if ("cubedata".equals(parts[i]) && i + 1 < parts.length) {
                    return parts[i + 1];
                }
            }
        } catch (Exception e) {
            logger.error("提取cubeId失败: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 从文件路径中提取quarter
     */
    private String extractQuarterFromPath(String filePath) {
        try {
            // 从路径中提取quarter，例如：D:\GISER\ard\development\cubedata\GRID_CUBE_T0_N51E016010\raw\2025q1\...
            String[] parts = filePath.split("\\\\");
            for (int i = 0; i < parts.length; i++) {
                if ("raw".equals(parts[i]) && i + 1 < parts.length) {
                    return parts[i + 1];
                }
            }
        } catch (Exception e) {
            logger.error("提取quarter失败: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 为单个切片计算NDVI
     */
    private Map<String, Object> calculateNDVIForSlice(String redBandFile, String nirBandFile, 
                                                     String cubeId, String quarter, String taskId) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            // 读取波段数据
            Map<String, Object> redBandData = sliceFileReaderService.readBandData(redBandFile, 1);
            Map<String, Object> nirBandData = sliceFileReaderService.readBandData(nirBandFile, 1);
            
            if (!"success".equals(redBandData.get("status"))) {
                result.put("status", "error");
                result.put("message", "读取红光波段数据失败: " + redBandData.get("message"));
                return result;
            }
            
            if (!"success".equals(nirBandData.get("status"))) {
                result.put("status", "error");
                result.put("message", "读取近红外波段数据失败: " + nirBandData.get("message"));
                return result;
            }
            
            // 计算NDVI
            Map<String, Object> ndviResult = calculateNDVI(redBandData, nirBandData);
            
            if ("success".equals(ndviResult.get("status"))) {
                // 构建参数用于保存
                Map<String, Object> parameters = new java.util.HashMap<>();
                parameters.put("cubeId", cubeId);
                parameters.put("quarter", quarter);
                parameters.put("algorithmCode", "NDVI_ANALYSIS");
                parameters.put("taskId", taskId);
                parameters.put("username", "default_user"); // 添加用户名参数
                
                // 保存NDVI结果为TIFF文件
                String outputPath = saveNDVIToTiff(ndviResult, parameters, redBandData);
                ndviResult.put("outputPath", outputPath);
                
                // 获取生成的预览图路径
                String browseImagePath = (String) parameters.get("browseImagePath");
                if (browseImagePath != null) {
                    ndviResult.put("browseImagePath", browseImagePath);
                    result.put("browseImagePath", browseImagePath);
                    logger.info("预览图路径已添加到结果: {}", browseImagePath);
                }
                
                result.put("status", "success");
                result.put("message", "NDVI计算成功");
                result.put("outputPath", outputPath);
                result.put("ndviResult", ndviResult);
                
                logger.info("切片NDVI计算成功 - 立方体: {}, 季度: {}, 输出文件: {}", 
                           cubeId, quarter, outputPath);
            } else {
                result.put("status", "error");
                result.put("message", "NDVI计算失败: " + ndviResult.get("message"));
            }
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "计算NDVI异常: " + e.getMessage());
            logger.error("计算NDVI异常: {}", e.getMessage(), e);
        }
        
        return result;
    }
    
    /**
     * 生成NDVI预览图（JPG格式，带水印）
     * 
     * @param ndviData NDVI数据数组
     * @param width 图像宽度
     * @param height 图像高度
     * @param cubeId 立方体ID
     * @param username 用户名
     * @param ndviResult NDVI计算结果（包含统计信息）
     * @param tifPath TIF文件路径（用于生成对应的JPG文件名）
     * @return JPG文件路径（相对于服务器根目录）
     */
    private String generatePreviewImage(double[] ndviData, int width, int height, 
                                       String cubeId, String username, 
                                       Map<String, Object> ndviResult, String tifPath) {
        try {
            // 直接使用参数 username/cubeId，不要再重新定义。只从参数列表获取。
            String vizDirectory = userDataConfig.buildUserVizPath(username, cubeId);
            logger.info("构建预览图目录路径 - username: {}, cubeId: {}, 目录: {}", username, cubeId, vizDirectory);
            File vizDir = new File(vizDirectory);
            if (!vizDir.exists()) {
                logger.info("预览图目录不存在，开始创建: {}", vizDirectory);
                boolean created = vizDir.mkdirs();
                if (created) {
                    logger.info("✓ 预览图目录创建成功: {}", vizDirectory);
                } else {
                    logger.error("✗ 创建预览图目录失败: {}", vizDirectory);
                    return null;
                }
            } else {
                logger.info("预览图目录已存在: {}", vizDirectory);
            }
            
            // 生成JPG文件名（基于TIF文件名）
            String tifFileName = new File(tifPath).getName();
            String baseName = tifFileName.substring(0, tifFileName.lastIndexOf('.'));
            String jpgFileName = baseName + ".jpg";
            String jpgPath = vizDirectory + "/" + jpgFileName;
            
            // 创建BufferedImage（RGB格式）
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            // 获取NDVI统计信息（用于颜色映射）
            double minValue = ndviResult.get("minValue") != null ? (Double) ndviResult.get("minValue") : -1.0;
            double maxValue = ndviResult.get("maxValue") != null ? (Double) ndviResult.get("maxValue") : 1.0;
            
            // 将NDVI值映射到RGB颜色
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = y * width + x;
                    if (index < ndviData.length) {
                        double ndvi = ndviData[index];
                        Color color;
                        
                        // 处理无效值
                        if (ndvi <= -9999 || Double.isNaN(ndvi)) {
                            color = Color.BLACK; // 无效值显示为黑色
                        } else {
                            // NDVI颜色映射：-1到1 -> 蓝色到红色（标准NDVI配色）
                            // 或者使用更标准的配色：棕色(-1) -> 黄色(0) -> 绿色(1)
                            color = mapNDVIToColor(ndvi, minValue, maxValue);
                        }
                        
                        image.setRGB(x, y, color.getRGB());
                    } else {
                        image.setRGB(x, y, Color.BLACK.getRGB());
                    }
                }
            }
            
            // 添加水印（统计信息）
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // 设置字体
            Font font = new Font("Microsoft YaHei", Font.BOLD, Math.max(width / 40, 12));
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            
            // 获取统计信息
            Integer validPixels = ndviResult.get("validPixels") != null ? (Integer) ndviResult.get("validPixels") : 0;
            Double meanValue = ndviResult.get("meanValue") != null ? (Double) ndviResult.get("meanValue") : 0.0;
            Double minNDVI = ndviResult.get("minValue") != null ? (Double) ndviResult.get("minValue") : 0.0;
            Double maxNDVI = ndviResult.get("maxValue") != null ? (Double) ndviResult.get("maxValue") : 0.0;
            
            // 构建水印文本
            String watermarkText = String.format(
                "NDVI Analysis | Valid: %d | Mean: %.4f | Min: %.4f | Max: %.4f",
                validPixels, meanValue, minNDVI, maxNDVI
            );
            
            // 计算文本位置（右下角，带背景）
            int textX = width - fm.stringWidth(watermarkText) - 20;
            int textY = height - 20;
            
            // 绘制半透明背景
            g2d.setColor(new Color(0, 0, 0, 180)); // 半透明黑色
            g2d.fillRect(textX - 10, textY - fm.getHeight() - 5, 
                        fm.stringWidth(watermarkText) + 20, fm.getHeight() + 10);
            
            // 绘制文本
            g2d.setColor(Color.WHITE);
            g2d.drawString(watermarkText, textX, textY);
            
            // 添加立方体ID和用户名信息（左上角）
            String infoText = String.format("Cube: %s | User: %s", cubeId, username);
            Font infoFont = new Font("Microsoft YaHei", Font.PLAIN, Math.max(width / 50, 10));
            g2d.setFont(infoFont);
            FontMetrics infoFm = g2d.getFontMetrics();
            
            int infoX = 10;
            int infoY = 30;
            
            // 绘制信息背景
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(infoX - 5, infoY - infoFm.getHeight() - 5,
                        infoFm.stringWidth(infoText) + 10, infoFm.getHeight() + 10);
            
            // 绘制信息文本
            g2d.setColor(Color.WHITE);
            g2d.drawString(infoText, infoX, infoY);
            
            g2d.dispose();
            
            // 保存为JPG文件
            File jpgFile = new File(jpgPath);
            ImageIO.write(image, "jpg", jpgFile);
            
            logger.info("NDVI预览图生成成功（绝对路径）: {}", jpgPath);
            
            // 返回相对于用户数据根目录的路径（用于存储到数据库）
            // 例如：/default_user/ARD_CUB_GRIDT0_default_user_VIZ/cube_id/filename.jpg
            String dataRootPath = userDataConfig.getDataRootPath();
            logger.info("用户数据根目录: {}", dataRootPath);
            
            // 标准化路径分隔符
            String normalizedDataRootPath = dataRootPath.replace("\\", "/");
            String normalizedJpgPath = jpgPath.replace("\\", "/");
            
            // 计算相对路径
            String relativePath;
            if (normalizedJpgPath.startsWith(normalizedDataRootPath)) {
                relativePath = normalizedJpgPath.substring(normalizedDataRootPath.length());
                // 确保以 / 开头
                if (!relativePath.startsWith("/")) {
                    relativePath = "/" + relativePath;
                }
            } else {
                // 如果路径不匹配，使用相对于用户数据根目录的路径
                // 从路径中提取用户名和cubeId部分
                // jpgPath格式: dataRootPath/username/ARD_CUB_GRIDT0_username_VIZ/cubeId/filename.jpg
                String pathAfterRoot = normalizedJpgPath.replace(normalizedDataRootPath, "");
                relativePath = pathAfterRoot.startsWith("/") ? pathAfterRoot : "/" + pathAfterRoot;
            }
            
            logger.info("预览图相对路径: {}", relativePath);
            return relativePath;
            
        } catch (Exception e) {
            logger.error("生成NDVI预览图失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 将NDVI值映射到RGB颜色
     * 使用标准NDVI配色方案：-1(蓝色) -> 0(黄色) -> 1(绿色)
     */
    private Color mapNDVIToColor(double ndvi, double minValue, double maxValue) {
        // 标准化NDVI值到0-1范围
        double normalized = (ndvi - minValue) / (maxValue - minValue);
        normalized = Math.max(0.0, Math.min(1.0, normalized));
        
        int r, g, b;
        
        // NDVI标准配色：
        // -1.0 (蓝色) -> 0.0 (黄色) -> 1.0 (绿色)
        if (normalized < 0.5) {
            // 蓝色到黄色 (normalized: 0 -> 0.5)
            double t = normalized * 2.0; // 0 -> 1
            r = (int) (t * 255); // 0 -> 255
            g = (int) (t * 255); // 0 -> 255
            b = (int) ((1 - t) * 255); // 255 -> 0
        } else {
            // 黄色到绿色 (normalized: 0.5 -> 1.0)
            double t = (normalized - 0.5) * 2.0; // 0 -> 1
            r = (int) ((1 - t) * 255); // 255 -> 0
            g = 255; // 保持255
            b = 0; // 保持0
        }
        
        return new Color(r, g, b);
    }
}
