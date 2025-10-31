package com.project.ard.dataretrieval.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 预览图配置类
 * 用于管理原始数据切片的预览图存储路径
 * 
 * @author project
 */
@Component
@ConfigurationProperties(prefix = "ard.viz")
public class VizConfig {
    
    /**
     * 预览图根存储目录
     */
    private String rootPath = "D:/GISER/ard/development/cubedata/ARD_CUB_GRIDT0_VIZ";
    
    public String getRootPath() {
        return rootPath;
    }
    
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
    
    /**
     * 构建原始数据切片的预览图路径
     * 格式：预览图根目录/cubeId/文件名
     * 
     * @param cubeId 立方体ID
     * @param fileName 文件名
     * @return 预览图完整路径
     */
    public String buildOriginalSliceVizPath(String cubeId, String fileName) {
        if (cubeId == null || fileName == null) {
            return null;
        }
        // 处理路径分隔符，统一使用/
        String normalizedRootPath = rootPath.replace('\\', '/');
        if (!normalizedRootPath.endsWith("/")) {
            normalizedRootPath += "/";
        }
        return normalizedRootPath + cubeId + "/" + fileName;
    }
    
    /**
     * 根据browseImagePath构建完整的预览图路径
     * 如果browseImagePath是相对路径，则基于rootPath构建
     * 如果是绝对路径（Windows盘符），则直接返回
     * 注意：以 / 开头的路径被认为是相对于viz根目录的路径，不是Unix绝对路径
     * 
     * @param cubeId 立方体ID
     * @param browseImagePath 浏览图路径（可能是相对路径或绝对路径）
     * @return 完整的预览图路径
     */
    public String buildFullVizPath(String cubeId, String browseImagePath) {
        if (browseImagePath == null || browseImagePath.isEmpty()) {
            return null;
        }
        
        // 如果是Windows绝对路径（盘符开头），直接返回
        if (browseImagePath.matches("^[A-Za-z]:[/\\\\].*")) {
            return browseImagePath.replace('\\', '/');
        }
        
        // 处理路径分隔符，统一使用/
        String normalizedRootPath = rootPath.replace('\\', '/');
        if (!normalizedRootPath.endsWith("/")) {
            normalizedRootPath += "/";
        }
        
        // 处理browseImagePath，去掉开头的/（如果存在）
        String normalizedBrowsePath = browseImagePath.replace('\\', '/');
        // 如果以/开头，去掉开头的/
        if (normalizedBrowsePath.startsWith("/")) {
            normalizedBrowsePath = normalizedBrowsePath.substring(1);
        }
        
        // 如果browseImagePath已经包含目录结构（包含/），说明路径可能是 cubeId/filename 格式
        // 例如：GRID_CUBE_T0_J49E016017/ndvi_result_1761808377681.jpg
        if (normalizedBrowsePath.contains("/")) {
            String firstPart = normalizedBrowsePath.substring(0, normalizedBrowsePath.indexOf("/"));
            // 如果路径的第一部分看起来像是cubeId格式（包含GRID_CUBE、CUBE等关键字），直接使用
            // 这样即使cubeId和路径中的不一致，也能正确构建路径
            if (normalizedBrowsePath.startsWith("GRID_CUBE_") || 
                normalizedBrowsePath.startsWith("CUBE_") ||
                firstPart.equals(cubeId)) {
                return normalizedRootPath + normalizedBrowsePath;
            }
        }
        
        // 如果browseImagePath直接以cubeId开头（不含/），说明路径已经是 cubeId/filename 格式
        if (normalizedBrowsePath.startsWith(cubeId + "/")) {
            return normalizedRootPath + normalizedBrowsePath;
        }
        
        // 否则，添加cubeId作为目录
        return normalizedRootPath + cubeId + "/" + normalizedBrowsePath;
    }
}

