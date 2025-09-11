package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.Province;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ProvinceMapper extends BaseMapper<Province> {

    @Select("SELECT \"regionId\", \"parentRegionId\", \"regionName\", \"regionEnName\", \"regionAlias\" FROM province WHERE \"regionId\" = #{regionId}")
    Province selectByRegionId(String regionId);

    @Select("SELECT \"regionId\", \"parentRegionId\", \"regionName\", \"regionEnName\", \"regionAlias\" FROM province WHERE \"parentRegionId\" = #{parentRegionId}")
    List<Province> selectByParentRegionId(String parentRegionId);
}


