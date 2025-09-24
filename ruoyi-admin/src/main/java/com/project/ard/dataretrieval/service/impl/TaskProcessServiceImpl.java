package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.ard.dataretrieval.domain.TaskProcess;
import com.project.ard.dataretrieval.mapper.TaskProcessMapper;
import com.project.ard.dataretrieval.service.TaskProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 任务进度Service业务层处理
 * 
 * @author system
 * @date 2024-01-20
 */
@Service
public class TaskProcessServiceImpl implements TaskProcessService {

    @Autowired
    private TaskProcessMapper taskProcessMapper;

    /**
     * 查询任务进度列表
     * 
     * @param taskProcess 任务进度
     * @return 任务进度
     */
    @Override
    public List<TaskProcess> selectTaskProcessList(TaskProcess taskProcess) {
        QueryWrapper<TaskProcess> queryWrapper = new QueryWrapper<>();
        
        if (taskProcess.getTaskId() != null) {
            queryWrapper.eq("task_id", taskProcess.getTaskId());
        }
        if (taskProcess.getStepStatus() != null && !taskProcess.getStepStatus().isEmpty()) {
            queryWrapper.eq("step_status", taskProcess.getStepStatus());
        }
        if (taskProcess.getStepName() != null && !taskProcess.getStepName().isEmpty()) {
            queryWrapper.like("step_name", taskProcess.getStepName());
        }
        
        queryWrapper.orderByAsc("process_id");
        return taskProcessMapper.selectList(queryWrapper);
    }

    /**
     * 根据任务ID查询任务进度列表
     * 
     * @param taskId 任务ID
     * @return 任务进度列表
     */
    @Override
    public List<TaskProcess> selectTaskProcessByTaskId(Integer taskId) {
        return taskProcessMapper.selectTaskProcessByTaskId(taskId);
    }

    /**
     * 新增任务进度
     * 
     * @param taskProcess 任务进度
     * @return 结果
     */
    @Override
    public int insertTaskProcess(TaskProcess taskProcess) {
        return taskProcessMapper.insert(taskProcess);
    }

    /**
     * 修改任务进度
     * 
     * @param taskProcess 任务进度
     * @return 结果
     */
    @Override
    public int updateTaskProcess(TaskProcess taskProcess) {
        return taskProcessMapper.updateById(taskProcess);
    }

    /**
     * 批量删除任务进度
     * 
     * @param processIds 需要删除的任务进度主键集合
     * @return 结果
     */
    @Override
    public int deleteTaskProcessByIds(Integer[] processIds) {
        return taskProcessMapper.deleteBatchIds(Arrays.asList(processIds));
    }

    /**
     * 删除任务进度信息
     * 
     * @param processId 任务进度主键
     * @return 结果
     */
    @Override
    public int deleteTaskProcessById(Integer processId) {
        return taskProcessMapper.deleteById(processId);
    }

    /**
     * 根据任务ID删除任务进度
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int deleteTaskProcessByTaskId(Integer taskId) {
        QueryWrapper<TaskProcess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        return taskProcessMapper.delete(queryWrapper);
    }

    /**
     * 更新任务进度状态
     * 
     * @param processId 进程ID
     * @param stepStatus 步骤状态
     * @return 结果
     */
    @Override
    public int updateTaskProcessStatus(Integer processId, String stepStatus) {
        TaskProcess taskProcess = new TaskProcess();
        taskProcess.setProcessId(processId);
        taskProcess.setStepStatus(stepStatus);
        
        // 如果状态是进行中，设置开始时间
        if ("进行中".equals(stepStatus)) {
            taskProcess.setStartTime(LocalDate.now());
        }
        // 如果状态是已完成或失败，设置结束时间
        else if ("已完成".equals(stepStatus) || "失败".equals(stepStatus)) {
            taskProcess.setEndTime(LocalDate.now());
        }
        
        return taskProcessMapper.updateById(taskProcess);
    }

    /**
     * 批量插入任务进度
     * 
     * @param taskProcessList 任务进度列表
     * @return 结果
     */
    @Override
    public int batchInsertTaskProcess(List<TaskProcess> taskProcessList) {
        int count = 0;
        for (TaskProcess taskProcess : taskProcessList) {
            count += taskProcessMapper.insert(taskProcess);
        }
        return count;
    }
}
