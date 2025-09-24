package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.ProvinceAdministrative;

import java.util.List;

public interface IProvinceAdministrativeService extends IService<ProvinceAdministrative> {
    ProvinceAdministrative selectByCode(String code);
    ProvinceAdministrative selectByName(String name);
    List<ProvinceAdministrative> selectByCodeLike(String code);
    List<ProvinceAdministrative> selectByNameLike(String name);
}
















