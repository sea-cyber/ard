package com.project.ard.dataretrieval.controller;

import com.project.ard.dataretrieval.dto.*;
import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import com.project.common.core.page.TableDataInfo;
import com.project.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 任务管理控制器
 */
@RestController
@RequestMapping("/ard/task")
public class TaskController extends BaseController {
    
    @Autowired
    private com.project.ard.dataretrieval.service.TaskProcessingService taskProcessingService;
    
    @Autowired
    private WorkflowTaskController workflowTaskController;
    
    @Autowired
    private com.project.ard.dataretrieval.service.CubeTaskInfoService cubeTaskInfoService;
    
    @Autowired
    private com.project.ard.dataretrieval.service.CubeTaskStepService cubeTaskStepService;

    /**
     * 创建任务
     */
    @PostMapping("/create")
    public AjaxResult createTask(@RequestBody SimpleTaskCreateRequest request) {
        try {
            logger.info("接收到任务创建请求: {}", request.getTaskName());
            
            // 参数验证
            if (request.getTaskName() == null || request.getTaskName().trim().isEmpty()) {
                return AjaxResult.error("任务名称不能为空");
            }
            
            if (request.getSelectedCubes() == null || request.getSelectedCubes().isEmpty()) {
                return AjaxResult.error("请至少选择一个立方体");
            }
            
            // 获取当前用户ID
            Long currentUserId = null;
            try {
                currentUserId = SecurityUtils.getUserId();
                request.setCreateUserId(currentUserId);
                logger.info("当前用户ID: {}", currentUserId);
            } catch (Exception e) {
                logger.warn("获取当前用户ID失败: {}", e.getMessage());
                // 如果获取用户ID失败，仍然允许创建任务，但记录警告
            }
            
            // 生成任务ID
            String taskId = "TASK_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
            
            // 先创建任务记录
            com.project.ard.dataretrieval.domain.CubeTaskInfo taskInfo = new com.project.ard.dataretrieval.domain.CubeTaskInfo();
            taskInfo.setTaskId(taskId);
            taskInfo.setTaskName(request.getTaskName());
            taskInfo.setTaskDescription(request.getDescription());
            taskInfo.setTaskType(request.getWorkflow() != null ? "WORKFLOW_ANALYSIS" : "DATA_RETRIEVAL");
            taskInfo.setUserId(currentUserId);
            taskInfo.setCreatedBy(SecurityUtils.getUsername());
            taskInfo.setTimeStart(parseDateTime(request.getTimeStart()));
            taskInfo.setTimeEnd(parseDateTime(request.getTimeEnd()));
            taskInfo.setProcessingCenter(request.getProcessingCenter());
            taskInfo.setOutputResolution("30m×30m"); // 默认分辨率
            taskInfo.setOutputFormat("TIF"); // 默认格式
            taskInfo.setWorkflowId(request.getWorkflow());
            taskInfo.setStatus("pending");
            taskInfo.setProgress(0);
            taskInfo.setPriority(5);
            
            // 保存任务记录
            logger.info("开始创建任务记录 - 任务ID: {}, 任务名称: {}", taskId, request.getTaskName());
            boolean taskCreated = cubeTaskInfoService.createTask(taskInfo);
            if (!taskCreated) {
                logger.error("创建任务记录失败 - 任务ID: {}", taskId);
                return AjaxResult.error("创建任务失败");
            }
            
            // 验证任务是否真的创建成功
            com.project.ard.dataretrieval.domain.CubeTaskInfo verifyTask = cubeTaskInfoService.getTaskById(taskId);
            if (verifyTask == null) {
                logger.error("任务创建后验证失败 - 任务ID: {} 在数据库中不存在", taskId);
                return AjaxResult.error("任务创建失败：数据库验证失败");
            }
            
            logger.info("成功创建任务记录 - 任务ID: {}, 任务名称: {}, 验证通过", taskId, request.getTaskName());
            
            // 创建任务的所有步骤
            boolean stepsCreated = cubeTaskStepService.createAllTaskSteps(taskId, request.getProcessingCenter());
            if (!stepsCreated) {
                logger.warn("创建任务步骤失败 - 任务ID: {}", taskId);
            } else {
                logger.info("成功创建任务所有步骤 - 任务ID: {}", taskId);
            }
                
            // 记录任务信息

            logger.info(request.toString());
            logger.info("任务ID: {}", taskId);
            logger.info("创建用户ID: {}", request.getCreateUserId());
            logger.info("任务名称: {}", request.getTaskName());
            logger.info("任务描述: {}", request.getDescription());
            logger.info("时间范围: {} - {}", request.getTimeStart(), request.getTimeEnd());
            logger.info("分析类型: {}", request.getAnalysisType());
            logger.info("处理中心: {}", request.getProcessingCenter());
            logger.info("工作流: {}", request.getWorkflow());
            logger.info("任务类型: {}", request.getTaskType());
            logger.info("是否定时任务: {}", request.getIsScheduled());
            logger.info("选中立方体数量: {}", request.getSelectedCubes().size());
            logger.info("选中切片数量: {}", request.getSelectedSlices() != null ? request.getSelectedSlices().size() : 0);
            
            // 记录立方体信息
            if (request.getSelectedCubes() != null) {
                for (Map<String, Object> cube : request.getSelectedCubes()) {
                    logger.info("立方体 - ID: {}, 名称: {}, 位置: {}", 
                            cube.get("id"), cube.get("cubeName"), cube.get("location"));
                }
            }
            
            // 记录切片信息
            if (request.getSelectedSlices() != null) {
                for (Map<String, Object> slice : request.getSelectedSlices()) {
                    logger.info("切片 - ID: {}, 立方体ID: {}, 文件名: {}, 成像时间: {}", 
                            slice.get("id"), slice.get("cubeId"), slice.get("fileName"), slice.get("imagingTime"));
                }
            }
            
            // TODO: 这里可以添加具体的业务逻辑
            // 1. 保存任务到数据库
            // 2. 启动任务处理流程
            // 3. 发送通知等
            
            // 不再执行文件复制操作，直接进行工作流处理
            Map<String, Object> processed = new HashMap<>();
            processed.put("taskId", taskId);
            
            // 如果指定了工作流，则执行工作流处理
            if (request.getWorkflow() != null && !request.getWorkflow().isEmpty()) {
                logger.info("开始执行工作流处理: {}", request.getWorkflow());
                
                // 开始数据准备步骤
                cubeTaskStepService.startStep(taskId, 1, "开始检索切片数据，任务名称: " + request.getTaskName());
                
                try {
                    // 调用工作流任务控制器
                    Map<String, Object> workflowRequest = new HashMap<>();
                    workflowRequest.put("taskId", taskId); // 传递已创建的任务ID
                    workflowRequest.put("workflow", request.getWorkflow());
                    workflowRequest.put("selectedCubes", request.getSelectedCubes());
                    workflowRequest.put("selectedSlices", request.getSelectedSlices());
                    workflowRequest.put("timeStart", request.getTimeStart());
                    workflowRequest.put("timeEnd", request.getTimeEnd());
                    workflowRequest.put("processingCenter", request.getProcessingCenter());
                    
                    // 调用工作流处理逻辑
                    logger.info("工作流请求参数: {}", workflowRequest);
                    
                    AjaxResult workflowResult = workflowTaskController.createAndExecuteTask(workflowRequest);
                    logger.info("工作流处理结果: {}", workflowResult);
                    
                    if (workflowResult.get("code") != null && workflowResult.get("code").equals(200)) {
                        // 检查工作流处理结果中的实际状态
                        @SuppressWarnings("unchecked")
                        Map<String, Object> data = (Map<String, Object>) workflowResult.get("data");
                        if (data != null) {
                            String status = (String) data.get("status");
                            if ("error".equals(status)) {
                                logger.error("工作流处理失败: {}", data.get("message"));
                            } else {
                                logger.info("工作流处理成功");
                            }
                        } else {
                            logger.info("工作流处理成功");
                        }
                    } else {
                        logger.warn("工作流处理失败: {}", workflowResult.get("msg"));
                    }
                    
                    // 完成数据准备步骤
                    int sliceCount = request.getSelectedSlices() != null ? request.getSelectedSlices().size() : 0;
                    cubeTaskStepService.completeStep(taskId, 1, "数据准备完成，检索到切片数量: " + sliceCount);
                    
                } catch (Exception e) {
                    logger.error("工作流处理失败", e);
                    // 失败数据准备步骤
                    cubeTaskStepService.failStep(taskId, 1, "数据准备失败: " + e.getMessage());
                    // 不中断主流程，只记录错误
                }
            }

            TaskCreateResponse response = new TaskCreateResponse(true, "任务创建成功", taskId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("task", response);
            resp.putAll(processed);
            return AjaxResult.success(resp);
            
        } catch (Exception e) {
            logger.error("任务创建失败", e);
            return AjaxResult.error("任务创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取任务列表
     */
    @GetMapping("/list")
    public TableDataInfo getTaskList(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            logger.info("获取任务列表 - 页码: {}, 页大小: {}", pageNum, pageSize);
            
            // TODO: 实现获取任务列表的逻辑
            // 这里应该从数据库查询任务列表
            
            return getDataTable(null);
        } catch (Exception e) {
            logger.error("获取任务列表失败", e);
            return getDataTable(null);
        }
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public AjaxResult getTaskDetail(@PathVariable String taskId) {
        try {
            logger.info("获取任务详情: {}", taskId);
            
            // TODO: 实现获取任务详情的逻辑
            // 这里应该从数据库查询任务详情
            
            return AjaxResult.success("任务详情获取成功");
        } catch (Exception e) {
            logger.error("获取任务详情失败", e);
            return AjaxResult.error("获取任务详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 启动任务
     */
    @PostMapping("/{taskId}/start")
    public AjaxResult startTask(@PathVariable String taskId) {
        try {
            logger.info("启动任务: {}", taskId);
            
            // TODO: 实现启动任务的逻辑
            // 这里应该启动任务处理流程
            
            return AjaxResult.success("任务启动成功");
        } catch (Exception e) {
            logger.error("启动任务失败", e);
            return AjaxResult.error("启动任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 停止任务
     */
    @PostMapping("/{taskId}/stop")
    public AjaxResult stopTask(@PathVariable String taskId) {
        try {
            logger.info("停止任务: {}", taskId);
            
            // TODO: 实现停止任务的逻辑
            // 这里应该停止任务处理流程
            
            return AjaxResult.success("任务停止成功");
        } catch (Exception e) {
            logger.error("停止任务失败", e);
            return AjaxResult.error("停止任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除任务
     */
    @DeleteMapping("/{taskId}")
    public AjaxResult deleteTask(@PathVariable String taskId) {
        try {
            logger.info("删除任务: {}", taskId);
            
            // TODO: 实现删除任务的逻辑
            // 这里应该从数据库删除任务
            
            return AjaxResult.success("任务删除成功");
        } catch (Exception e) {
            logger.error("删除任务失败", e);
            return AjaxResult.error("删除任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 解析日期时间字符串
     * 支持多种格式：2024/10/21, 2024-10-21, 2024-10-21T00:00:00+08:00 等
     */
    private java.time.OffsetDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 尝试直接解析ISO格式
            return java.time.OffsetDateTime.parse(dateTimeStr);
        } catch (java.time.format.DateTimeParseException e1) {
            try {
                // 尝试解析 2024/10/21 格式
                if (dateTimeStr.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
                    java.time.LocalDate date = java.time.LocalDate.parse(dateTimeStr.replace("/", "-"));
                    return date.atStartOfDay().atOffset(java.time.ZoneOffset.of("+08:00"));
                }
                
                // 尝试解析 2024-10-21 格式
                if (dateTimeStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                    java.time.LocalDate date = java.time.LocalDate.parse(dateTimeStr);
                    return date.atStartOfDay().atOffset(java.time.ZoneOffset.of("+08:00"));
                }
                
                // 尝试解析 2024-10-21 12:00:00 格式
                if (dateTimeStr.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")) {
                    java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(dateTimeStr, 
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    return dateTime.atOffset(java.time.ZoneOffset.of("+08:00"));
                }
                
                // 尝试解析 2024/10/21 12:00:00 格式
                if (dateTimeStr.matches("\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")) {
                    java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(dateTimeStr, 
                        java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
                    return dateTime.atOffset(java.time.ZoneOffset.of("+08:00"));
                }
                
                logger.warn("无法解析日期时间格式: {}", dateTimeStr);
                return null;
                
            } catch (Exception e2) {
                logger.error("解析日期时间失败: {}, 错误: {}", dateTimeStr, e2.getMessage());
                return null;
            }
        }
    }
}
