package com.project.ard.dataretrieval.service.impl;

import com.project.ard.dataretrieval.domain.CubeResultSliceInfo;
import com.project.ard.dataretrieval.mapper.CubeResultSliceInfoMapper;
import com.project.ard.dataretrieval.service.CubeResultSliceInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 立方体结果切片信息服务实现类
 * 
 * @author project
 */
@Service
public class CubeResultSliceInfoServiceImpl implements CubeResultSliceInfoService {
    
    private static final Logger logger = LoggerFactory.getLogger(CubeResultSliceInfoServiceImpl.class);
    
    @Autowired
    private CubeResultSliceInfoMapper cubeResultSliceInfoMapper;
    
    @Override
    public boolean saveResultSliceInfo(CubeResultSliceInfo resultSliceInfo) {
        try {
            // 设置创建时间
            if (resultSliceInfo.getCreated() == null) {
                resultSliceInfo.setCreated(OffsetDateTime.now());
            }
            
            // 设置更新时间
            resultSliceInfo.setUpdated(OffsetDateTime.now());
            
            // 设置分析时间
            if (resultSliceInfo.getAnalysisTime() == null) {
                resultSliceInfo.setAnalysisTime(OffsetDateTime.now());
            }
            
            // 调试：打印要保存的对象的所有字段值
            logger.info("=== 准备保存到数据库的对象信息 ===");
            logger.info("cubeId: {}", resultSliceInfo.getCubeId());
            logger.info("fileName: {}", resultSliceInfo.getFileName());
            logger.info("resultSlicePath: {}", resultSliceInfo.getResultSlicePath());
            logger.info("browseImagePath: {}", resultSliceInfo.getBrowseImagePath());
            logger.info("===============================");
            
            // 插入数据
            int result = cubeResultSliceInfoMapper.insert(resultSliceInfo);
            
            if (result > 0) {
                logger.info("成功保存结果切片信息 - 立方体ID: {}, 分析类型: {}, 文件名: {}, 预览图路径: {}", 
                           resultSliceInfo.getCubeId(), resultSliceInfo.getAnalysisType(), 
                           resultSliceInfo.getFileName(), resultSliceInfo.getBrowseImagePath());
                return true;
            } else {
                logger.error("保存结果切片信息失败 - 立方体ID: {}, 分析类型: {}", 
                           resultSliceInfo.getCubeId(), resultSliceInfo.getAnalysisType());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("保存结果切片信息异常 - 立方体ID: {}, 分析类型: {}, 错误: {}", 
                        resultSliceInfo.getCubeId(), resultSliceInfo.getAnalysisType(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<CubeResultSliceInfo> getResultSliceInfoByCubeId(String cubeId) {
        try {
            logger.info("查询结果切片信息 - 立方体ID: {}", cubeId);
            
            // 使用MyBatis-Plus的查询方法
            List<CubeResultSliceInfo> result = cubeResultSliceInfoMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CubeResultSliceInfo>()
                    .eq("cube_id", cubeId)
                    .orderByDesc("created")
            );
            
            // 转换路径：将绝对路径转换为相对路径
            result.forEach(this::convertBrowseImagePathToRelative);
            
            logger.info("查询到 {} 条结果切片信息", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询结果切片信息异常 - 立方体ID: {}, 错误: {}", cubeId, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public List<CubeResultSliceInfo> getResultSliceInfoByAnalysisType(String analysisType) {
        try {
            logger.info("查询结果切片信息 - 分析类型: {}", analysisType);
            
            // 使用MyBatis-Plus的查询方法
            List<CubeResultSliceInfo> result = cubeResultSliceInfoMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CubeResultSliceInfo>()
                    .eq("analysis_type", analysisType)
                    .orderByDesc("created")
            );
            
            // 转换路径：将绝对路径转换为相对路径
            result.forEach(this::convertBrowseImagePathToRelative);
            
            logger.info("查询到 {} 条结果切片信息", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询结果切片信息异常 - 分析类型: {}, 错误: {}", analysisType, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public List<CubeResultSliceInfo> getResultSliceInfoByTaskId(String taskId) {
        try {
            logger.info("查询结果切片信息 - 任务ID: {}", taskId);
            
            // 使用MyBatis-Plus的查询方法
            List<CubeResultSliceInfo> result = cubeResultSliceInfoMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CubeResultSliceInfo>()
                    .eq("task_id", taskId)
                    .orderByDesc("created")
            );
            
            // 转换路径：将绝对路径转换为相对路径
            result.forEach(this::convertBrowseImagePathToRelative);
            
            logger.info("查询到 {} 条结果切片信息", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询结果切片信息异常 - 任务ID: {}, 错误: {}", taskId, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public List<CubeResultSliceInfo> getResultSliceInfoByUserIdAndCubeId(Long userId, String cubeId) {
        try {
            logger.info("查询结果切片信息 - 用户ID: {}, 立方体ID: {}", userId, cubeId);
            
            // 使用MyBatis-Plus的查询方法
            List<CubeResultSliceInfo> result = cubeResultSliceInfoMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CubeResultSliceInfo>()
                    .eq("user_id", userId)
                    .eq("cube_id", cubeId)
                    .orderByDesc("created")
            );
            
            // 转换路径：将绝对路径转换为相对路径
            result.forEach(this::convertBrowseImagePathToRelative);
            
            logger.info("查询到 {} 条结果切片信息", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询结果切片信息异常 - 用户ID: {}, 立方体ID: {}, 错误: {}", userId, cubeId, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * 将结果切片的浏览图路径从绝对路径转换为相对路径
     * 与原始切片的处理方式保持一致
     * 
     * @param resultSlice 结果切片信息
     */
    private void convertBrowseImagePathToRelative(CubeResultSliceInfo resultSlice) {
        if (resultSlice != null && resultSlice.getBrowseImagePath() != null && !resultSlice.getBrowseImagePath().isEmpty()) {
            String originalPath = resultSlice.getBrowseImagePath();
            String relativePath = convertToRelativePath(originalPath);
            resultSlice.setBrowseImagePath(relativePath);
            
            if (!originalPath.equals(relativePath)) {
                logger.debug("结果切片路径转换 - resultSliceId: {}, 原始路径: {}, 相对路径: {}", 
                        resultSlice.getResultSliceId(), originalPath, relativePath);
            }
        }
    }
    
    /**
     * 将绝对路径转换为相对路径
     * 例如：D:/GISER/ard/development/cubedata/ARD_CUB_GRIDT0_VIZ/GRID_CUBE_T0_N51E016010/default_user/ARD_CUB_GRIDT0_default_user_VIZ/GRID_CUBE_T0_J49E016017/ndvi_result_1761808377682.jpg
     * 转换为：/default_user/ARD_CUB_GRIDT0_default_user_VIZ/GRID_CUBE_T0_J49E016017/ndvi_result_1761808377682.jpg
     * 
     * @param path 原始路径（可能是绝对路径或相对路径）
     * @return 相对路径
     */
    private String convertToRelativePath(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        
        // 如果已经是相对路径（以/开头且不包含Windows盘符），检查格式
        if (path.startsWith("/") && !path.matches("^[A-Za-z]:[/\\\\].*")) {
            // 验证是否是有效的相对路径格式：
            // 1. 包含 /default_user（用户数据路径）
            // 2. 包含用户VIZ目录模式
            // 3. 或者是 GRID_CUBE_xxx/文件名 格式（原始切片预览图）
            if (path.contains("/default_user") 
                || path.matches("/[^/]+/ARD_CUB_GRIDT0_[^/]+_VIZ/.*")
                || path.matches("/GRID_CUBE_[^/]+/.*\\.(jpg|jpeg|png|JPG|JPEG|PNG)$")) {
                logger.debug("已识别为相对路径: {}", path);
                return path;
            }
        }
        
        // 处理绝对路径：提取从 /default_user 或用户目录开始的部分
        String normalizedPath = path.replace('\\', '/');
        
        // 优先查找 /default_user 的位置（最常见的情况）
        int defaultUserIndex = normalizedPath.indexOf("/default_user");
        if (defaultUserIndex >= 0) {
            String relativePath = normalizedPath.substring(defaultUserIndex);
            logger.debug("路径转换（default_user）: {} -> {}", path, relativePath);
            return relativePath;
        }
        
        // 如果没有找到 /default_user，查找其他用户目录模式
        // 模式：/username/ARD_CUB_GRIDT0_username_VIZ/...
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("/([^/]+)/ARD_CUB_GRIDT0_[^/]+_VIZ/");
        java.util.regex.Matcher matcher = pattern.matcher(normalizedPath);
        if (matcher.find()) {
            int startIndex = matcher.start();
            String relativePath = normalizedPath.substring(startIndex);
            logger.debug("路径转换（用户目录模式）: {} -> {}", path, relativePath);
            return relativePath;
        }
        
        // 如果路径中包含 ARD_CUB_GRIDT0_*_VIZ 模式，尝试提取从用户名目录开始的部分
        // 模式：/username/ARD_CUB_GRIDT0_username_VIZ/...
        java.util.regex.Pattern vizPattern = java.util.regex.Pattern.compile("(/[^/]+/ARD_CUB_GRIDT0_[^/]+_VIZ/.*)");
        java.util.regex.Matcher vizMatcher = vizPattern.matcher(normalizedPath);
        if (vizMatcher.find()) {
            String relativePath = vizMatcher.group(1);
            logger.debug("路径转换（VIZ目录模式）: {} -> {}", path, relativePath);
            return relativePath;
        }
        
        // 如果是绝对路径且包含 ARD_CUB_GRIDT0_VIZ 目录，尝试提取从 VIZ 目录下第一个 cubeId 开始的部分
        // 例如：D:/.../ARD_CUB_GRIDT0_VIZ/GRID_CUBE_T0_J49E016017/xxx.jpg
        // 转换为：/GRID_CUBE_T0_J49E016017/xxx.jpg
        int vizDirIndex = normalizedPath.indexOf("/ARD_CUB_GRIDT0_VIZ/");
        if (vizDirIndex >= 0) {
            int afterVizIndex = vizDirIndex + "/ARD_CUB_GRIDT0_VIZ/".length();
            if (afterVizIndex < normalizedPath.length()) {
                String relativePath = "/" + normalizedPath.substring(afterVizIndex);
                logger.info("路径转换（VIZ根目录）: {} -> {}", path, relativePath);
                return relativePath;
            }
        }
        
        // 如果路径已经是 GRID_CUBE_xxx/文件名 格式（以/GRID_CUBE_开头），直接返回
        if (normalizedPath.startsWith("/GRID_CUBE_") && normalizedPath.contains("/")) {
            logger.debug("已识别为GRID_CUBE相对路径格式: {}", path);
            return path;
        }
        
        // 如果无法转换，返回原路径（可能是相对路径或其他格式）
        logger.warn("无法将路径转换为相对路径，保持原路径: {}", path);
        return path;
    }
}
