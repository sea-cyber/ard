package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.District;
import java.util.List;

public interface IDistrictService extends IService<District> {
    District selectByRegionId(String regionId);
    List<District> selectByParentRegionId(String parentRegionId);
}


