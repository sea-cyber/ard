package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.CubeTaskStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 立方体任务步骤Mapper接口
 * 
 * @author project
 */
@Mapper
public interface CubeTaskStepMapper extends BaseMapper<CubeTaskStep> {
    
    /**
     * 根据任务ID查询步骤列表
     * 
     * @param taskId 任务ID
     * @return 步骤列表
     */
    List<CubeTaskStep> selectStepsByTaskId(@Param("taskId") String taskId);
    
    /**
     * 根据任务ID和步骤顺序查询步骤
     * 
     * @param taskId 任务ID
     * @param stepOrder 步骤顺序
     * @return 步骤信息
     */
    CubeTaskStep selectStepByTaskIdAndOrder(@Param("taskId") String taskId, @Param("stepOrder") Integer stepOrder);
    
    /**
     * 更新步骤状态
     * 
     * @param stepId 步骤ID
     * @param stepStatus 步骤状态
     * @param stepDesc 步骤说明
     * @param errorDetails 错误详情
     * @return 更新结果
     */
    int updateStepStatus(@Param("stepId") Long stepId, 
                        @Param("stepStatus") String stepStatus,
                        @Param("stepDesc") String stepDesc,
                        @Param("errorDetails") String errorDetails);
    
    /**
     * 更新步骤开始时间
     * 
     * @param stepId 步骤ID
     * @param startTime 开始时间
     * @return 更新结果
     */
    int updateStepStartTime(@Param("stepId") Long stepId, @Param("startTime") java.time.OffsetDateTime startTime);
    
    /**
     * 更新步骤结束时间
     * 
     * @param stepId 步骤ID
     * @param endTime 结束时间
     * @return 更新结果
     */
    int updateStepEndTime(@Param("stepId") Long stepId, @Param("endTime") java.time.OffsetDateTime endTime);
}

