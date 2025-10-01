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
     * 根据ID查询立方体数据，包含GeoJSON格式的boundary字段
     * 
     * @param id 立方体ID
     * @return 立方体数据
     */
    @Select("SELECT id, cube_name, create_user, ST_AsGeoJSON(boundary) as boundary, create_time, data_type, data_describe, compression_algorithm, path_code, row_code, time_range FROM cube WHERE id = #{id}")
    Cube selectByIdWithGeoJSON(Long id);
}