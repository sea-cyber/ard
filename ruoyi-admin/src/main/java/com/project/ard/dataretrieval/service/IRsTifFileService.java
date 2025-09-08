package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.RsTifFile;

import java.util.List;

/**
 * 遥感TIF文件服务接口
 * 
 * @author ard
 */
public interface IRsTifFileService extends IService<RsTifFile> {

    /**
     * 根据卫星ID查询文件列表
     */
    List<RsTifFile> selectBySatelliteId(String satelliteId);

    /**
     * 根据传感器ID查询文件列表
     */
    List<RsTifFile> selectBySensorId(String sensorId);

    /**
     * 根据产品ID查询文件列表
     */
    List<RsTifFile> selectByProductId(String productId);

    /**
     * 根据空间相交查询文件列表
     */
    List<RsTifFile> selectBySpatialIntersection(String geoJson);

    /**
     * 根据空间包含查询文件列表
     */
    List<RsTifFile> selectBySpatialContains(String geoJson);

    /**
     * 根据空间被包含查询文件列表
     */
    List<RsTifFile> selectBySpatialWithin(String geoJson);
}
