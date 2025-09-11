package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.City;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CityMapper extends BaseMapper<City> {

    @Select("SELECT \"regionId\", \"parentRegionId\", \"regionName\", \"regionEnName\", \"regionAlias\" FROM city WHERE \"regionId\" = #{regionId}")
    City selectByRegionId(String regionId);

    @Select("SELECT \"regionId\", \"parentRegionId\", \"regionName\", \"regionEnName\", \"regionAlias\" FROM city WHERE \"parentRegionId\" = #{parentRegionId}")
    java.util.List<City> selectByParentRegionId(String parentRegionId);
}


