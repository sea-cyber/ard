package com.project.ard.dataretrieval.controller;

import com.project.ard.dataretrieval.entity.CubeWorkflow;
import com.project.ard.dataretrieval.service.ICubeWorkflowService;
import com.project.ard.dataretrieval.service.WorkflowProcessorManager;
import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;

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
                
                // 开始任务拆分步骤
                cubeTaskStepService.startStep(taskId, 2, "开始任务拆分，分析切片位置分布");
                
                // 构建计算参数
                Map<String, Object> parameters = buildCalculationParameters(request, workflow);
                
                // 获取切片文件路径（修改为新的路径结构）
                List<String> sliceFiles = extractSliceFilePathsForCalculation(selectedSlices, parameters);
                
                // 完成任务拆分步骤
                String processingCenter = (String) parameters.get("processingCenter");
                cubeTaskStepService.completeStep(taskId, 2, "任务拆分完成，处理中心: " + processingCenter);
                
                // 更新进度
                cubeTaskInfoService.updateTaskProgress(taskId, 30);
                
                // 执行计算
                Map<String, Object> result = workflowProcessorManager.processWorkflow(workflow, parameters, sliceFiles);
                
                // 更新进度
                cubeTaskInfoService.updateTaskProgress(taskId, 80);
                
                // 计算完成后保存结果到数据库
                saveWorkflowResult(taskId, workflow, result, parameters);
                
                // 根据计算结果更新任务状态
                if ("success".equals(result.get("status"))) {
                    // 计算成功，更新任务状态为完成
                    cubeTaskInfoService.completeTask(taskId, 1, "计算结果已保存");
                    System.out.println("任务计算完成 - 任务ID: " + taskId);
                } else {
                    // 计算失败，更新任务状态为失败
                    String errorMessage = (String) result.get("message");
                    cubeTaskInfoService.updateTaskStatus(taskId, "failed", 100, errorMessage);
                    System.err.println("任务计算失败 - 任务ID: " + taskId + ", 错误: " + errorMessage);
                }
                
            } catch (Exception e) {
                // 记录错误并更新任务状态为失败
                System.err.println("异步任务执行失败: " + e.getMessage());
                e.printStackTrace();
                cubeTaskInfoService.updateTaskStatus(taskId, "failed", 100, e.getMessage());
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
                
                // 使用参数中的cubeId，如果没有则使用slice中的cubeId
                String targetCubeId = cubeId != null ? cubeId : sliceCubeId;
                
                // 使用切片自己的quarter，如果没有则使用参数中的quarter
                String targetQuarter = sliceQuarter != null ? sliceQuarter : (String) parameters.get("quarter");
                
                if (targetCubeId != null && targetQuarter != null) {
                    // 构建绝对目录路径: D:\GISER\ard\development\cubedata\ARD_CUB_GRIDT0_OFF_RAW\cube_id\quarter\
                    String basePath = "D:\\GISER\\ard\\development\\cubedata\\ARD_CUB_GRIDT0_OFF_RAW";
                    String directoryPath = basePath + "\\" + targetCubeId + "\\" + targetQuarter + "\\";
                    
                    System.out.println("处理切片: " + sliceFileName + ", 立方体: " + targetCubeId + ", 季度: " + targetQuarter);
                    
                    // 根据切片标识符和季度信息，构建正确的波段文件名
                    if (sliceFileName != null && !sliceFileName.isEmpty()) {
                        // 构建红光波段和近红外波段文件名
                        String redBandFile = directoryPath + targetCubeId + "_" + targetQuarter + "_B4.TIF";
                        String nirBandFile = directoryPath + targetCubeId + "_" + targetQuarter + "_B5.TIF";
                        
                        // 检查波段文件是否存在
                        File redFile = new File(redBandFile);
                        File nirFile = new File(nirBandFile);
                        
                        if (redFile.exists() && nirFile.exists()) {
                            // 只添加一个切片标识符到处理列表，而不是两个波段文件
                            // 这样NDVIWorkflowProcessor会为每个切片生成一个NDVI结果
                            uniqueFiles.add(sliceFileName + "|" + targetCubeId + "|" + targetQuarter);
                            System.out.println("找到波段文件: " + redBandFile + ", " + nirBandFile);
                            System.out.println("添加切片标识符: " + sliceFileName);
                        } else {
                            System.out.println("波段文件不存在: " + redBandFile + " 或 " + nirBandFile);
                            System.out.println("跳过不存在的波段文件");
                        }
                    } else {
                        // 如果没有指定文件名，跳过这个切片
                        System.out.println("跳过没有文件名的切片");
                    }
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
                            // 从完整路径中提取文件名
                            String[] pathParts = outputPath.split("\\\\");
                            if (pathParts.length > 0) {
                                fileName = pathParts[pathParts.length - 1];
                            }
                            System.out.println("输出文件路径 " + (i + 1) + ": " + outputPath);
                            System.out.println("提取的文件名 " + (i + 1) + ": " + fileName);
                        }
                        
                        // 创建计算结果对象（用于兼容原有接口）
                        Map<String, Object> calcResult = new HashMap<>();
                        calcResult.put("outputPath", outputPath);
                        calcResult.put("validPixels", 0);
                        calcResult.put("meanValue", 0.0);
                        calcResult.put("minValue", 0.0);
                        calcResult.put("maxValue", 0.0);
                        
                        // 保存结果到数据库
                        boolean saveResult = saveResultToDatabase(taskId, cubeId, algorithmCode, outputPath, fileName, quarter, calcResult);
                        
                        if (saveResult) {
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
}
