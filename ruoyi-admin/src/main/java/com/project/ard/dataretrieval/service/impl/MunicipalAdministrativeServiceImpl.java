package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.MunicipalAdministrative;
import com.project.ard.dataretrieval.mapper.MunicipalAdministrativeMapper;
import com.project.ard.dataretrieval.service.IMunicipalAdministrativeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MunicipalAdministrativeServiceImpl extends ServiceImpl<MunicipalAdministrativeMapper, MunicipalAdministrative>
        implements IMunicipalAdministrativeService {

    @Override
    public MunicipalAdministrative selectByCode(String code) {
        return baseMapper.selectByCode(code);
    }

    @Override
    public MunicipalAdministrative selectByName(String name) {
        return baseMapper.selectByName(name);
    }

    @Override
    public List<MunicipalAdministrative> selectByCodeLike(String code) {
        return baseMapper.selectByCodeLike(code);
    }

    @Override
    public List<MunicipalAdministrative> selectByNameLike(String name) {
        return baseMapper.selectByNameLike(name);
    }
}
















