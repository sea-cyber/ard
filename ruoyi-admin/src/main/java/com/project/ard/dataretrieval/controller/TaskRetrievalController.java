package com.project.ard.dataretrieval.controller;

import com.project.ard.dataretrieval.domain.Task;
import com.project.ard.dataretrieval.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务检索Controller
 * 
 * @author system
 * @date 2025-01-01
 */
@RestController
@RequestMapping("/ard/dataretrieval/task")
public class TaskRetrievalController {

    @Autowired
    private TaskService taskService;

    /**
     * 查询任务列表
     * 
     * @param task 查询条件
     * @return 任务列表
     */
    @PostMapping("/search")
    public List<Task> searchTasks(@RequestBody Task task) {
        return taskService.selectTaskList(task);
    }

    /**
     * 获取任务详情
     * 
     * @param taskId 任务ID
     * @return 任务详情
     */
    @GetMapping("/detail/{taskId}")
    public Task getTaskDetail(@PathVariable Integer taskId) {
        return taskService.selectTaskById(taskId);
    }

    /**
     * 获取任务进度
     * 
     * @param taskId 任务ID
     * @return 任务进度信息
     */
    @GetMapping("/progress/{taskId}")
    public Task getTaskProgress(@PathVariable Integer taskId) {
        return taskService.selectTaskById(taskId);
    }

    /**
     * 取消任务
     * 
     * @param taskId 任务ID
     * @return 操作结果
     */
    @PostMapping("/cancel/{taskId}")
    public String cancelTask(@PathVariable Integer taskId) {
        int result = taskService.cancelTask(taskId);
        return result > 0 ? "任务取消成功" : "任务取消失败";
    }

    /**
     * 删除任务
     * 
     * @param taskId 任务ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{taskId}")
    public String deleteTask(@PathVariable Integer taskId) {
        int result = taskService.deleteTaskById(taskId);
        return result > 0 ? "任务删除成功" : "任务删除失败";
    }

    /**
     * 重新执行任务
     * 
     * @param taskId 任务ID
     * @return 操作结果
     */
    @PostMapping("/retry/{taskId}")
    public String retryTask(@PathVariable Integer taskId) {
        int result = taskService.retryTask(taskId);
        return result > 0 ? "任务重新执行成功" : "任务重新执行失败";
    }

    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 新状态
     * @return 操作结果
     */
    @PostMapping("/status/{taskId}")
    public String updateTaskStatus(@PathVariable Integer taskId, @RequestParam Integer status) {
        int result = taskService.updateTaskStatus(taskId, status);
        return result > 0 ? "任务状态更新成功" : "任务状态更新失败";
    }

    /**
     * 更新任务进度
     * 
     * @param taskId 任务ID
     * @param progress 进度百分比
     * @return 操作结果
     */
    @PostMapping("/progress/{taskId}")
    public String updateTaskProgress(@PathVariable Integer taskId, @RequestParam Integer progress) {
        int result = taskService.updateTaskProgress(taskId, progress);
        return result > 0 ? "任务进度更新成功" : "任务进度更新失败";
    }
}
