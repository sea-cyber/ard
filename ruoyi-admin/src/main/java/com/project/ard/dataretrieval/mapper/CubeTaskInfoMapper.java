package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.CubeTaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 立方体任务信息Mapper接口
 * 
 * @author project
 */
@Mapper
public interface CubeTaskInfoMapper extends BaseMapper<CubeTaskInfo> {
    
    /**
     * 根据用户ID查询任务列表
     * 
     * @param userId 用户ID
     * @return 任务列表
     */
    List<CubeTaskInfo> selectTasksByUserId(@Param("userId") Long userId);
    
    /**
     * 根据任务状态查询任务列表
     * 
     * @param status 任务状态
     * @return 任务列表
     */
    List<CubeTaskInfo> selectTasksByStatus(@Param("status") String status);
    
    /**
     * 根据用户ID和状态查询任务列表
     * 
     * @param userId 用户ID
     * @param status 任务状态
     * @return 任务列表
     */
    List<CubeTaskInfo> selectTasksByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    
    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 新状态
     * @param progress 进度
     * @param errorMessage 错误信息
     * @return 更新结果
     */
    int updateTaskStatus(@Param("taskId") String taskId, 
                        @Param("status") String status, 
                        @Param("progress") Integer progress, 
                        @Param("errorMessage") String errorMessage);
    
    /**
     * 更新任务进度
     * 
     * @param taskId 任务ID
     * @param progress 进度
     * @return 更新结果
     */
    int updateTaskProgress(@Param("taskId") String taskId, @Param("progress") Integer progress);
    
    /**
     * 完成任务
     * 
     * @param taskId 任务ID
     * @param resultCount 结果数量
     * @param resultDirectory 结果目录
     * @return 更新结果
     */
    int completeTask(@Param("taskId") String taskId, 
                     @Param("resultCount") Integer resultCount, 
                     @Param("resultDirectory") String resultDirectory);
}

