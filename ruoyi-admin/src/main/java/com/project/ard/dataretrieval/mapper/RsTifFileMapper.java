package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.RsTifFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
     */
    @Select("SELECT * FROM rs_tif_file WHERE ST_Intersects(boundary, ST_GeomFromGeoJSON(#{geoJson}))")
    List<RsTifFile> selectBySpatialIntersection(String geoJson);

    /**
     * 根据空间包含查询文件列表
     * 使用PostGIS的ST_Contains函数进行空间查询
     */
    @Select("SELECT * FROM rs_tif_file WHERE ST_Contains(ST_GeomFromGeoJSON(#{geoJson}), boundary)")
    List<RsTifFile> selectBySpatialContains(String geoJson);

    /**
     * 根据空间被包含查询文件列表
     * 使用PostGIS的ST_Within函数进行空间查询
     */
    @Select("SELECT * FROM rs_tif_file WHERE ST_Within(boundary, ST_GeomFromGeoJSON(#{geoJson}))")
    List<RsTifFile> selectBySpatialWithin(String geoJson);
}
