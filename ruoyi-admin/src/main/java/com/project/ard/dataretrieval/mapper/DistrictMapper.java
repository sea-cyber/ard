package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.District;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DistrictMapper extends BaseMapper<District> {

    @Select("SELECT \"regionId\", \"parentRegionId\", \"regionName\", \"regionEnName\", \"regionAlias\" FROM district WHERE \"regionId\" = #{regionId}")
    District selectByRegionId(String regionId);

    @Select("SELECT \"regionId\", \"parentRegionId\", \"regionName\", \"regionEnName\", \"regionAlias\" FROM district WHERE \"parentRegionId\" = #{parentRegionId}")
    java.util.List<District> selectByParentRegionId(String parentRegionId);
}


