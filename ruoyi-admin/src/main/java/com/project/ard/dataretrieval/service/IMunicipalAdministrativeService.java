package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.MunicipalAdministrative;

import java.util.List;

public interface IMunicipalAdministrativeService extends IService<MunicipalAdministrative> {
    MunicipalAdministrative selectByCode(String code);
    MunicipalAdministrative selectByName(String name);
    List<MunicipalAdministrative> selectByCodeLike(String code);
    List<MunicipalAdministrative> selectByNameLike(String name);
}





