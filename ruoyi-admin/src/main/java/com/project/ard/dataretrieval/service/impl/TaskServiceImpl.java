package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.Task;
import com.project.ard.dataretrieval.mapper.TaskMapper;
import com.project.ard.dataretrieval.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 任务Service业务层处理
 * 
 * @author system
 * @date 2025-01-01
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private TaskMapper taskMapper;
    
    /**
     * 将英文任务类型转换为中文
     * 
     * @param englishType 英文任务类型
     * @return 中文任务类型
     */
    private String convertTaskTypeToChinese(String englishType) {
        switch (englishType) {
            case "cube":
                return "立方体构建";
            case "timeseries":
                return "时序分析";
            case "convert":
                return "格式转换";
            default:
                return englishType; // 如果无法转换，返回原值
        }
    }

    /**
     * 查询任务列表
     * 
     * @param task 任务查询条件
     * @return 任务列表
     */
    @Override
    public List<Task> selectTaskList(Task task) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        
        // 调试信息
        System.out.println("=== 后端任务查询调试信息 ===");
        System.out.println("Task查询参数: " + task);
        System.out.println("TaskType: " + task.getTaskType());
        System.out.println("Status: " + task.getStatus());
        System.out.println("QueryTimeRange: " + task.getQueryTimeRange());
        
        // 任务类型筛选
        if (task.getTaskType() != null && !task.getTaskType().isEmpty()) {
            // 将英文任务类型转换为中文进行查询
            String chineseTaskType = convertTaskTypeToChinese(task.getTaskType());
            queryWrapper.eq("task_type", chineseTaskType);
            System.out.println("添加任务类型筛选条件: task_type = " + task.getTaskType() + " -> " + chineseTaskType);
        } else {
            System.out.println("任务类型筛选条件为空，不添加筛选");
        }
        
        // 任务状态筛选
        if (task.getStatus() != null) {
            queryWrapper.eq("status", task.getStatus());
            System.out.println("添加状态筛选条件: status = " + task.getStatus());
        } else {
            System.out.println("状态筛选条件为空，不添加筛选");
        }
        
        // 创建用户筛选
        if (task.getCreateUser() != null) {
            queryWrapper.eq("create_user", task.getCreateUser());
        }
        
        // 时间范围筛选
        if (task.getQueryTimeRange() != null) {
            Task.TimeRange timeRange = task.getQueryTimeRange();
            if (timeRange.getBeginTime() != null && !timeRange.getBeginTime().isEmpty()) {
                // 使用PostgreSQL的带时区时间戳类型转换进行时间筛选
                queryWrapper.apply("create_time >= {0}::timestamptz", timeRange.getBeginTime());
                System.out.println("添加开始时间筛选: " + timeRange.getBeginTime());
            }
            if (timeRange.getEndTime() != null && !timeRange.getEndTime().isEmpty()) {
                // 使用PostgreSQL的带时区时间戳类型转换进行时间筛选
                queryWrapper.apply("create_time <= {0}::timestamptz", timeRange.getEndTime());
                System.out.println("添加结束时间筛选: " + timeRange.getEndTime());
            }
        }
        
        // 按创建时间降序排列
        queryWrapper.orderByDesc("create_time");
        
        // 打印最终的SQL查询条件
        System.out.println("最终查询条件: " + queryWrapper.getTargetSql());
        
        List<Task> result = taskMapper.selectList(queryWrapper);
        System.out.println("查询结果数量: " + result.size());
        
        return result;
    }

    /**
     * 根据任务ID查询任务详情
     * 
     * @param taskId 任务ID
     * @return 任务详情
     */
    @Override
    public Task selectTaskById(Integer taskId) {
        return taskMapper.selectTaskById(taskId);
    }

    /**
     * 新增任务
     * 
     * @param task 任务信息
     * @return 结果
     */
    @Override
    public int insertTask(Task task) {
        task.setCreateTime(OffsetDateTime.now());
        task.setUpdateTime(OffsetDateTime.now());
        task.setStatus(Task.TaskStatus.PENDING.getCode());
        return taskMapper.insert(task);
    }
    
    /**
     * 修改任务
     * 
     * @param task 任务信息
     * @return 结果
     */
    @Override
    public int updateTask(Task task) {
        task.setUpdateTime(OffsetDateTime.now());
        return taskMapper.updateById(task);
    }

    /**
     * 批量删除任务
     * 
     * @param taskIds 需要删除的任务ID
     * @return 结果
     */
    @Override
    public int deleteTaskByIds(Integer[] taskIds) {
        return taskMapper.deleteBatchIds(Arrays.asList(taskIds));
    }

    /**
     * 删除任务信息
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int deleteTaskById(Integer taskId) {
        return taskMapper.deleteById(taskId);
    }

    /**
     * 取消任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int cancelTask(Integer taskId) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setStatus(Task.TaskStatus.FAILED.getCode());
        task.setUpdateTime(OffsetDateTime.now());
        task.setEndTime(OffsetDateTime.now());
        return taskMapper.updateById(task);
    }

    /**
     * 重新执行任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int retryTask(Integer taskId) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setStatus(Task.TaskStatus.PENDING.getCode());
        task.setUpdateTime(OffsetDateTime.now());
        task.setStartTime(null);
        task.setEndTime(null);
        task.setRunDuration(null);
        return taskMapper.updateById(task);
    }

    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 新状态
     * @return 结果
     */
    @Override
    public int updateTaskStatus(Integer taskId, Integer status) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setStatus(status);
        task.setUpdateTime(OffsetDateTime.now());
        
        // 根据状态设置相应的时间字段
        if (status.equals(Task.TaskStatus.RUNNING.getCode())) {
            task.setStartTime(OffsetDateTime.now());
        } else if (status.equals(Task.TaskStatus.COMPLETED.getCode()) || 
                   status.equals(Task.TaskStatus.FAILED.getCode())) {
            task.setEndTime(OffsetDateTime.now());
        }
        
        return taskMapper.updateById(task);
    }
        
    /**
     * 更新任务进度
     * 
     * @param taskId 任务ID
     * @param progress 进度百分比
     * @return 结果
     */
    @Override
    public int updateTaskProgress(Integer taskId, Integer progress) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setUpdateTime(OffsetDateTime.now());
        
        // 根据进度更新状态
        if (progress >= 100) {
            task.setStatus(Task.TaskStatus.COMPLETED.getCode());
            task.setEndTime(OffsetDateTime.now());
        } else if (progress > 0) {
            task.setStatus(Task.TaskStatus.RUNNING.getCode());
            if (task.getStartTime() == null) {
                task.setStartTime(OffsetDateTime.now());
            }
        }

        return taskMapper.updateById(task);
    }
}
