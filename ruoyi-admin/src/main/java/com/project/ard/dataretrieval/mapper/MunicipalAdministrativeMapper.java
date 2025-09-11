package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.MunicipalAdministrative;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MunicipalAdministrativeMapper extends BaseMapper<MunicipalAdministrative> {

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM municipal_administrative WHERE code = #{code}")
    MunicipalAdministrative selectByCode(String code);

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM municipal_administrative WHERE name = #{name}")
    MunicipalAdministrative selectByName(String name);

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM municipal_administrative WHERE code LIKE CONCAT('%', #{code}, '%')")
    List<MunicipalAdministrative> selectByCodeLike(String code);

    @Select("SELECT id, name, code, ST_AsGeoJSON(geom) as geom FROM municipal_administrative WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<MunicipalAdministrative> selectByNameLike(String name);
}





