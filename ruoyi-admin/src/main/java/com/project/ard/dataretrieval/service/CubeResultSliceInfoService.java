package com.project.ard.dataretrieval.service;

import com.project.ard.dataretrieval.domain.CubeResultSliceInfo;

/**
 * 立方体结果切片信息服务接口
 * 
 * @author project
 */
public interface CubeResultSliceInfoService {
    
    /**
     * 保存结果切片信息
     * 
     * @param resultSliceInfo 结果切片信息
     * @return 保存结果
     */
    boolean saveResultSliceInfo(CubeResultSliceInfo resultSliceInfo);
    
    /**
     * 根据立方体ID查询结果切片信息
     * 
     * @param cubeId 立方体ID
     * @return 结果切片信息列表
     */
    java.util.List<CubeResultSliceInfo> getResultSliceInfoByCubeId(String cubeId);
    
    /**
     * 根据分析类型查询结果切片信息
     * 
     * @param analysisType 分析类型
     * @return 结果切片信息列表
     */
    java.util.List<CubeResultSliceInfo> getResultSliceInfoByAnalysisType(String analysisType);
    
    /**
     * 根据任务ID查询结果切片信息
     * 
     * @param taskId 任务ID
     * @return 结果切片信息列表
     */
    java.util.List<CubeResultSliceInfo> getResultSliceInfoByTaskId(String taskId);
}
