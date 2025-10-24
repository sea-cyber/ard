package com.project.ard.dataretrieval.controller;

import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import com.project.ard.dataretrieval.domain.CubeTaskStep;
import com.project.ard.dataretrieval.service.CubeTaskStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 立方体任务步骤控制器
 * 
 * @author project
 */
@RestController
@RequestMapping("/ard/task-step")
public class CubeTaskStepController extends BaseController {
    
    @Autowired
    private CubeTaskStepService cubeTaskStepService;
    
    /**
     * 获取任务的所有步骤
     */
    @GetMapping("/{taskId}/steps")
    public AjaxResult getStepsByTaskId(@PathVariable String taskId) {
        try {
            System.out.println("=== CubeTaskStepController.getStepsByTaskId 被调用 ===");
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
    
    /**
     * 开始步骤
     */
    @PostMapping("/{taskId}/start/{stepOrder}")
    public AjaxResult startStep(@PathVariable String taskId, 
                               @PathVariable Integer stepOrder,
                               @RequestParam(required = false) String stepDesc) {
        try {
            boolean result = cubeTaskStepService.startStep(taskId, stepOrder, stepDesc);
            if (result) {
                return AjaxResult.success("步骤开始成功");
            } else {
                return AjaxResult.error("步骤开始失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("步骤开始失败：" + e.getMessage());
        }
    }
    
    /**
     * 完成步骤
     */
    @PostMapping("/{taskId}/complete/{stepOrder}")
    public AjaxResult completeStep(@PathVariable String taskId, 
                                 @PathVariable Integer stepOrder,
                                 @RequestParam(required = false) String stepDesc) {
        try {
            boolean result = cubeTaskStepService.completeStep(taskId, stepOrder, stepDesc);
            if (result) {
                return AjaxResult.success("步骤完成成功");
            } else {
                return AjaxResult.error("步骤完成失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("步骤完成失败：" + e.getMessage());
        }
    }
}
