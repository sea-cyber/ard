package com.project.ard.dataretrieval.service;

import com.project.ard.dataretrieval.domain.CubeTaskInfo;

import java.util.List;

/**
 * 立方体任务信息服务接口
 * 
 * @author project
 */
public interface CubeTaskInfoService {
    
    /**
     * 创建新任务
     * 
     * @param taskInfo 任务信息
     * @return 创建结果
     */
    boolean createTask(CubeTaskInfo taskInfo);
    
    /**
     * 根据任务ID查询任务信息
     * 
     * @param taskId 任务ID
     * @return 任务信息
     */
    CubeTaskInfo getTaskById(String taskId);
    
    /**
     * 根据用户ID查询任务列表
     * 
     * @param userId 用户ID
     * @return 任务列表
     */
    List<CubeTaskInfo> getTasksByUserId(Long userId);
    
    /**
     * 根据任务状态查询任务列表
     * 
     * @param status 任务状态
     * @return 任务列表
     */
    List<CubeTaskInfo> getTasksByStatus(String status);
    
    /**
     * 根据用户ID和状态查询任务列表
     * 
     * @param userId 用户ID
     * @param status 任务状态
     * @return 任务列表
     */
    List<CubeTaskInfo> getTasksByUserIdAndStatus(Long userId, String status);
    
    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 新状态
     * @param progress 进度
     * @param errorMessage 错误信息
     * @return 更新结果
     */
    boolean updateTaskStatus(String taskId, String status, Integer progress, String errorMessage);
    
    /**
     * 更新任务进度
     * 
     * @param taskId 任务ID
     * @param progress 进度
     * @return 更新结果
     */
    boolean updateTaskProgress(String taskId, Integer progress);
    
    /**
     * 完成任务
     * 
     * @param taskId 任务ID
     * @param resultCount 结果数量
     * @param resultDirectory 结果目录
     * @return 更新结果
     */
    boolean completeTask(String taskId, Integer resultCount, String resultDirectory);
    
    /**
     * 删除任务
     * 
     * @param taskId 任务ID
     * @return 删除结果
     */
    boolean deleteTask(String taskId);
}

