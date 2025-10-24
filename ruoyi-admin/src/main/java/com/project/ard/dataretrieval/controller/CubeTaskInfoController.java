package com.project.ard.dataretrieval.controller;

import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import com.project.common.core.page.TableDataInfo;
import com.project.common.utils.SecurityUtils;
import com.project.ard.dataretrieval.domain.CubeTaskInfo;
import com.project.ard.dataretrieval.domain.CubeResultSliceInfo;
import com.project.ard.dataretrieval.domain.CubeTaskStep;
import com.project.ard.dataretrieval.service.CubeTaskInfoService;
import com.project.ard.dataretrieval.service.CubeResultSliceInfoService;
import com.project.ard.dataretrieval.service.CubeTaskStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 立方体任务信息控制器
 * 
 * @author project
 */
@RestController
@RequestMapping("/ard/task-info")
public class CubeTaskInfoController extends BaseController {
    
    @Autowired
    private CubeTaskInfoService cubeTaskInfoService;
    
    @Autowired
    private CubeResultSliceInfoService cubeResultSliceInfoService;
    
    @Autowired
    private CubeTaskStepService cubeTaskStepService;
    
    /**
     * 获取当前用户的任务列表
     */
    @GetMapping("/list")
    public TableDataInfo list(CubeTaskInfo taskInfo) {
        startPage();
        
        // 获取当前用户ID
        Long userId = SecurityUtils.getUserId();
        taskInfo.setUserId(userId);
        
        List<CubeTaskInfo> list = cubeTaskInfoService.getTasksByUserId(userId);
        return getDataTable(list);
    }
    
    /**
     * 获取当前用户成功完成的任务列表（用于应用数据集）
     */
    @GetMapping("/completed")
    public AjaxResult getCompletedTasks() {
        try {
            // 获取当前用户ID
            Long userId = SecurityUtils.getUserId();
            
            // 查询成功完成的任务
            List<CubeTaskInfo> completedTasks = cubeTaskInfoService.getTasksByUserIdAndStatus(userId, "completed");
            
            return AjaxResult.success(completedTasks);
        } catch (Exception e) {
            logger.error("获取完成任务列表失败", e);
            return AjaxResult.error("获取任务列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") String taskId) {
        try {
            CubeTaskInfo taskInfo = cubeTaskInfoService.getTaskById(taskId);
            if (taskInfo == null) {
                return AjaxResult.error("任务不存在");
            }
            
            // 检查是否为当前用户的任务
            Long currentUserId = SecurityUtils.getUserId();
            if (!currentUserId.equals(taskInfo.getUserId())) {
                return AjaxResult.error("无权限访问该任务");
            }
            
            return AjaxResult.success(taskInfo);
        } catch (Exception e) {
            logger.error("获取任务详情失败", e);
            return AjaxResult.error("获取任务详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取任务状态统计
     */
    @GetMapping("/status-stats")
    public AjaxResult getTaskStatusStats() {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 获取各种状态的任务数量
            List<CubeTaskInfo> pendingTasks = cubeTaskInfoService.getTasksByUserIdAndStatus(userId, "pending");
            List<CubeTaskInfo> runningTasks = cubeTaskInfoService.getTasksByUserIdAndStatus(userId, "running");
            List<CubeTaskInfo> completedTasks = cubeTaskInfoService.getTasksByUserIdAndStatus(userId, "completed");
            List<CubeTaskInfo> failedTasks = cubeTaskInfoService.getTasksByUserIdAndStatus(userId, "failed");
            
            // 构建统计结果
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("pending", pendingTasks.size());
            stats.put("running", runningTasks.size());
            stats.put("completed", completedTasks.size());
            stats.put("failed", failedTasks.size());
            stats.put("total", pendingTasks.size() + runningTasks.size() + completedTasks.size() + failedTasks.size());
            
            return AjaxResult.success(stats);
        } catch (Exception e) {
            logger.error("获取任务状态统计失败", e);
            return AjaxResult.error("获取统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除任务
     */
    @DeleteMapping("/{taskId}")
    public AjaxResult remove(@PathVariable("taskId") String taskId) {
        try {
            // 先检查任务是否存在且属于当前用户
            CubeTaskInfo taskInfo = cubeTaskInfoService.getTaskById(taskId);
            if (taskInfo == null) {
                return AjaxResult.error("任务不存在");
            }
            
            Long currentUserId = SecurityUtils.getUserId();
            if (!currentUserId.equals(taskInfo.getUserId())) {
                return AjaxResult.error("无权限删除该任务");
            }
            
            // 删除任务
            boolean result = cubeTaskInfoService.deleteTask(taskId);
            if (result) {
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (Exception e) {
            logger.error("删除任务失败", e);
            return AjaxResult.error("删除任务失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取任务的结果切片列表
     */
    @GetMapping("/{taskId}/result-slices")
    public AjaxResult getTaskResultSlices(@PathVariable String taskId) {
        try {
            // 获取当前用户ID
            Long currentUserId = SecurityUtils.getUserId();
            
            // 验证任务是否存在且属于当前用户
            CubeTaskInfo taskInfo = cubeTaskInfoService.getTaskById(taskId);
            if (taskInfo == null) {
                return AjaxResult.error("任务不存在");
            }
            
            if (!currentUserId.equals(taskInfo.getUserId())) {
                return AjaxResult.error("无权限查看该任务的结果切片");
            }
            
            // 根据任务ID查询结果切片
            List<CubeResultSliceInfo> resultSlices = cubeResultSliceInfoService.getResultSliceInfoByTaskId(taskId);
            
            return AjaxResult.success(resultSlices);
        } catch (Exception e) {
            logger.error("获取任务结果切片失败", e);
            return AjaxResult.error("获取任务结果切片失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取任务步骤列表（临时方法）
     */
    @GetMapping("/{taskId}/steps")
    public AjaxResult getTaskSteps(@PathVariable String taskId) {
        try {
            System.out.println("=== CubeTaskInfoController.getTaskSteps 被调用 ===");
            System.out.println("任务ID: " + taskId);
            
            List<CubeTaskStep> steps = cubeTaskStepService.getStepsByTaskId(taskId);
            System.out.println("查询到的步骤数量: " + (steps != null ? steps.size() : 0));
            
            return AjaxResult.success(steps);
        } catch (Exception e) {
            System.err.println("获取任务步骤失败: " + e.getMessage());
            e.printStackTrace();
            return AjaxResult.error("获取任务步骤失败：" + e.getMessage());
        }
    }
}
