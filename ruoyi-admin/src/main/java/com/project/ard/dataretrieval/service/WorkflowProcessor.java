package com.project.ard.dataretrieval.service;

import com.project.ard.dataretrieval.entity.CubeWorkflow;
import java.util.List;
import java.util.Map;

/**
 * 工作流处理器接口
 * 用于处理不同类型的工作流计算任务
 */
public interface WorkflowProcessor {
    
    /**
     * 获取处理器支持的工作流类型
     * @return 支持的工作流类型标识
     */
    String getSupportedWorkflowType();
    
    /**
     * 处理工作流计算任务
     * @param workflow 工作流信息
     * @param parameters 计算参数
     * @param sliceFiles 切片文件路径列表
     * @return 计算结果
     */
    Map<String, Object> processWorkflow(CubeWorkflow workflow, Map<String, Object> parameters, List<String> sliceFiles);
    
    /**
     * 验证工作流参数是否有效
     * @param workflow 工作流信息
     * @param parameters 计算参数
     * @return 验证结果
     */
    boolean validateParameters(CubeWorkflow workflow, Map<String, Object> parameters);
}

