package com.project.ard.dataretrieval.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 用户数据配置类
 * 用于管理用户分析结果的存储路径
 * 
 * @author project
 */
@Component
@ConfigurationProperties(prefix = "ard.user")
public class UserDataConfig {
    
    /**
     * 用户数据根目录
     */
    private String dataRootPath = "D:/GISER/ard/development/userdata";
    
    public String getDataRootPath() {
        return dataRootPath;
    }
    
    public void setDataRootPath(String dataRootPath) {
        this.dataRootPath = dataRootPath;
    }
    
    /**
     * 构建用户分析结果存储路径
     * 格式：用户数据根目录/username/grid_id_RAW/quarter/analysis_type
     * 
     * @param username 用户名
     * @param gridId 网格ID
     * @param quarter 季度
     * @param analysisType 分析类型
     * @return 完整的存储路径
     */
    public String buildUserAnalysisPath(String username, String gridId, String quarter, String analysisType) {
        String rawGridId = gridId + "_RAW";
        return dataRootPath + "/" + username + "/" + rawGridId + "/" + quarter + "/" + analysisType;
    }
    
    /**
     * 构建用户数据根目录下的用户目录路径
     * 格式：用户数据根目录/username/ARD_CUB_GRIDT0_username_RAW
     * 
     * @param username 用户名
     * @return 用户目录路径
     */
    public String buildUserCubePath(String username) {
        String userCubeName = "ARD_CUB_GRIDT0_" + username + "_RAW";
        return dataRootPath + "/" + username + "/" + userCubeName;
    }
}
