package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.District;
import com.project.ard.dataretrieval.mapper.DistrictMapper;
import com.project.ard.dataretrieval.service.IDistrictService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DistrictServiceImpl extends ServiceImpl<DistrictMapper, District> implements IDistrictService {

    @Override
    public District selectByRegionId(String regionId) {
        return baseMapper.selectByRegionId(regionId);
    }

    @Override
    public List<District> selectByParentRegionId(String parentRegionId) {
        return baseMapper.selectByParentRegionId(parentRegionId);
    }
}


