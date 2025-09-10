package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.City;
import com.project.ard.dataretrieval.mapper.CityMapper;
import com.project.ard.dataretrieval.service.ICityService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements ICityService {

    @Override
    public City selectByRegionId(String regionId) {
        return baseMapper.selectByRegionId(regionId);
    }

    @Override
    public List<City> selectByParentRegionId(String parentRegionId) {
        return baseMapper.selectByParentRegionId(parentRegionId);
    }
}


