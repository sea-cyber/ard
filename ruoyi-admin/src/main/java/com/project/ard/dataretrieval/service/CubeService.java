package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.Cube;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalRequest;
import com.project.ard.dataretrieval.domain.vo.CubeRetrievalResponse;
import com.project.ard.dataretrieval.domain.vo.CubeDetailResponse;

import java.util.List;

/**
 * 立方体数据服务接口
 * 
 * @author project
 */
public interface CubeService extends IService<Cube> {
    
    /**
     * 根据条件查询立方体数据
     * 
     * @param request 查询请求参数
     * @return 查询结果
     */
    List<CubeRetrievalResponse> searchCubeData(CubeRetrievalRequest request);
    
    /**
     * 分页查询立方体数据
     * 
     * @param request 查询请求参数
     * @return 分页查询结果
     */
    IPage<CubeRetrievalResponse> searchCubeDataPage(CubeRetrievalRequest request);
    
    /**
     * 根据立方体ID获取立方体详情，包含切片数据
     * 
     * @param cubeId 立方体ID
     * @return 立方体详情
     */
    CubeDetailResponse getCubeDetail(String cubeId);
}