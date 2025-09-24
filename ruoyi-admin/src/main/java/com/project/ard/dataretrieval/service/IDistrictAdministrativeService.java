package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.DistrictAdministrative;

import java.util.List;

public interface IDistrictAdministrativeService extends IService<DistrictAdministrative> {
    DistrictAdministrative selectByCode(String code);
    DistrictAdministrative selectByName(String name);
    List<DistrictAdministrative> selectByCodeLike(String code);
    List<DistrictAdministrative> selectByNameLike(String name);
}
















