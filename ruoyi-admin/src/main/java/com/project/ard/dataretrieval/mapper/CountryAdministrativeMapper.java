package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.CountryAdministrative;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 国家行政区划Mapper接口
 * 
 * @author ard
 */
@Mapper
public interface CountryAdministrativeMapper extends BaseMapper<CountryAdministrative> {

    /**
     * 根据代码查询行政区划
     */
    @Select("SELECT id, name, code, ST_AsText(geom) as geom FROM country_administrative WHERE code = #{code}")
    CountryAdministrative selectByCode(String code);

    /**
     * 根据代码模糊查询行政区划列表
     */
    @Select("SELECT id, name, code, ST_AsText(geom) as geom FROM country_administrative WHERE code LIKE CONCAT('%', #{code}, '%')")
    List<CountryAdministrative> selectByCodeLike(String code);

    /**
     * 根据名称模糊查询行政区划列表
     */
    @Select("SELECT id, name, code, ST_AsText(geom) as geom FROM country_administrative WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<CountryAdministrative> selectByNameLike(String name);
}
