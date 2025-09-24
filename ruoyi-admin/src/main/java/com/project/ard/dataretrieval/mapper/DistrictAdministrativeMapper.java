package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.DistrictAdministrative;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DistrictAdministrativeMapper extends BaseMapper<DistrictAdministrative> {

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM district_administrative WHERE code = #{code}")
    DistrictAdministrative selectByCode(String code);

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM district_administrative WHERE name = #{name}")
    DistrictAdministrative selectByName(String name);

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM district_administrative WHERE code LIKE CONCAT('%', #{code}, '%')")
    List<DistrictAdministrative> selectByCodeLike(String code);

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM district_administrative WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<DistrictAdministrative> selectByNameLike(String name);
}






