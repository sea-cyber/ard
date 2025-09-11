package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.City;
import java.util.List;

public interface ICityService extends IService<City> {
    City selectByRegionId(String regionId);
    List<City> selectByParentRegionId(String parentRegionId);
}


