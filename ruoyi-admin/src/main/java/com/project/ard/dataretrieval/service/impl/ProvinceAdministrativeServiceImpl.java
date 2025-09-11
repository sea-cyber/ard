package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.ProvinceAdministrative;
import com.project.ard.dataretrieval.mapper.ProvinceAdministrativeMapper;
import com.project.ard.dataretrieval.service.IProvinceAdministrativeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceAdministrativeServiceImpl extends ServiceImpl<ProvinceAdministrativeMapper, ProvinceAdministrative>
        implements IProvinceAdministrativeService {

    @Override
    public ProvinceAdministrative selectByCode(String code) {
        return baseMapper.selectByCode(code);
    }

    @Override
    public ProvinceAdministrative selectByName(String name) {
        return baseMapper.selectByName(name);
    }

    @Override
    public List<ProvinceAdministrative> selectByCodeLike(String code) {
        return baseMapper.selectByCodeLike(code);
    }

    @Override
    public List<ProvinceAdministrative> selectByNameLike(String name) {
        return baseMapper.selectByNameLike(name);
    }
}





