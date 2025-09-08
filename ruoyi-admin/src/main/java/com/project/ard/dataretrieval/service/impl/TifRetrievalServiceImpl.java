package com.project.ard.dataretrieval.service.impl;

import com.project.ard.dataretrieval.domain.vo.TifRetrievalRequest;
import com.project.ard.dataretrieval.domain.vo.TifRetrievalResponse;
import com.project.ard.dataretrieval.service.ITifRetrievalService;
import com.project.common.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * TIF数据检索服务实现
 */
@Service
public class TifRetrievalServiceImpl implements ITifRetrievalService {

    @Override
    public TableDataInfo searchTifData(TifRetrievalRequest request) {
        // 简单的模拟数据
        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setCode(200);
        dataInfo.setMsg("查询成功");
        dataInfo.setRows(Arrays.asList("模拟数据1", "模拟数据2"));
        dataInfo.setTotal(2L);
        return dataInfo;
    }

    @Override
    public TifRetrievalResponse getDataDetail(String dataId) {
        // 简单的模拟数据
        TifRetrievalResponse response = new TifRetrievalResponse();
        response.setDataId(dataId);
        response.setDataName("模拟文件_" + dataId + ".tif");
        response.setSatelliteName("LANDSAT8");
        response.setAcquisitionTime(new java.util.Date());
        return response;
    }

    @Override
    public String downloadData(String dataId) {
        return "http://localhost:8080/download/" + dataId;
    }

    @Override
    public String getPreviewImage(String dataId) {
        return "http://localhost:8080/preview/" + dataId;
    }

    @Override
    public List<String> getSatelliteList() {
        return Arrays.asList("LANDSAT8", "SENTINEL2", "MODIS");
    }

    @Override
    public List<String> getProductTypeList() {
        return Arrays.asList("L1T", "L2A", "L3A");
    }
}
