package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.RsTifFile;
import com.project.ard.dataretrieval.mapper.RsTifFileMapper;
import com.project.ard.dataretrieval.service.IRsTifFileService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 遥感TIF文件服务实现类
 * 
 * @author ard
 */
@Service
public class RsTifFileServiceImpl extends ServiceImpl<RsTifFileMapper, RsTifFile> implements IRsTifFileService {

    @Override
    public List<RsTifFile> selectBySatelliteId(String satelliteId) {
        return baseMapper.selectBySatelliteId(satelliteId);
    }

    @Override
    public List<RsTifFile> selectBySensorId(String sensorId) {
        return baseMapper.selectBySensorId(sensorId);
    }

    @Override
    public List<RsTifFile> selectByProductId(String productId) {
        return baseMapper.selectByProductId(productId);
    }

    @Override
    public List<RsTifFile> selectBySpatialIntersection(String geoJson) {
        return baseMapper.selectBySpatialIntersection(geoJson);
    }

    @Override
    public List<RsTifFile> selectBySpatialContains(String geoJson) {
        return baseMapper.selectBySpatialContains(geoJson);
    }

    @Override
    public List<RsTifFile> selectBySpatialWithin(String geoJson) {
        return baseMapper.selectBySpatialWithin(geoJson);
    }
}
