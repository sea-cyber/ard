package com.project.ard.dataretrieval.controller;

import com.project.ard.dataretrieval.entity.CubeWorkflow;
import com.project.ard.dataretrieval.service.ICubeWorkflowService;
import com.project.ard.dataretrieval.service.WorkflowProcessorManager;
import com.project.ard.dataretrieval.service.WorkflowProcessor;
import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流任务处理控制器
 */
@RestController
@RequestMapping("/ard/dataretrieval/workflow/task")
public class WorkflowTaskController extends BaseController {
    
    @Autowired
    private ICubeWorkflowService cubeWorkflowService;
    
    @Autowired
    private WorkflowProcessorManager workflowProcessorManager;
    
    /**
     * 处理工作流任务
     * @param request 任务请求参数
     * @return 处理结果
     */
    @PostMapping("/process")
    public AjaxResult processWorkflowTask(@RequestBody Map<String, Object> request) {
        try {
            // 获取工作流ID
            String workflowId = (String) request.get("workflowId");
            if (workflowId == null) {
                return error("工作流ID不能为空");
            }
            
            // 查询工作流信息
            CubeWorkflow workflow = cubeWorkflowService.selectCubeWorkflowByAlgorithmCode(workflowId);
            if (workflow == null) {
                return error("未找到指定的工作流");
            }
            
            // 获取计算参数
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters = (Map<String, Object>) request.get("parameters");
            
            // 获取切片文件路径
            @SuppressWarnings("unchecked")
            List<String> sliceFiles = (List<String>) request.get("sliceFiles");
            
            // 处理工作流任务
            Map<String, Object> result = workflowProcessorManager.processWorkflow(workflow, parameters, sliceFiles);
            
            return success(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return error("任务处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建并执行工作流任务（异步模式）
     * @param request 任务创建请求
     * @return 任务创建结果
     */
    @PostMapping("/create-and-execute")
    public AjaxResult createAndExecuteTask(@RequestBody Map<String, Object> request) {
        try {
            // 获取任务参数
            String workflowId = (String) request.get("workflow");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> selectedCubes = (List<Map<String, Object>>) request.get("selectedCubes");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> selectedSlices = (List<Map<String, Object>>) request.get("selectedSlices");
            
            if (workflowId == null) {
                return error("工作流不能为空");
            }
            
            // 查询工作流信息
            CubeWorkflow workflow = null;
            
            // 1. 首先尝试按工作流ID查询
            if (workflowId != null) {
                workflow = cubeWorkflowService.selectCubeWorkflowByWorkflowId(workflowId);
            }
            
            // 2. 如果ID查询失败，尝试按算法代码查询
            if (workflow == null && workflowId != null) {
                workflow = cubeWorkflowService.selectCubeWorkflowByAlgorithmCode(workflowId);
            }
            
            // 3. 如果算法代码查询失败，尝试按工作流名称查询
            if (workflow == null && workflowId != null) {
                CubeWorkflow searchWorkflow = new CubeWorkflow();
                searchWorkflow.setWorkflowName(workflowId);
                List<CubeWorkflow> workflows = cubeWorkflowService.selectCubeWorkflowList(searchWorkflow);
                if (workflows != null && !workflows.isEmpty()) {
                    workflow = workflows.get(0);
                }
            }
            
            if (workflow == null) {
                return error("未找到指定的工作流: " + workflowId);
            }
            
            // 获取任务ID（从请求中获取，如果不存在则生成新的）
            String taskId = (String) request.get("taskId");
            if (taskId == null || taskId.isEmpty()) {
                taskId = "TASK_" + System.currentTimeMillis();
            }
            
            // 立即返回任务创建成功
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("taskId", taskId);
            result.put("status", "created");
            result.put("message", "任务已创建，正在后台处理中...");
            result.put("workflowName", workflow.getWorkflowName());
            result.put("selectedCubesCount", selectedCubes != null ? selectedCubes.size() : 0);
            result.put("selectedSlicesCount", selectedSlices != null ? selectedSlices.size() : 0);
            
            // 异步执行计算任务
            executeWorkflowAsync(taskId, workflow, request, selectedCubes, selectedSlices);
            
            return success(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return error("任务创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 异步执行工作流任务
     */
    private void executeWorkflowAsync(String taskId, CubeWorkflow workflow, Map<String, Object> request, 
                                    List<Map<String, Object>> selectedCubes, List<Map<String, Object>> selectedSlices) {
        // 使用线程池异步执行
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                // 更新任务状态为运行中
                cubeTaskInfoService.updateTaskStatus(taskId, "running", 10, null);
                
                // ========== 第一步：数据准备 ==========
                // 开始数据准备步骤
                cubeTaskStepService.startStep(taskId, 1, "开始检索切片数据，检查文件是否存在");
                
                // 构建计算参数（用于获取cubeId等信息）
                Map<String, Object> parameters = buildCalculationParameters(request, workflow);
                
                // 将selectedSlices添加到parameters中
                parameters.put("selectedSlices", selectedSlices);
                
                // 检查切片文件是否存在
                List<String> sliceFiles = extractSliceFilePathsForCalculation(selectedSlices, parameters);
                
                // 如果没有找到任何切片文件，标记第一步为失败并停止
                if (sliceFiles == null || sliceFiles.isEmpty()) {
                    String errorMessage = "未找到切片文件，文件不存在或路径不正确";
                    System.err.println("数据准备失败 - 任务ID: " + taskId);
                    System.err.println("错误信息: " + errorMessage);
                    
                    // 标记第一步（数据准备）为失败
                    cubeTaskStepService.failStep(taskId, 1, errorMessage);
                    
                    // 更新任务状态为失败，进度保持在10%（数据准备阶段的进度）
                    cubeTaskInfoService.updateTaskStatus(taskId, "failed", 10, errorMessage);
                    System.err.println("任务停止 - 任务ID: " + taskId + ", 错误: " + errorMessage);
                    return; // 停止执行，不继续后续步骤
                }
                
                // 数据准备成功，完成第一步
                cubeTaskStepService.completeStep(taskId, 1, "数据准备完成，检索到切片数量: " + sliceFiles.size());
                System.out.println("数据准备完成 - 任务ID: " + taskId + ", 切片数量: " + sliceFiles.size());
                
                // ========== 第二步：任务拆分 ==========
                // 开始任务拆分步骤
                cubeTaskStepService.startStep(taskId, 2, "开始任务拆分，分析切片位置分布");
                
                // 完成任务拆分步骤
                String processingCenter = (String) parameters.get("processingCenter");
                cubeTaskStepService.completeStep(taskId, 2, "任务拆分完成，处理中心: " + processingCenter);
                
                // 更新进度
                cubeTaskInfoService.updateTaskProgress(taskId, 30);
                
                // 检查工作流处理器是否存在（在执行计算之前）
                WorkflowProcessor processor = workflowProcessorManager.getProcessor(workflow);
                if (processor == null) {
                    // 处理器不存在，立即停止任务
                    String errorMessage = "未找到支持的工作流处理器: " + workflow.getWorkflowId();
                    System.err.println("工作流计算失败 - 任务ID: " + taskId);
                    System.err.println("错误信息: " + errorMessage);
                    
                    // 标记步骤3为失败（算法初始化步骤）
                    cubeTaskStepService.failStep(taskId, 3, errorMessage);
                    
                    // 更新任务状态为失败，进度保持在30%（任务拆分完成时的进度）
                    cubeTaskInfoService.updateTaskStatus(taskId, "failed", 30, errorMessage);
                    System.err.println("任务计算失败 - 任务ID: " + taskId + ", 错误: " + errorMessage);
                    return; // 停止执行，不继续处理
                }
                
                // 执行计算
                Map<String, Object> result = workflowProcessorManager.processWorkflow(workflow, parameters, sliceFiles);
                
                // 检查计算结果状态
                String resultStatus = (String) result.get("status");
                
                // 如果结果是错误状态，立即停止
                if ("error".equals(resultStatus)) {
                    String errorMessage = (String) result.get("message");
                    if (errorMessage == null || errorMessage.isEmpty()) {
                        errorMessage = "工作流处理失败";
                    }
                    
                    System.err.println("工作流计算失败 - 任务ID: " + taskId);
                    System.err.println("错误信息: " + errorMessage);
                    
                    // 标记步骤3为失败（算法初始化步骤）
                    cubeTaskStepService.failStep(taskId, 3, errorMessage);
                    
                    // 更新任务状态为失败，进度保持在30%（任务拆分完成时的进度）
                    cubeTaskInfoService.updateTaskStatus(taskId, "failed", 30, errorMessage);
                    System.err.println("任务计算失败 - 任务ID: " + taskId + ", 错误: " + errorMessage);
                    return; // 停止执行，不继续处理
                }
                
                // 更新进度（只有在计算成功时才更新）
                cubeTaskInfoService.updateTaskProgress(taskId, 80);
                
                // 计算完成后保存结果到数据库
                saveWorkflowResult(taskId, workflow, result, parameters);
                
                // 根据计算结果更新任务状态
                if ("success".equals(resultStatus)) {
                    // 计算成功，更新任务状态为完成
                    cubeTaskInfoService.completeTask(taskId, 1, "计算结果已保存");
                    System.out.println("任务计算完成 - 任务ID: " + taskId);
                } else {
                    // 其他状态（如部分成功），也标记为失败
                    String errorMessage = (String) result.get("message");
                    if (errorMessage == null || errorMessage.isEmpty()) {
                        errorMessage = "工作流处理失败";
                    }
                    
                    // 标记步骤3为失败
                    cubeTaskStepService.failStep(taskId, 3, errorMessage);
                    
                    // 更新任务状态为失败，进度保持在80%（计算完成但保存失败）
                    cubeTaskInfoService.updateTaskStatus(taskId, "failed", 80, errorMessage);
                    System.err.println("任务计算失败 - 任务ID: " + taskId + ", 错误: " + errorMessage);
                }
                
            } catch (Exception e) {
                // 记录错误并更新任务状态为失败
                String errorMessage = e.getMessage();
                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = "任务执行异常: " + e.getClass().getSimpleName();
                }
                
                System.err.println("异步任务执行失败: " + errorMessage);
                e.printStackTrace();
                
                // 标记步骤3为失败（算法初始化步骤）
                cubeTaskStepService.failStep(taskId, 3, errorMessage);
                
                // 更新任务状态为失败，进度保持在30%（任务拆分完成时的进度，因为异常发生在计算阶段）
                cubeTaskInfoService.updateTaskStatus(taskId, "failed", 30, errorMessage);
            }
        });
    }
    
    /**
     * 构建计算参数
     */
    private Map<String, Object> buildCalculationParameters(Map<String, Object> request, CubeWorkflow workflow) {
        Map<String, Object> parameters = new java.util.HashMap<>();
        
        // 根据工作流ID构建不同的参数
        String workflowId = workflow.getWorkflowId().toString();
        
        // 从请求中提取cubeId信息
        String cubeId = extractCubeIdFromRequest(request);
        
        // 根据工作流ID匹配计算类型
        if ("flow_0001".equals(workflowId)) {
            // NDVI植被指数计算参数
            parameters.put("redBand", "B4"); // 红波段
            parameters.put("nirBand", "B8"); // 近红外波段
            parameters.put("outputFormat", "tif");
            parameters.put("outputResolution", "20m");
            parameters.put("calculationType", "ndvi");
            
            // 添加路径构建参数（不设置统一的quarter，让每个切片使用自己的quarter）
            parameters.put("cubeId", cubeId);
            parameters.put("workflowName", workflow.getWorkflowName());
            parameters.put("workflowId", workflow.getWorkflowId());
            parameters.put("algorithmCode", workflow.getAlgorithmCode());
        } else if ("flow_0002".equals(workflowId)) {
            // 土地覆盖分类参数
            parameters.put("trainingData", "training_samples.geojson");
            parameters.put("labelData", "land_cover_labels.csv");
            parameters.put("modelType", "random_forest");
            parameters.put("calculationType", "land_cover");
        } else {
            // 默认参数
            parameters.put("outputFormat", "tif");
            parameters.put("outputResolution", "20m");
            parameters.put("calculationType", "default");
        }
        
        // 添加通用参数
        parameters.put("taskId", request.get("taskId"));
        parameters.put("timeStart", request.get("timeStart"));
        parameters.put("username", request.get("username")); // 添加用户名参数
        parameters.put("timeEnd", request.get("timeEnd"));
        parameters.put("processingCenter", request.get("processingCenter"));
        parameters.put("userId", request.get("userId"));
        if ((parameters.get("username") == null || "".equals(parameters.get("username"))) && request.get("userId") != null) {
            parameters.put("username", String.valueOf(request.get("userId")));
        }
        
        // 添加切片文件路径列表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> selectedSlices = (List<Map<String, Object>>) request.get("selectedSlices");
        if (selectedSlices != null && !selectedSlices.isEmpty()) {
            List<String> sliceFiles = extractSliceFilePathsForCalculation(selectedSlices, parameters);
            parameters.put("sliceFiles", sliceFiles);
            logger.info("提取到切片文件数量: {}", sliceFiles.size());
        }
        
        // 添加结果保存路径参数
        parameters.put("resultDirectory", buildResultDirectory(request, workflowId));
        
        return parameters;
    }
    
    /**
     * 构建结果保存目录
     */
    private String buildResultDirectory(Map<String, Object> request, String workflowId) {
        String processingCenter = (String) request.get("processingCenter");
        
        // 构建目录结构：/results/{processingCenter}/{workflowId}/
        String baseDir = "/results";
        if (processingCenter != null && !processingCenter.isEmpty()) {
            baseDir += "/" + processingCenter;
        }
        baseDir += "/" + workflowId;
        
        return baseDir;
    }
    
    
    /**
     * 提取切片文件路径（用于计算）
     * 路径结构: cube_id/raw/quarter/******.tif
     */
    private List<String> extractSliceFilePathsForCalculation(List<Map<String, Object>> selectedSlices, Map<String, Object> parameters) {
        List<String> sliceFiles = new java.util.ArrayList<>();
        
        if (selectedSlices != null) {
            String cubeId = (String) parameters.get("cubeId");
            
            // 使用Set去重，避免重复文件
            java.util.Set<String> uniqueFiles = new java.util.HashSet<>();
            
            for (Map<String, Object> slice : selectedSlices) {
                String sliceCubeId = (String) slice.get("cubeId");
                String sliceFileName = (String) slice.get("fileName");
                String sliceQuarter = (String) slice.get("quarter");
                
                // 关键修复：必须使用切片自己的cubeId，不能使用全局cubeId
                // 这样可以确保每个切片使用正确的cubeId构建路径和标识符
                String targetCubeId = sliceCubeId != null ? sliceCubeId : cubeId;
                
                // 使用切片自己的quarter，如果没有则使用参数中的quarter
                String targetQuarter = sliceQuarter != null ? sliceQuarter : (String) parameters.get("quarter");
                
                // 验证必需字段
                if (targetCubeId == null || targetCubeId.isEmpty()) {
                    System.err.println("警告：切片 " + sliceFileName + " 的cubeId为空，跳过处理");
                    continue;
                }
                
                if (targetQuarter == null || targetQuarter.isEmpty()) {
                    System.err.println("警告：切片 " + sliceFileName + " 的quarter为空，跳过处理");
                    continue;
                }
                
                // 构建绝对目录路径: D:\GISER\ard\development\cubedata\ARD_CUB_GRIDT0_OFF_RAW\cube_id\quarter\
                String basePath = "D:\\GISER\\ard\\development\\cubedata\\ARD_CUB_GRIDT0_OFF_RAW";
                String directoryPath = basePath + "\\" + targetCubeId + "\\" + targetQuarter + "\\";
                
                System.out.println("处理切片: " + sliceFileName + ", 立方体ID: " + targetCubeId + ", 季度: " + targetQuarter);
                
                // 根据切片标识符和季度信息，构建正确的波段文件名
                if (sliceFileName != null && !sliceFileName.isEmpty()) {
                    // 构建红光波段和近红外波段文件名
                    String redBandFile = directoryPath + targetCubeId + "_" + targetQuarter + "_B4.TIF";
                    String nirBandFile = directoryPath + targetCubeId + "_" + targetQuarter + "_B5.TIF";
                    
                    // 检查波段文件是否存在
                    File redFile = new File(redBandFile);
                    File nirFile = new File(nirBandFile);
                    
                    if (redFile.exists() && nirFile.exists()) {
                        // 构建切片标识符：sliceFileName|cubeId|quarter
                        // 这里的cubeId必须是切片自己的cubeId，这样后续处理时才能使用正确的cubeId
                        String sliceIdentifier = sliceFileName + "|" + targetCubeId + "|" + targetQuarter;
                        uniqueFiles.add(sliceIdentifier);
                        System.out.println("✓ 找到波段文件: " + redBandFile + ", " + nirBandFile);
                        System.out.println("✓ 添加切片标识符: " + sliceIdentifier);
                    } else {
                        System.err.println("✗ 波段文件不存在: " + redBandFile + " 或 " + nirBandFile);
                        System.err.println("✗ 跳过不存在的波段文件");
                    }
                } else {
                    // 如果没有指定文件名，跳过这个切片
                    System.err.println("✗ 跳过没有文件名的切片");
                }
            }
            
            // 将Set转换为List
            sliceFiles.addAll(uniqueFiles);
            System.out.println("最终切片文件数量: " + sliceFiles.size());
        }
        
        return sliceFiles;
    }
    
    /**
     * 扫描目录下的所有TIFF文件
     */
    private List<String> scanTiffFilesInDirectory(String directoryPath) {
        List<String> tiffFiles = new java.util.ArrayList<>();
        
        try {
            File directory = new File(directoryPath);
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".tif") || name.toLowerCase().endsWith(".tiff"));
                
                if (files != null) {
                    for (File file : files) {
                        tiffFiles.add(file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("扫描目录失败: " + directoryPath + ", 错误: " + e.getMessage());
        }
        
        return tiffFiles;
    }
    
    /**
     * 提取切片文件路径（旧方法，保留兼容性）
     */
    private List<String> extractSliceFilePaths(List<Map<String, Object>> selectedSlices) {
        List<String> sliceFiles = new java.util.ArrayList<>();
        
        if (selectedSlices != null) {
            for (Map<String, Object> slice : selectedSlices) {
                String slicePath = (String) slice.get("slicePath");
                String fileName = (String) slice.get("fileName");
                String cubeId = (String) slice.get("cubeId");
                
                // 如果slicePath为空，尝试构建路径
                if (slicePath == null || slicePath.isEmpty()) {
                    if (fileName != null && cubeId != null) {
                        // 构建可能的路径
                        slicePath = "/data/cube_slices/" + cubeId + "/" + fileName;
                    }
                }
                
                if (slicePath != null && !slicePath.isEmpty()) {
                    sliceFiles.add(slicePath);
                }
            }
        }
        
        return sliceFiles;
    }
    
    /**
     * 保存工作流计算结果到数据库
     */
    private void saveWorkflowResult(String taskId, CubeWorkflow workflow, Map<String, Object> result, Map<String, Object> parameters) {
        try {
            String cubeId = (String) parameters.get("cubeId");
            String quarter = (String) parameters.get("quarter");
            String algorithmCode = workflow.getAlgorithmCode();
            
            // 构建结果保存路径: 用户数据根目录/username/grid_id/quarter/analysis_type
            String username = (String) parameters.get("username");
            if (username == null || username.isEmpty()) {
                username = "default_user";
            }
            String resultDirectory = username + "/" + cubeId + "/" + quarter + "/" + algorithmCode;
            
            if ("success".equals(result.get("status"))) {
                System.out.println("工作流计算成功 - 任务ID: " + taskId);
                System.out.println("成功信息: " + result.get("message"));
                
                // 调试：打印result中的所有键
                System.out.println("Result keys: " + result.keySet());
                
                // 先尝试从calculationResult中获取outputPaths
                List<String> outputPaths = null;
                @SuppressWarnings("unchecked")
                Map<String, Object> calculationResult = (Map<String, Object>) result.get("calculationResult");
                    
                if (calculationResult != null) {
                    System.out.println("Found calculationResult: " + calculationResult.keySet());
                    @SuppressWarnings("unchecked")
                    List<String> paths = (List<String>) calculationResult.get("outputPaths");
                    if (paths != null && !paths.isEmpty()) {
                        outputPaths = paths;
                        System.out.println("Found " + outputPaths.size() + " output paths in calculationResult");
                    }
                }   

                // 如果没有从calculationResult中找到，尝试直接从result中获取
                if (outputPaths == null) {
                    System.out.println("Trying to get outputPaths from result...");
                    @SuppressWarnings("unchecked")
                    List<String> paths = (List<String>) result.get("outputPaths");
                    if (paths != null && !paths.isEmpty()) {
                        outputPaths = paths;
                        System.out.println("Found " + outputPaths.size() + " output paths in result");
                    }
                }

                if (outputPaths != null && !outputPaths.isEmpty()) {
                    System.out.println("开始保存 " + outputPaths.size() + " 个输出文件到数据库");
                    
                    // 为每个输出文件保存到数据库
                    for (int i = 0; i < outputPaths.size(); i++) {
                        System.out.println("========== 处理第 " + (i + 1) + " 个切片 ==========");
                        String outputPath = outputPaths.get(i);
                        
                        // 从输出路径中提取文件名
                        String fileName = "ndvi_result_" + System.currentTimeMillis() + "_" + i + ".tif";
                        if (outputPath != null && !outputPath.isEmpty()) {
                            // 从完整路径中提取文件名（支持Windows路径分隔符）
                            String[] pathParts = outputPath.split("[\\\\/]");
                            if (pathParts.length > 0) {
                                fileName = pathParts[pathParts.length - 1];
                            }
                            System.out.println("输出文件路径 " + (i + 1) + ": " + outputPath);
                            System.out.println("提取的文件名 " + (i + 1) + ": " + fileName);
                        }
                        
                        // 获取该输出路径对应的原始切片cubeId和quarter
                        // 必须使用映射关系，确保每个结果切片的cubeId与原始切片一致
                        String sliceCubeId = null;
                        String sliceQuarter = null;
                        
                        // 方法1（必须）：从result中的映射获取原始切片的cubeId
                        // 这个映射是在NDVIWorkflowProcessor中建立的，确保准确性
                        // 优先从calculationResult中获取（如果存在），否则从result顶层获取
                        @SuppressWarnings("unchecked")
                        Map<String, String> outputPathToCubeId = null;
                        @SuppressWarnings("unchecked")
                        Map<String, String> outputPathToQuarter = null;
                        
                        // 首先尝试从calculationResult中获取映射
                        @SuppressWarnings("unchecked")
                        Map<String, String> outputPathToBrowseImagePath = null;
                        if (calculationResult != null) {
                            outputPathToCubeId = (Map<String, String>) calculationResult.get("outputPathToCubeId");
                            outputPathToQuarter = (Map<String, String>) calculationResult.get("outputPathToQuarter");
                            outputPathToBrowseImagePath = (Map<String, String>) calculationResult.get("outputPathToBrowseImagePath");
                        }
                        
                        // 如果calculationResult中没有，从result顶层获取
                        if (outputPathToCubeId == null) {
                            outputPathToCubeId = (Map<String, String>) result.get("outputPathToCubeId");
                            outputPathToQuarter = (Map<String, String>) result.get("outputPathToQuarter");
                            outputPathToBrowseImagePath = (Map<String, String>) result.get("outputPathToBrowseImagePath");
                        }
                        
                        // 标准化输出路径（统一路径分隔符，便于匹配）
                        String normalizedOutputPath = outputPath != null ? outputPath.replace("\\", "/") : null;
                        
                        // 尝试直接匹配
                        boolean foundInMap = false;
                        if (outputPathToCubeId != null && normalizedOutputPath != null) {
                            // 先尝试直接匹配
                            if (outputPathToCubeId.containsKey(outputPath)) {
                                sliceCubeId = outputPathToCubeId.get(outputPath);
                                sliceQuarter = outputPathToQuarter != null ? outputPathToQuarter.get(outputPath) : null;
                                foundInMap = true;
                                System.out.println("✓ 从映射获取原始切片信息（直接匹配） - cubeId: " + sliceCubeId + ", quarter: " + sliceQuarter);
                            } 
                            // 如果直接匹配失败，尝试标准化路径匹配
                            else if (outputPathToCubeId.containsKey(normalizedOutputPath)) {
                                sliceCubeId = outputPathToCubeId.get(normalizedOutputPath);
                                sliceQuarter = outputPathToQuarter != null ? outputPathToQuarter.get(normalizedOutputPath) : null;
                                foundInMap = true;
                                System.out.println("✓ 从映射获取原始切片信息（标准化匹配） - cubeId: " + sliceCubeId + ", quarter: " + sliceQuarter);
                            }
                            // 如果还是失败，尝试反向标准化（将/转为\）
                            else {
                                String reversedPath = outputPath.replace("/", "\\");
                                if (outputPathToCubeId.containsKey(reversedPath)) {
                                    sliceCubeId = outputPathToCubeId.get(reversedPath);
                                    sliceQuarter = outputPathToQuarter != null ? outputPathToQuarter.get(reversedPath) : null;
                                    foundInMap = true;
                                    System.out.println("✓ 从映射获取原始切片信息（反向标准化匹配） - cubeId: " + sliceCubeId + ", quarter: " + sliceQuarter);
                                }
                            }
                        }
                        
                        if (!foundInMap) {
                            System.err.println("❌ 警告：无法从映射中获取输出路径对应的cubeId！");
                            System.err.println("输出路径（原始）: " + outputPath);
                            System.err.println("输出路径（标准化）: " + normalizedOutputPath);
                            System.err.println("映射是否存在: " + (outputPathToCubeId != null));
                            if (outputPathToCubeId != null) {
                                System.err.println("映射大小: " + outputPathToCubeId.size());
                                System.err.println("映射中的键（前5个）:");
                                int count = 0;
                                for (String key : outputPathToCubeId.keySet()) {
                                    if (count++ < 5) {
                                        System.err.println("  [" + count + "] " + key + " -> cubeId: " + outputPathToCubeId.get(key));
                                    } else {
                                        break;
                                    }
                                }
                                // 检查是否有文件名匹配的键（文件名是唯一的）
                                // 注意：fileName 已在前面定义，这里使用 fileName 变量
                                String outputFileName = normalizedOutputPath != null ? normalizedOutputPath.substring(normalizedOutputPath.lastIndexOf("/") + 1) : null;
                                if (outputFileName != null) {
                                    System.err.println("尝试通过文件名匹配: " + outputFileName);
                                    for (String key : outputPathToCubeId.keySet()) {
                                        if (key.contains(outputFileName)) {
                                            sliceCubeId = outputPathToCubeId.get(key);
                                            sliceQuarter = outputPathToQuarter != null ? outputPathToQuarter.get(key) : null;
                                            foundInMap = true;
                                            System.err.println("✓ 通过文件名匹配找到映射 - cubeId: " + sliceCubeId + ", quarter: " + sliceQuarter);
                                            break;
                                        }
                                    }
                                }
                            }
                            
                            // 方法2（备选）：从selectedSlices中按索引获取（这是必须的，因为映射可能失败）
                            // 注意：outputPaths的顺序应该与sliceFilePaths的处理顺序一致
                            // 而sliceFilePaths是从selectedSlices按顺序构建的，所以索引应该匹配
                            if (!foundInMap) {
                                @SuppressWarnings("unchecked")
                                List<Map<String, Object>> selectedSlices = (List<Map<String, Object>>) parameters.get("selectedSlices");
                                if (selectedSlices != null && i < selectedSlices.size()) {
                                    Map<String, Object> slice = selectedSlices.get(i);
                                    if (slice != null) {
                                        sliceCubeId = (String) slice.get("cubeId");
                                        sliceQuarter = sliceQuarter != null ? sliceQuarter : (String) slice.get("quarter");
                                        foundInMap = true;
                                        System.out.println("✓ 从selectedSlices[索引 " + i + "] 获取原始切片信息 - cubeId: " + sliceCubeId + ", quarter: " + sliceQuarter);
                                    } else {
                                        System.err.println("警告：selectedSlices[" + i + "] 为null");
                                    }
                                } else {
                                    System.err.println("警告：索引 " + i + " 超出 selectedSlices 范围（大小: " + (selectedSlices != null ? selectedSlices.size() : 0) + "）");
                                }
                            }
                        }
                        
                        // 绝对不使用路径提取或默认值，必须从映射或selectedSlices获取
                        if (sliceCubeId == null) {
                            System.err.println("❌ 错误：无法确定第 " + (i + 1) + " 个切片的原始cubeId，跳过保存！");
                            System.err.println("输出路径: " + outputPath);
                            System.err.println("映射大小: " + (outputPathToCubeId != null ? outputPathToCubeId.size() : 0));
                            System.err.println("selectedSlices大小: " + (parameters.get("selectedSlices") != null ? ((List<?>) parameters.get("selectedSlices")).size() : 0));
                            System.err.println("这会导致数据不一致，请检查代码逻辑");
                            continue;
                        }
                        
                        // 获取预览图路径
                        String browseImagePath = null;
                        if (outputPathToBrowseImagePath != null && outputPath != null) {
                            // 尝试多种路径格式匹配
                            if (outputPathToBrowseImagePath.containsKey(outputPath)) {
                                browseImagePath = outputPathToBrowseImagePath.get(outputPath);
                            } else if (normalizedOutputPath != null && outputPathToBrowseImagePath.containsKey(normalizedOutputPath)) {
                                browseImagePath = outputPathToBrowseImagePath.get(normalizedOutputPath);
                            } else {
                                String reversedPath = outputPath.replace("/", "\\");
                                if (outputPathToBrowseImagePath.containsKey(reversedPath)) {
                                    browseImagePath = outputPathToBrowseImagePath.get(reversedPath);
                                }
                            }
                        }
                        
                        System.out.println("═══════════════════════════════════════");
                        System.out.println("保存结果切片到数据库");
                        System.out.println("输出路径: " + outputPath);
                        System.out.println("原始切片cubeId: " + sliceCubeId);
                        System.out.println("原始切片quarter: " + sliceQuarter);
                        System.out.println("预览图路径: " + (browseImagePath != null ? browseImagePath : "未找到"));
                        System.out.println("═══════════════════════════════════════");
                        
                        // 创建计算结果对象（用于兼容原有接口）
                        Map<String, Object> calcResult = new HashMap<>();
                        calcResult.put("outputPath", outputPath);
                        calcResult.put("validPixels", 0);
                        calcResult.put("meanValue", 0.0);
                        calcResult.put("minValue", 0.0);
                        calcResult.put("maxValue", 0.0);
                        if (browseImagePath != null) {
                            calcResult.put("browseImagePath", browseImagePath);
                        }
                        
                        // 保存结果到数据库（使用每个切片对应的cubeId）
                        boolean saveResult = saveResultToDatabase(taskId, sliceCubeId, algorithmCode, outputPath, fileName, sliceQuarter, calcResult);
                        
            if (saveResult) {
                try {
                    String __metaUsername = null;
                    try { __metaUsername = com.project.common.utils.SecurityUtils.getUsername(); } catch (Exception ignore) {}
                    if (__metaUsername == null || __metaUsername.isEmpty()) __metaUsername = "default_user";
                    String gridId = sliceCubeId != null ? sliceCubeId.replace("GRID_CUBE_", "") : null;
                    String browsePath = (String) calculationResult.get("browseImagePath");
                    updateUserGridMetadata(__metaUsername, gridId, fileName, algorithmCode, outputPath, browsePath);
                } catch (Exception metaEx) {
                    System.err.println("更新用户元数据文件失败: " + metaEx.getMessage());
                }
                            System.out.println("第 " + (i + 1) + " 个切片插入数据库成功!");
                        } else {
                            System.err.println("第 " + (i + 1) + " 个切片插入数据库失败!");
                        }
                    }
                    
                    System.out.println("工作流计算完成 - 任务ID: " + taskId);
                    System.out.println("结果保存路径: " + resultDirectory);
                    System.out.println("输出文件数量: " + outputPaths.size());
                    System.out.println("算法代码: " + algorithmCode);
                } else {
                    System.err.println("未找到输出路径列表，尝试兼容的单文件处理逻辑");
                    
                    // 兼容原有的单文件处理逻辑
                    if (calculationResult != null) {
                        String outputPath = (String) calculationResult.get("outputPath");
                        
                        // 从输出路径中提取文件名
                        String fileName = "ndvi_result_" + System.currentTimeMillis() + ".tif";
                        if (outputPath != null && !outputPath.isEmpty()) {
                            // 从完整路径中提取文件名
                            String[] pathParts = outputPath.split("\\\\");
                            if (pathParts.length > 0) {
                                fileName = pathParts[pathParts.length - 1];
                            }
                            System.out.println("输出文件路径: " + outputPath);
                            System.out.println("提取的文件名: " + fileName);
                        }
                        
                        // 保存结果到数据库
                        boolean saveResult = saveResultToDatabase(taskId, cubeId, algorithmCode, outputPath, fileName, quarter, calculationResult);
                        
                        if (saveResult) {
                            System.out.println("第 1 个切片插入数据库成功!");
                        } else {
                            System.err.println("第 1 个切片插入数据库失败!");
                        }
                        
                        System.out.println("工作流计算完成 - 任务ID: " + taskId);
                        System.out.println("结果保存路径: " + resultDirectory);
                        System.out.println("输出文件: " + fileName);
                        System.out.println("算法代码: " + algorithmCode);
                    } else {
                        System.err.println("没有找到任何结果数据");
                    }
                }
                
            } else {
                System.err.println("工作流计算失败 - 任务ID: " + taskId);
                System.err.println("错误信息: " + result.get("message"));
            }
            
        } catch (Exception e) {
            System.err.println("保存工作流结果失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取工作流处理状态
     * @param taskId 任务ID
     * @return 处理状态
     */
    @GetMapping("/status/{taskId}")
    public AjaxResult getTaskStatus(@PathVariable String taskId) {
        // TODO: 实现任务状态查询逻辑
        return success("任务状态查询功能待实现");
    }
    
    /**
     * 从请求中提取cubeId
     */
    private String extractCubeIdFromRequest(Map<String, Object> request) {
        // 尝试从selectedCubes中提取cubeId
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> selectedCubes = (List<Map<String, Object>>) request.get("selectedCubes");
        
        if (selectedCubes != null && !selectedCubes.isEmpty()) {
            Map<String, Object> firstCube = selectedCubes.get(0);
            String cubeId = (String) firstCube.get("cubeId");
            if (cubeId != null) {
                return cubeId;
            }
        }
        
        // 尝试从selectedSlices中提取cubeId
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> selectedSlices = (List<Map<String, Object>>) request.get("selectedSlices");
        
        if (selectedSlices != null && !selectedSlices.isEmpty()) {
            Map<String, Object> firstSlice = selectedSlices.get(0);
            String cubeId = (String) firstSlice.get("cubeId");
            if (cubeId != null) {
                return cubeId;
            }
        }
        
        return "GRID_CUBE_T0_N51E016010"; // 默认值
    }
    
    
    @Autowired
    private com.project.ard.dataretrieval.service.CubeResultSliceInfoService cubeResultSliceInfoService;
    
    @Autowired
    private com.project.ard.dataretrieval.service.CubeTaskInfoService cubeTaskInfoService;
    
    @Autowired
    private com.project.ard.dataretrieval.service.CubeTaskStepService cubeTaskStepService;

    @Autowired
    private com.project.ard.dataretrieval.config.UserDataConfig userDataConfig;

    private final com.fasterxml.jackson.databind.ObjectMapper __metaMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    
    /**
     * 保存计算结果到数据库
     */
    private boolean saveResultToDatabase(String taskId, String cubeId, String algorithmCode, 
                                    String outputPath, String fileName, String quarter, 
                                    Map<String, Object> calculationResult) {
        try {
            System.out.println("=== 保存计算结果到数据库 ===");
            System.out.println("任务ID: " + taskId);
            System.out.println("立方体ID: " + cubeId);
            System.out.println("算法代码: " + algorithmCode);
            System.out.println("输出路径: " + outputPath);
            System.out.println("文件名: " + fileName);
            System.out.println("季度: " + quarter);
            
            // 创建CubeResultSliceInfo实体
            com.project.ard.dataretrieval.domain.CubeResultSliceInfo resultSliceInfo = 
                new com.project.ard.dataretrieval.domain.CubeResultSliceInfo();
            
            // 设置基本信息
            resultSliceInfo.setCubeId(cubeId);
            resultSliceInfo.setUserId(1L); // 当前用户ID，实际应该从SecurityContext获取
            resultSliceInfo.setRawSliceId(1); // 原始切片ID，实际应该从参数获取
            resultSliceInfo.setAnalysisType(algorithmCode);
            resultSliceInfo.setResultSlicePath(outputPath);
            resultSliceInfo.setFileName(fileName);
            resultSliceInfo.setFileFormat("TIF");
            resultSliceInfo.setLocation("N51°E16°区域网格：51.0°N-51.1°N, 16.0°E-16.1°E");
            resultSliceInfo.setResolution("30m×30m");
            resultSliceInfo.setCreatedBy("1"); // 当前用户ID
            resultSliceInfo.setTaskId(taskId); // 关联任务ID
            
            // 设置计算结果描述
            if (calculationResult != null) {
                Integer validPixels = (Integer) calculationResult.get("validPixels");
                Double meanValue = (Double) calculationResult.get("meanValue");
                Double minValue = (Double) calculationResult.get("minValue");
                Double maxValue = (Double) calculationResult.get("maxValue");
                
                String resultDesc = String.format("NDVI植被指数分析结果 - 有效像素: %d, 平均值: %.6f, 最小值: %.6f, 最大值: %.6f", 
                    validPixels != null ? validPixels : 0,
                    meanValue != null ? meanValue : 0.0,
                    minValue != null ? minValue : 0.0,
                    maxValue != null ? maxValue : 0.0
                );
                resultSliceInfo.setResultDesc(resultDesc);
                
                System.out.println("有效像素数: " + validPixels);
                System.out.println("平均值: " + meanValue);
                System.out.println("最小值: " + minValue);
                System.out.println("最大值: " + maxValue);
            } else {
                resultSliceInfo.setResultDesc("NDVI植被指数分析结果");
            }
            
            // 设置预览图路径（无论calculationResult是否为null都尝试设置）
            String browseImagePath = null;
            if (calculationResult != null) {
                browseImagePath = (String) calculationResult.get("browseImagePath");
                if (browseImagePath != null && !browseImagePath.isEmpty()) {
                    resultSliceInfo.setBrowseImagePath(browseImagePath);
                    System.out.println("✓ 设置预览图路径到数据库: " + browseImagePath);
                } else {
                    System.out.println("✗ calculationResult中预览图路径为空或null");
                }
            } else {
                System.out.println("✗ calculationResult为null，无法获取预览图路径");
            }
            
            // 调试：打印最终要保存的对象的所有字段
            System.out.println("=== 保存到数据库的对象信息 ===");
            System.out.println("cubeId: " + resultSliceInfo.getCubeId());
            System.out.println("fileName: " + resultSliceInfo.getFileName());
            System.out.println("resultSlicePath: " + resultSliceInfo.getResultSlicePath());
            System.out.println("browseImagePath: " + resultSliceInfo.getBrowseImagePath());
            System.out.println("===============================");
            
            // 保存到数据库
            boolean saveResult = cubeResultSliceInfoService.saveResultSliceInfo(resultSliceInfo);
            
            if (saveResult) {
                System.out.println("=== 数据库保存成功 ===");
                return true;
            } else {
                System.err.println("=== 数据库保存失败 ===");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("保存计算结果到数据库失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将新生成的结果切片追加到用户格网的元数据文件中
     */
    private void updateUserGridMetadata(String username, String gridId, String fileName,
                                        String algorithmCode, String resultPath, String previewPath) {
        if (username == null || gridId == null) return;
        try {
            String userRoot = userDataConfig.getDataRootPath();
            String userCubeDir = "ARD_CUB_GRIDT0_" + username + "_RAW";
            Path gridDir = Paths.get(userRoot, username, userCubeDir, gridId);
            if (!Files.exists(gridDir)) {
                Files.createDirectories(gridDir);
            }
            Path metaPath = gridDir.resolve("metadata.json");

            com.fasterxml.jackson.databind.node.ObjectNode root;
            if (Files.exists(metaPath)) {
                root = (com.fasterxml.jackson.databind.node.ObjectNode) __metaMapper.readTree(metaPath.toFile());
            } else {
                root = __metaMapper.createObjectNode();
                root.put("cube_id", "GRID_CUBE_" + gridId);
                root.put("grid_id", gridId);
                root.put("secretlevel", "public");
                root.put("description", "用户格网元数据");
                root.set("files", __metaMapper.createArrayNode());
                com.fasterxml.jackson.databind.node.ObjectNode stats = __metaMapper.createObjectNode();
                stats.put("total_files", 0);
                stats.put("original_files", 0);
                stats.put("derived_files", 0);
                root.set("statistics", stats);
                root.put("created", OffsetDateTime.now().toString());
            }

            com.fasterxml.jackson.databind.node.ArrayNode filesArr = (com.fasterxml.jackson.databind.node.ArrayNode) root.withArray("files");
            com.fasterxml.jackson.databind.node.ObjectNode newFile = __metaMapper.createObjectNode();
            newFile.put("filename", fileName != null ? fileName : Paths.get(resultPath).getFileName().toString());
            // 根据算法类型填写类型与产品编码
            String fileType = "衍生数据";
            String productType = "03";
            if ("NDVI_ANALYSIS".equalsIgnoreCase(algorithmCode)) {
                fileType = "衍生数据-植被指数";
                productType = "03";
            }
            newFile.put("file_type", fileType);
            newFile.put("imaging_time", OffsetDateTime.now().toString());
            newFile.put("storage_time", OffsetDateTime.now().toString());
            newFile.put("product_type", productType);
            newFile.put("description", fileType);
            newFile.put("season", quarterOrEmptyFromPath(resultPath));
            newFile.put("resolution", "30m");
            newFile.put("location", "用户数据中心");
            // 额外挂上路径
            newFile.put("result_path", normalizeSlashes(resultPath));
            if (previewPath != null) newFile.put("preview_path", normalizeSlashes(previewPath));

            filesArr.add(newFile);

            // 更新统计
            com.fasterxml.jackson.databind.node.ObjectNode stats = (com.fasterxml.jackson.databind.node.ObjectNode) root.with("statistics");
            int total = stats.path("total_files").asInt(0) + 1;
            int derived = stats.path("derived_files").asInt(0) + 1;
            stats.put("total_files", total);
            stats.put("derived_files", derived);
            root.put("updated", OffsetDateTime.now().toString());

            // 写回文件
            __metaMapper.writerWithDefaultPrettyPrinter().writeValue(metaPath.toFile(), root);
            System.out.println("✓ 元数据文件已更新: " + metaPath);
        } catch (Exception e) {
            System.err.println("更新元数据文件异常: " + e.getMessage());
        }
    }

    private String quarterOrEmptyFromPath(String path) {
        try {
            if (path == null) return "";
            String p = path.replace('\\','/');
            // 例如 .../2025q1/...
            int idx = p.indexOf("/202");
            if (idx >= 0) {
                String sub = p.substring(idx+1);
                int slash = sub.indexOf('/');
                if (slash > 0) {
                    String token = sub.substring(0, slash);
                    if (token.toLowerCase().matches("\\d{4}q[1-4]")) return token;
                }
            }
        } catch (Exception ignore) {}
        return "";
    }

    private String normalizeSlashes(String s) {
        return s == null ? null : s.replace('\\','/');
    }
}
