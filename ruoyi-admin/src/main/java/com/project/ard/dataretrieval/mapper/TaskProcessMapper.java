package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.TaskProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务进度Mapper接口
 * 
 * @author system
 * @date 2024-01-20
 */
@Mapper
public interface TaskProcessMapper extends BaseMapper<TaskProcess> {

    /**
     * 根据任务ID查询任务进度列表
     * 
     * @param taskId 任务ID
     * @return 任务进度列表
     */
    @Select("SELECT process_id, step_name, step_status, start_time, end_time, error_log, task_id " +
            "FROM task_process WHERE task_id = #{taskId} ORDER BY process_id ASC")
    List<TaskProcess> selectTaskProcessByTaskId(Integer taskId);

    /**
     * 根据任务ID统计任务进度数量
     * 
     * @param taskId 任务ID
     * @return 进度数量
     */
    @Select("SELECT COUNT(*) FROM task_process WHERE task_id = #{taskId}")
    Integer countTaskProcessByTaskId(Integer taskId);

    /**
     * 根据任务ID和状态查询任务进度
     * 
     * @param taskId 任务ID
     * @param stepStatus 步骤状态
     * @return 任务进度列表
     */
    @Select("SELECT process_id, step_name, step_status, start_time, end_time, error_log, task_id " +
            "FROM task_process WHERE task_id = #{taskId} AND step_status = #{stepStatus} ORDER BY process_id ASC")
    List<TaskProcess> selectTaskProcessByTaskIdAndStatus(Integer taskId, String stepStatus);
}
