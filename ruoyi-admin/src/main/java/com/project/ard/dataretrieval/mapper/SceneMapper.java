package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.Scene;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 场景Mapper接口
 * 
 * @author system
 * @date 2024-01-01
 */
@Mapper
public interface SceneMapper extends BaseMapper<Scene> {

    /**
     * 根据ID查询场景信息（包含GeoJSON格式的边界）
     * 
     * @param id 场景ID
     * @return 场景信息
     */
    @Select("SELECT id, scene_name, create_user, ST_AsGeoJSON(boundary) as boundary, " +
            "create_time, data_type, data_describe, is_analysis, analysis_type, " +
            "analysis_result_id, path_code, row_code, time_range " +
            "FROM scene WHERE id = #{id}")
    Scene selectByIdWithGeoJSON(Integer id);
}



