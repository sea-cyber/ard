package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.Cube;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 立方体数据Mapper接口
 * 
 * @author project
 */
@Mapper
public interface CubeMapper extends BaseMapper<Cube> {
    
    /**
     * 根据ID查询立方体数据，包含GeoJSON格式的bbox字段
     * 
     * @param cubeId 立方体ID
     * @return 立方体数据
     */
    @Select("SELECT cube_id, grid_id, secretlevel, description, province, city, county, city_district, " +
            "epsg, ST_AsGeoJSON(bbox) as bbox, grid_type, organization, department, operator, email, role, " +
            "total_files, original_files, derived_files, seasons_covered, time_span, resolution_level, " +
            "created, updated, created_by FROM cube_grid_info WHERE cube_id = #{cubeId}")
    Cube selectByIdWithGeoJSON(String cubeId);
}