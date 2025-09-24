package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.RsTifFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 遥感TIF文件Mapper接口
 * 
 * @author ard
 */
@Mapper
public interface RsTifFileMapper extends BaseMapper<RsTifFile> {

    /**
     * 根据卫星ID查询文件列表
     */
    @Select("SELECT * FROM rs_tif_file WHERE satellite_id = #{satelliteId}")
    List<RsTifFile> selectBySatelliteId(String satelliteId);

    /**
     * 根据传感器ID查询文件列表
     */
    @Select("SELECT * FROM rs_tif_file WHERE sensor_id = #{sensorId}")
    List<RsTifFile> selectBySensorId(String sensorId);

    /**
     * 根据产品ID查询文件列表
     */
    @Select("SELECT * FROM rs_tif_file WHERE product_id = #{productId}")
    List<RsTifFile> selectByProductId(String productId);

    /**
     * 根据空间相交查询文件列表
     * 使用PostGIS的ST_Intersects函数进行空间查询
     * 强制设置两个几何体的SRID为0（无SRID）进行比较，避免坐标系不匹配问题
     */
    @Select("SELECT * FROM rs_tif_file WHERE ST_Intersects(ST_SetSRID(boundary, 0), ST_SetSRID(ST_GeomFromGeoJSON(#{geoJson}), 0))")
    List<RsTifFile> selectBySpatialIntersection(String geoJson);

    /**
     * 根据空间包含查询文件列表
     * 使用PostGIS的ST_Contains函数进行空间查询
     * 强制设置两个几何体的SRID为0（无SRID）进行比较，避免坐标系不匹配问题
     */
    @Select("SELECT * FROM rs_tif_file WHERE ST_Contains(ST_SetSRID(ST_GeomFromGeoJSON(#{geoJson}), 0), ST_SetSRID(boundary, 0))")
    List<RsTifFile> selectBySpatialContains(String geoJson);

    /**
     * 根据空间被包含查询文件列表
     * 使用PostGIS的ST_Within函数进行空间查询
     * 强制设置两个几何体的SRID为0（无SRID）进行比较，避免坐标系不匹配问题
     */
    @Select("SELECT * FROM rs_tif_file WHERE ST_Within(ST_SetSRID(boundary, 0), ST_SetSRID(ST_GeomFromGeoJSON(#{geoJson}), 0))")
    List<RsTifFile> selectBySpatialWithin(String geoJson);

    /**
     * 根据ID查询文件，并将boundary字段转换为GeoJSON格式
     * 使用PostGIS的ST_AsGeoJSON函数将几何数据转换为GeoJSON
     */
    @Select("SELECT id, satellite_id, sensor_id, acquisition_time, cloud_percent, file_path, filename, " +
            "ST_AsGeoJSON(boundary) as boundary, product_id, scene_path, scene_row, orbit_id, " +
            "laser_count, has_entity, has_pair, in_cart, quick_view_uri, input_time, tar_input_time " +
            "FROM rs_tif_file WHERE id = #{id}")
    RsTifFile selectByIdWithGeoJSON(@Param("id") Long id);

    /**
     * 查询所有文件，并将boundary字段转换为GeoJSON格式
     * 使用PostGIS的ST_AsGeoJSON函数将几何数据转换为GeoJSON
     */
    @Select("SELECT id, satellite_id, sensor_id, acquisition_time, cloud_percent, file_path, filename, " +
            "ST_AsGeoJSON(boundary) as boundary, product_id, scene_path, scene_row, orbit_id, " +
            "laser_count, has_entity, has_pair, in_cart, quick_view_uri, input_time, tar_input_time " +
            "FROM rs_tif_file")
    List<RsTifFile> selectAllWithGeoJSON();
}
