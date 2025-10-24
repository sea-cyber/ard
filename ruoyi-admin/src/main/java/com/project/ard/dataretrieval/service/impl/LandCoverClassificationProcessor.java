package com.project.ard.dataretrieval.service.impl;

import com.project.ard.dataretrieval.annotation.WorkflowType;
import com.project.ard.dataretrieval.entity.CubeWorkflow;
import com.project.ard.dataretrieval.service.WorkflowProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 土地覆盖分类工作流处理器
 */
@Component
@WorkflowType(value = "land_cover_classification", description = "土地覆盖分类模型训练")
public class LandCoverClassificationProcessor implements WorkflowProcessor {
    
    @Override
    public String getSupportedWorkflowType() {
        return getClass().getAnnotation(WorkflowType.class).value();
    }
    
    @Override
    public Map<String, Object> processWorkflow(CubeWorkflow workflow, Map<String, Object> parameters, List<String> sliceFiles) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 土地覆盖分类计算逻辑
            result.put("status", "success");
            result.put("message", "土地覆盖分类模型训练完成");
            result.put("workflowType", getSupportedWorkflowType());
            result.put("workflowName", workflow.getWorkflowName());
            result.put("processedFiles", sliceFiles.size());
            
            // 模拟分类结果
            Map<String, Object> classificationResult = new HashMap<>();
            classificationResult.put("modelAccuracy", 0.92);
            classificationResult.put("trainingSamples", 1000);
            classificationResult.put("outputModelPath", "/output/land_cover_model.pkl");
            
            result.put("calculationResult", classificationResult);
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "土地覆盖分类训练失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public boolean validateParameters(CubeWorkflow workflow, Map<String, Object> parameters) {
        if (parameters == null) {
            return false;
        }
        
        // 验证土地覆盖分类所需的参数
        return parameters.containsKey("trainingData") && parameters.containsKey("labelData");
    }
}

