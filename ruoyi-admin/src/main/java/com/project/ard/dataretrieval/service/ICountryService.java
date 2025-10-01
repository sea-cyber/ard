package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.Country;

public interface ICountryService extends IService<Country> {
    Country selectByRegionId(String regionId);
}


























