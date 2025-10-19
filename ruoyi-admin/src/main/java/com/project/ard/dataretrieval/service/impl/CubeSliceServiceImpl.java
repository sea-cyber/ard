package com.project.ard.dataretrieval.service.impl;

import com.project.ard.dataretrieval.domain.CubeSlice;
import com.project.ard.dataretrieval.domain.vo.CubeSliceResponse;
import com.project.ard.dataretrieval.mapper.CubeSliceMapper;
import com.project.ard.dataretrieval.service.CubeSliceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 立方体切片数据服务实现类
 * 
 * @author project
 */
@Slf4j
@Service
public class CubeSliceServiceImpl implements CubeSliceService {
    
    @Autowired
    private CubeSliceMapper cubeSliceMapper;
    
    @Override
    public List<CubeSliceResponse> getSlicesByCubeId(String cubeId) {
        log.info("查询立方体切片数据，立方体ID: {}", cubeId);
        
        List<CubeSlice> slices = cubeSliceMapper.selectSlicesByCubeId(cubeId);
        log.info("查询到 {} 条切片数据", slices.size());
        
        return slices.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public CubeSliceResponse getSliceById(Integer sliceId) {
        log.info("查询切片详情，切片ID: {}", sliceId);
        
        CubeSlice slice = cubeSliceMapper.selectSliceByIdWithGeoJSON(sliceId);
        if (slice == null) {
            log.warn("未找到切片数据，切片ID: {}", sliceId);
            return null;
        }
        
        return convertToResponse(slice);
    }
    
    /**
     * 将实体类转换为响应VO
     * 
     * @param slice 切片实体
     * @return 响应VO
     */
    private CubeSliceResponse convertToResponse(CubeSlice slice) {
        CubeSliceResponse response = new CubeSliceResponse();
        BeanUtils.copyProperties(slice, response);
        return response;
    }
}

