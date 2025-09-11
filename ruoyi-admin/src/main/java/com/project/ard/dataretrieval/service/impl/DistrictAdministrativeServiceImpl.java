package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.DistrictAdministrative;
import com.project.ard.dataretrieval.mapper.DistrictAdministrativeMapper;
import com.project.ard.dataretrieval.service.IDistrictAdministrativeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistrictAdministrativeServiceImpl extends ServiceImpl<DistrictAdministrativeMapper, DistrictAdministrative>
        implements IDistrictAdministrativeService {

    @Override
    public DistrictAdministrative selectByCode(String code) {
        return baseMapper.selectByCode(code);
    }

    @Override
    public DistrictAdministrative selectByName(String name) {
        return baseMapper.selectByName(name);
    }

    @Override
    public List<DistrictAdministrative> selectByCodeLike(String code) {
        return baseMapper.selectByCodeLike(code);
    }

    @Override
    public List<DistrictAdministrative> selectByNameLike(String name) {
        return baseMapper.selectByNameLike(name);
    }
}





