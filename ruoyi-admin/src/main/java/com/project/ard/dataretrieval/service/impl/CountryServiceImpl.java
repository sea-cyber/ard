package com.project.ard.dataretrieval.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.ard.dataretrieval.domain.Country;
import com.project.ard.dataretrieval.mapper.CountryMapper;
import com.project.ard.dataretrieval.service.ICountryService;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl extends ServiceImpl<CountryMapper, Country> implements ICountryService {

    @Override
    public Country selectByRegionId(String regionId) {
        return baseMapper.selectByRegionId(regionId);
    }
}


























