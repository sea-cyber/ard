package com.project.ard.dataretrieval.service;

import com.project.ard.dataretrieval.domain.vo.CubeSliceResponse;

import java.util.List;

/**
 * 立方体切片数据服务接口
 * 
 * @author project
 */
public interface CubeSliceService {
    
    /**
     * 根据立方体ID查询相关的切片数据
     * 
     * @param cubeId 立方体ID
     * @return 切片数据列表
     */
    List<CubeSliceResponse> getSlicesByCubeId(String cubeId);
    
    /**
     * 根据切片ID查询切片详情
     * 
     * @param sliceId 切片ID
     * @return 切片详情
     */
    CubeSliceResponse getSliceById(Integer sliceId);
}
