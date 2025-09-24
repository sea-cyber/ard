package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.ProvinceAdministrative;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProvinceAdministrativeMapper extends BaseMapper<ProvinceAdministrative> {

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM province_administrative WHERE code = #{code}")
    ProvinceAdministrative selectByCode(String code);

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM province_administrative WHERE name = #{name}")
    ProvinceAdministrative selectByName(String name);

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM province_administrative WHERE code LIKE CONCAT('%', #{code}, '%')")
    List<ProvinceAdministrative> selectByCodeLike(String code);

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM province_administrative WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<ProvinceAdministrative> selectByNameLike(String name);
}






