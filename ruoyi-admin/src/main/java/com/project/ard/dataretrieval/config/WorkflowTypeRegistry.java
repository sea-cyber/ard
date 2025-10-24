package com.project.ard.dataretrieval.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流类型注册表
 * 管理算法代码到处理器类型的映射关系
 */
@Component
@ConfigurationProperties(prefix = "workflow.types")
public class WorkflowTypeRegistry {
    
    /**
     * 算法代码到处理器类型的映射
     * key: 算法代码 (algorithm_code)
     * value: 处理器类型 (processor_type)
     */
    private Map<String, String> mappings = new HashMap<>();
    
    public WorkflowTypeRegistry() {
        // 初始化默认映射
        initDefaultMappings();
    }
    
    /**
     * 初始化默认映射关系
     */
    private void initDefaultMappings() {
        mappings.put("workflow_20231026_101522", "ndvi_analysis");
        mappings.put("workflow_20231027_143045", "land_cover_classification");
        mappings.put("workflow_20241119_160000", "change_detection");
        mappings.put("workflow_20250820_110000", "object_detection");
    }
    
    /**
     * 根据算法代码获取处理器类型
     * @param algorithmCode 算法代码
     * @return 处理器类型，如果未找到则返回算法代码本身
     */
    public String getProcessorType(String algorithmCode) {
        return mappings.getOrDefault(algorithmCode, algorithmCode);
    }
    
    /**
     * 注册新的映射关系
     * @param algorithmCode 算法代码
     * @param processorType 处理器类型
     */
    public void registerMapping(String algorithmCode, String processorType) {
        mappings.put(algorithmCode, processorType);
    }
    
    /**
     * 获取所有映射关系
     * @return 映射关系Map
     */
    public Map<String, String> getAllMappings() {
        return new HashMap<>(mappings);
    }
    
    // Getter and Setter for Spring Boot configuration
    public Map<String, String> getMappings() {
        return mappings;
    }
    
    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }
}

