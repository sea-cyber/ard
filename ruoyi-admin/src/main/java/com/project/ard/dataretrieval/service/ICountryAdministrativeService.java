package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.CountryAdministrative;

import java.util.List;

/**
 * 国家行政区划服务接口
 * 
 * @author ard
 */
public interface ICountryAdministrativeService extends IService<CountryAdministrative> {
    /**
     * 根据代码查询行政区划
     */
    CountryAdministrative selectByCode(String code);
    CountryAdministrative selectByName(String name);
    /**
     * 根据代码模糊查询行政区划列表
     */
    List<CountryAdministrative> selectByCodeLike(String code);

    /**
     * 根据名称模糊查询行政区划列表
     */
    List<CountryAdministrative> selectByNameLike(String name);
}

