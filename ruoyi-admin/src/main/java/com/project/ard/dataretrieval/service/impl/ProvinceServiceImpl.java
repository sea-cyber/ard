package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.Province;
import com.project.ard.dataretrieval.mapper.ProvinceMapper;
import com.project.ard.dataretrieval.service.IProvinceService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProvinceServiceImpl extends ServiceImpl<ProvinceMapper, Province> implements IProvinceService {

    @Override
    public Province selectByRegionId(String regionId) {
        return baseMapper.selectByRegionId(regionId);
    }

    @Override
    public List<Province> selectByParentRegionId(String parentRegionId) {
        return baseMapper.selectByParentRegionId(parentRegionId);
    }
}


