package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.Task;

import java.util.List;

/**
 * 任务Service接口
 * 
 * @author system
 * @date 2025-01-01
 */
public interface TaskService extends IService<Task> {

    /**
     * 查询任务列表
     * 
     * @param task 任务查询条件
     * @return 任务列表
     */
    List<Task> selectTaskList(Task task);

    /**
     * 根据任务ID查询任务详情
     * 
     * @param taskId 任务ID
     * @return 任务详情
     */
    Task selectTaskById(Integer taskId);

    /**
     * 新增任务
     * 
     * @param task 任务信息
     * @return 结果
     */
    int insertTask(Task task);

    /**
     * 修改任务
     * 
     * @param task 任务信息
     * @return 结果
     */
    int updateTask(Task task);

    /**
     * 批量删除任务
     * 
     * @param taskIds 需要删除的任务ID
     * @return 结果
     */
    int deleteTaskByIds(Integer[] taskIds);

    /**
     * 删除任务信息
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    int deleteTaskById(Integer taskId);

    /**
     * 取消任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    int cancelTask(Integer taskId);

    /**
     * 重新执行任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    int retryTask(Integer taskId);

    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 新状态
     * @return 结果
     */
    int updateTaskStatus(Integer taskId, Integer status);

    /**
     * 更新任务进度
     * 
     * @param taskId 任务ID
     * @param progress 进度百分比
     * @return 结果
     */
    int updateTaskProgress(Integer taskId, Integer progress);
}

