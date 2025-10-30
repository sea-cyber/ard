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
     * 保存分析结果的tif：用户数据根目录/username/ARD_CUB_GRIDT0_username_RAW/grid_id/analysis_type
     */
    public String buildUserAnalysisPath(String username, String gridId, String analysisType) {
        String userCubeName = "ARD_CUB_GRIDT0_" + username + "_RAW";
        return dataRootPath + "/" + username + "/" + userCubeName + "/" + gridId + "/" + analysisType;
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
    
    /**
     * 预览图/浏览图片：用户数据根目录/username/ARD_CUB_GRIDT0_username_VIZ/grid_id/
     */
    public String buildUserVizPath(String username, String gridId) {
        String userCubeName = "ARD_CUB_GRIDT0_" + username + "_VIZ";
        return dataRootPath + "/" + username + "/" + userCubeName + "/" + gridId;
    }
}
