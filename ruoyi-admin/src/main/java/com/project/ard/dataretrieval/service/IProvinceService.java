package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.Province;
import java.util.List;

public interface IProvinceService extends IService<Province> {
    Province selectByRegionId(String regionId);
    List<Province> selectByParentRegionId(String parentRegionId);
}


