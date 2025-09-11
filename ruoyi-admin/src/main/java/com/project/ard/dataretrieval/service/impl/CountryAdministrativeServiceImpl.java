package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.CountryAdministrative;
import com.project.ard.dataretrieval.mapper.CountryAdministrativeMapper;
import com.project.ard.dataretrieval.service.ICountryAdministrativeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 国家行政区划服务实现类
 * 
 * @author ard
 */
@Service
public class CountryAdministrativeServiceImpl extends ServiceImpl<CountryAdministrativeMapper, CountryAdministrative> implements ICountryAdministrativeService {

    @Override
    public CountryAdministrative selectByCode(String code) {
        return baseMapper.selectByCode(code);
    }

    @Override
    public List<CountryAdministrative> selectByCodeLike(String code) {
        return baseMapper.selectByCodeLike(code);
    }

    @Override
    public List<CountryAdministrative> selectByNameLike(String name) {
        return baseMapper.selectByNameLike(name);
    }
}

