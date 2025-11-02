package com.project.ard.dataretrieval.service.impl;

import com.project.ard.dataretrieval.domain.CubeSlice;
import com.project.ard.dataretrieval.domain.vo.CubeSliceResponse;
import com.project.ard.dataretrieval.mapper.CubeSliceMapper;
import com.project.ard.dataretrieval.service.CubeSliceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 立方体切片数据服务实现类
 * 
 * @author project
 */
@Slf4j
@Service
public class CubeSliceServiceImpl implements CubeSliceService {
    
    @Autowired
    private CubeSliceMapper cubeSliceMapper;
    
    @Override
    public List<CubeSliceResponse> getSlicesByCubeId(String cubeId) {
        log.info("查询立方体切片数据，立方体ID: {}", cubeId);
        
        List<CubeSlice> slices = cubeSliceMapper.selectSlicesByCubeId(cubeId);
        log.info("查询到 {} 条切片数据", slices.size());
        
        return slices.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public CubeSliceResponse getSliceById(Integer sliceId) {
        log.info("查询切片详情，切片ID: {}", sliceId);
        
        CubeSlice slice = cubeSliceMapper.selectSliceByIdWithGeoJSON(sliceId);
        if (slice == null) {
            log.warn("未找到切片数据，切片ID: {}", sliceId);
            return null;
        }
        
        return convertToResponse(slice);
    }
    
    /**
     * 将实体类转换为响应VO
     * 将绝对路径转换为相对路径，便于前端拼接端口前缀
     * 
     * @param slice 切片实体
     * @return 响应VO
     */
    private CubeSliceResponse convertToResponse(CubeSlice slice) {
        CubeSliceResponse response = new CubeSliceResponse();
        BeanUtils.copyProperties(slice, response);
        
        // 如果存在浏览图路径，将绝对路径转换为相对路径
        if (slice.getBrowseImagePath() != null && !slice.getBrowseImagePath().isEmpty()) {
            String originalPath = slice.getBrowseImagePath();
            String relativePath = convertToRelativePath(originalPath);
            response.setBrowseImagePath(relativePath);
            
            if (!originalPath.equals(relativePath)) {
                log.debug("路径转换 - sliceId: {}, 原始路径: {}, 相对路径: {}", 
                        slice.getSliceId(), originalPath, relativePath);
            }
        }
        
        return response;
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
                log.debug("已识别为相对路径: {}", path);
                return path;
            }
        }
        
        // 处理绝对路径：提取从 /default_user 或用户目录开始的部分
        String normalizedPath = path.replace('\\', '/');
        
        // 优先查找 /default_user 的位置（最常见的情况）
        int defaultUserIndex = normalizedPath.indexOf("/default_user");
        if (defaultUserIndex >= 0) {
            String relativePath = normalizedPath.substring(defaultUserIndex);
            log.info("路径转换（default_user）: {} -> {}", path, relativePath);
            return relativePath;
        }
        
        // 如果没有找到 /default_user，查找其他用户目录模式
        // 模式：/username/ARD_CUB_GRIDT0_username_VIZ/...
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("/([^/]+)/ARD_CUB_GRIDT0_[^/]+_VIZ/");
        java.util.regex.Matcher matcher = pattern.matcher(normalizedPath);
        if (matcher.find()) {
            int startIndex = matcher.start();
            String relativePath = normalizedPath.substring(startIndex);
            log.info("路径转换（用户目录模式）: {} -> {}", path, relativePath);
            return relativePath;
        }
        
        // 如果路径中包含 ARD_CUB_GRIDT0_*_VIZ 模式，尝试提取从用户名目录开始的部分
        // 模式：/username/ARD_CUB_GRIDT0_username_VIZ/...
        java.util.regex.Pattern vizPattern = java.util.regex.Pattern.compile("(/[^/]+/ARD_CUB_GRIDT0_[^/]+_VIZ/.*)");
        java.util.regex.Matcher vizMatcher = vizPattern.matcher(normalizedPath);
        if (vizMatcher.find()) {
            String relativePath = vizMatcher.group(1);
            log.info("路径转换（VIZ目录模式）: {} -> {}", path, relativePath);
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
                log.info("路径转换（VIZ根目录）: {} -> {}", path, relativePath);
                return relativePath;
            }
        }
        
        // 如果路径已经是 GRID_CUBE_xxx/文件名 格式（以/GRID_CUBE_开头），直接返回
        if (normalizedPath.startsWith("/GRID_CUBE_") && normalizedPath.contains("/")) {
            log.debug("已识别为GRID_CUBE相对路径格式: {}", path);
            return path;
        }
        
        // 如果无法转换，返回原路径（可能是相对路径或其他格式）
        log.warn("无法将路径转换为相对路径，保持原路径: {}", path);
        return path;
    }
}

