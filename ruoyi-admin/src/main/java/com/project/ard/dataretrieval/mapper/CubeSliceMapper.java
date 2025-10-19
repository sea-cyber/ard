package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.CubeSlice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 立方体切片数据Mapper接口
 * 
 * @author project
 */
@Mapper
public interface CubeSliceMapper extends BaseMapper<CubeSlice> {
    
    /**
     * 根据立方体ID查询相关的切片数据
     * 查询cube_slice_info表
     * 
     * @param cubeId 立方体ID
     * @return 切片数据列表
     */
    @Select("SELECT slice_id, cube_id, quarter, slice_path, file_name, file_format, " +
            "browse_image_path, browse_file_name, browse_format, imaging_time, location, " +
            "slice_desc, resolution, created, updated, created_by " +
            "FROM cube_slice_info " +
            "WHERE cube_id = #{cubeId} " +
            "ORDER BY imaging_time DESC")
    List<CubeSlice> selectSlicesByCubeId(@Param("cubeId") String cubeId);
    
    /**
     * 根据切片ID查询切片详情
     * 
     * @param sliceId 切片ID
     * @return 切片数据
     */
    @Select("SELECT slice_id, cube_id, quarter, slice_path, file_name, file_format, " +
            "browse_image_path, browse_file_name, browse_format, imaging_time, location, " +
            "slice_desc, resolution, created, updated, created_by " +
            "FROM cube_slice_info WHERE slice_id = #{sliceId}")
    CubeSlice selectSliceByIdWithGeoJSON(@Param("sliceId") Integer sliceId);
}

