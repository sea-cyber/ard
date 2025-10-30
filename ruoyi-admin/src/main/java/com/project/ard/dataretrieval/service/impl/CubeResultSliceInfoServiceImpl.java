package com.project.ard.dataretrieval.service.impl;

import com.project.ard.dataretrieval.domain.CubeResultSliceInfo;
import com.project.ard.dataretrieval.mapper.CubeResultSliceInfoMapper;
import com.project.ard.dataretrieval.service.CubeResultSliceInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 立方体结果切片信息服务实现类
 * 
 * @author project
 */
@Service
public class CubeResultSliceInfoServiceImpl implements CubeResultSliceInfoService {
    
    private static final Logger logger = LoggerFactory.getLogger(CubeResultSliceInfoServiceImpl.class);
    
    @Autowired
    private CubeResultSliceInfoMapper cubeResultSliceInfoMapper;
    
    @Override
    public boolean saveResultSliceInfo(CubeResultSliceInfo resultSliceInfo) {
        try {
            // 设置创建时间
            if (resultSliceInfo.getCreated() == null) {
                resultSliceInfo.setCreated(OffsetDateTime.now());
            }
            
            // 设置更新时间
            resultSliceInfo.setUpdated(OffsetDateTime.now());
            
            // 设置分析时间
            if (resultSliceInfo.getAnalysisTime() == null) {
                resultSliceInfo.setAnalysisTime(OffsetDateTime.now());
            }
            
            // 调试：打印要保存的对象的所有字段值
            logger.info("=== 准备保存到数据库的对象信息 ===");
            logger.info("cubeId: {}", resultSliceInfo.getCubeId());
            logger.info("fileName: {}", resultSliceInfo.getFileName());
            logger.info("resultSlicePath: {}", resultSliceInfo.getResultSlicePath());
            logger.info("browseImagePath: {}", resultSliceInfo.getBrowseImagePath());
            logger.info("===============================");
            
            // 插入数据
            int result = cubeResultSliceInfoMapper.insert(resultSliceInfo);
            
            if (result > 0) {
                logger.info("成功保存结果切片信息 - 立方体ID: {}, 分析类型: {}, 文件名: {}, 预览图路径: {}", 
                           resultSliceInfo.getCubeId(), resultSliceInfo.getAnalysisType(), 
                           resultSliceInfo.getFileName(), resultSliceInfo.getBrowseImagePath());
                return true;
            } else {
                logger.error("保存结果切片信息失败 - 立方体ID: {}, 分析类型: {}", 
                           resultSliceInfo.getCubeId(), resultSliceInfo.getAnalysisType());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("保存结果切片信息异常 - 立方体ID: {}, 分析类型: {}, 错误: {}", 
                        resultSliceInfo.getCubeId(), resultSliceInfo.getAnalysisType(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<CubeResultSliceInfo> getResultSliceInfoByCubeId(String cubeId) {
        try {
            logger.info("查询结果切片信息 - 立方体ID: {}", cubeId);
            
            // 使用MyBatis-Plus的查询方法
            List<CubeResultSliceInfo> result = cubeResultSliceInfoMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CubeResultSliceInfo>()
                    .eq("cube_id", cubeId)
                    .orderByDesc("created")
            );
            
            logger.info("查询到 {} 条结果切片信息", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询结果切片信息异常 - 立方体ID: {}, 错误: {}", cubeId, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public List<CubeResultSliceInfo> getResultSliceInfoByAnalysisType(String analysisType) {
        try {
            logger.info("查询结果切片信息 - 分析类型: {}", analysisType);
            
            // 使用MyBatis-Plus的查询方法
            List<CubeResultSliceInfo> result = cubeResultSliceInfoMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CubeResultSliceInfo>()
                    .eq("analysis_type", analysisType)
                    .orderByDesc("created")
            );
            
            logger.info("查询到 {} 条结果切片信息", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询结果切片信息异常 - 分析类型: {}, 错误: {}", analysisType, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public List<CubeResultSliceInfo> getResultSliceInfoByTaskId(String taskId) {
        try {
            logger.info("查询结果切片信息 - 任务ID: {}", taskId);
            
            // 使用MyBatis-Plus的查询方法
            List<CubeResultSliceInfo> result = cubeResultSliceInfoMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CubeResultSliceInfo>()
                    .eq("task_id", taskId)
                    .orderByDesc("created")
            );
            
            logger.info("查询到 {} 条结果切片信息", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询结果切片信息异常 - 任务ID: {}, 错误: {}", taskId, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public List<CubeResultSliceInfo> getResultSliceInfoByUserIdAndCubeId(Long userId, String cubeId) {
        try {
            logger.info("查询结果切片信息 - 用户ID: {}, 立方体ID: {}", userId, cubeId);
            
            // 使用MyBatis-Plus的查询方法
            List<CubeResultSliceInfo> result = cubeResultSliceInfoMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CubeResultSliceInfo>()
                    .eq("user_id", userId)
                    .eq("cube_id", cubeId)
                    .orderByDesc("created")
            );
            
            logger.info("查询到 {} 条结果切片信息", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询结果切片信息异常 - 用户ID: {}, 立方体ID: {}, 错误: {}", userId, cubeId, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
}
