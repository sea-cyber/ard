package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.Country;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CountryMapper extends BaseMapper<Country> {

    @Select("SELECT \"regionId\", \"parentRegionId\", \"regionName\", \"regionEnName\", \"regionAlias\" FROM country WHERE \"regionId\" = #{regionId}")
    Country selectByRegionId(String regionId);
}



