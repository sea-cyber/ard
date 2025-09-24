package com.project.ard.dataretrieval.service;

import com.project.ard.dataretrieval.domain.vo.TifRetrievalRequest;
import com.project.ard.dataretrieval.domain.vo.TifRetrievalResponse;
import com.project.common.core.page.TableDataInfo;

import java.util.List;

/**
 * TIF数据检索服务接口
 */
public interface ITifRetrievalService {
    
    /**
     * 检索TIF数据
     */
    TableDataInfo searchTifData(TifRetrievalRequest request);
    
    /**
     * 获取数据详情
     */
    TifRetrievalResponse getDataDetail(String dataId);
    
    /**
     * 下载数据
     */
    String downloadData(String dataId);
    
    /**
     * 获取预览图
     */
    String getPreviewImage(String dataId);
    
    /**
     * 获取卫星列表
     */
    List<String> getSatelliteList();
    
    /**
     * 获取产品类型列表
     */
    List<String> getProductTypeList();
}

