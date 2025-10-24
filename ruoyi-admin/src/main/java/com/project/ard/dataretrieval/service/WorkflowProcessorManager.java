package com.project.ard.dataretrieval.service;

import com.project.ard.dataretrieval.entity.CubeWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 工作流处理器管理器
 * 负责根据工作流类型匹配对应的处理器
 */
@Service
public class WorkflowProcessorManager {
    
    @Autowired
    private List<WorkflowProcessor> processors;
    
    /**
     * 根据工作流信息获取对应的处理器
     * @param workflow 工作流信息
     * @return 匹配的处理器，如果没有找到则返回null
     */
    public WorkflowProcessor getProcessor(CubeWorkflow workflow) {
        if (workflow == null) {
            return null;
        }
        
        String workflowId = workflow.getWorkflowId();
        String algorithmCode = workflow.getAlgorithmCode();
        
        // 首先尝试根据算法代码匹配
        if (algorithmCode != null) {
            for (WorkflowProcessor processor : processors) {
                String supportedType = processor.getSupportedWorkflowType();
                if (algorithmCode.equals(supportedType)) {
                    return processor;
                }
            }
        }
        
        // 如果算法代码匹配失败，尝试根据工作流ID匹配
        for (WorkflowProcessor processor : processors) {
            String supportedType = processor.getSupportedWorkflowType();
            if (workflowId.equals(supportedType)) {
                return processor;
            }
        }
        
        return null;
    }
    
    /**
     * 处理工作流任务
     * @param workflow 工作流信息
     * @param parameters 计算参数
     * @param sliceFiles 切片文件路径列表
     * @return 处理结果
     */
    public Map<String, Object> processWorkflow(CubeWorkflow workflow, Map<String, Object> parameters, List<String> sliceFiles) {
        WorkflowProcessor processor = getProcessor(workflow);
        
        if (processor == null) {
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("status", "error");
            errorResult.put("message", "未找到支持的工作流处理器: " + workflow.getWorkflowId());
            return errorResult;
        }
        
        // 验证参数
        if (!processor.validateParameters(workflow, parameters)) {
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("status", "error");
            errorResult.put("message", "工作流参数验证失败");
            return errorResult;
        }
        
        // 执行处理
        return processor.processWorkflow(workflow, parameters, sliceFiles);
    }
}
