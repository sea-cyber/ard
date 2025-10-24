package com.project.ard.dataretrieval.service;

import com.project.ard.dataretrieval.domain.CubeTaskStep;

import java.util.List;

/**
 * 立方体任务步骤服务接口
 * 
 * @author project
 */
public interface CubeTaskStepService {
    
    /**
     * 创建任务步骤
     * 
     * @param taskStep 任务步骤信息
     * @return 创建结果
     */
    boolean createTaskStep(CubeTaskStep taskStep);
    
    /**
     * 根据任务ID查询步骤列表
     * 
     * @param taskId 任务ID
     * @return 步骤列表
     */
    List<CubeTaskStep> getStepsByTaskId(String taskId);
    
    /**
     * 根据任务ID和步骤顺序查询步骤
     * 
     * @param taskId 任务ID
     * @param stepOrder 步骤顺序
     * @return 步骤信息
     */
    CubeTaskStep getStepByTaskIdAndOrder(String taskId, Integer stepOrder);
    
    /**
     * 更新步骤状态为处理中
     * 
     * @param taskId 任务ID
     * @param stepOrder 步骤顺序
     * @param stepDesc 步骤说明
     * @return 更新结果
     */
    boolean startStep(String taskId, Integer stepOrder, String stepDesc);
    
    /**
     * 完成步骤
     * 
     * @param taskId 任务ID
     * @param stepOrder 步骤顺序
     * @param stepDesc 步骤说明
     * @return 更新结果
     */
    boolean completeStep(String taskId, Integer stepOrder, String stepDesc);
    
    /**
     * 失败步骤
     * 
     * @param taskId 任务ID
     * @param stepOrder 步骤顺序
     * @param errorDetails 错误详情
     * @return 更新结果
     */
    boolean failStep(String taskId, Integer stepOrder, String errorDetails);
    
    /**
     * 创建任务的所有步骤
     * 
     * @param taskId 任务ID
     * @param processingCenter 执行中心
     * @return 创建结果
     */
    boolean createAllTaskSteps(String taskId, String processingCenter);
}

