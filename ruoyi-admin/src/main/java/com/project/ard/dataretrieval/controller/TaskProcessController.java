package com.project.ard.dataretrieval.controller;

import com.project.ard.dataretrieval.domain.TaskProcess;
import com.project.ard.dataretrieval.service.TaskProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务进度Controller
 * 
 * @author system
 * @date 2024-01-20
 */
@RestController
@RequestMapping("/ard/dataretrieval/taskprocess")
public class TaskProcessController {

    @Autowired
    private TaskProcessService taskProcessService;

    /**
     * 根据任务ID查询任务进度列表
     * 
     * @param taskId 任务ID
     * @return 任务进度列表
     */
    @GetMapping("/list/{taskId}")
    public List<TaskProcess> getTaskProcessByTaskId(@PathVariable Integer taskId) {
        return taskProcessService.selectTaskProcessByTaskId(taskId);
    }

    /**
     * 查询任务进度列表
     * 
     * @param taskProcess 任务进度查询条件
     * @return 任务进度列表
     */
    @PostMapping("/search")
    public List<TaskProcess> searchTaskProcess(@RequestBody TaskProcess taskProcess) {
        return taskProcessService.selectTaskProcessList(taskProcess);
    }

    /**
     * 新增任务进度
     * 
     * @param taskProcess 任务进度
     * @return 结果
     */
    @PostMapping("/add")
    public int addTaskProcess(@RequestBody TaskProcess taskProcess) {
        return taskProcessService.insertTaskProcess(taskProcess);
    }

    /**
     * 修改任务进度
     * 
     * @param taskProcess 任务进度
     * @return 结果
     */
    @PutMapping("/update")
    public int updateTaskProcess(@RequestBody TaskProcess taskProcess) {
        return taskProcessService.updateTaskProcess(taskProcess);
    }

    /**
     * 更新任务进度状态
     * 
     * @param processId 进程ID
     * @param stepStatus 步骤状态
     * @return 结果
     */
    @PutMapping("/updateStatus/{processId}")
    public int updateTaskProcessStatus(@PathVariable Integer processId, @RequestParam String stepStatus) {
        return taskProcessService.updateTaskProcessStatus(processId, stepStatus);
    }

    /**
     * 删除任务进度
     * 
     * @param processIds 进程ID数组
     * @return 结果
     */
    @DeleteMapping("/delete/{processIds}")
    public int deleteTaskProcess(@PathVariable Integer[] processIds) {
        return taskProcessService.deleteTaskProcessByIds(processIds);
    }

    /**
     * 根据任务ID删除任务进度
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @DeleteMapping("/deleteByTaskId/{taskId}")
    public int deleteTaskProcessByTaskId(@PathVariable Integer taskId) {
        return taskProcessService.deleteTaskProcessByTaskId(taskId);
    }

    /**
     * 批量插入任务进度
     * 
     * @param taskProcessList 任务进度列表
     * @return 结果
     */
    @PostMapping("/batchAdd")
    public int batchAddTaskProcess(@RequestBody List<TaskProcess> taskProcessList) {
        return taskProcessService.batchInsertTaskProcess(taskProcessList);
    }
}
