package com.project.ard.dataretrieval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.ard.dataretrieval.domain.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务Mapper接口
 * 
 * @author system
 * @date 2025-01-01
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 查询任务列表
     * 
     * @param task 任务查询条件
     * @return 任务列表
     */
    @Select("SELECT task_id, task_description, create_time, status, start_time, end_time, " +
            "run_duration, create_user, update_time, process_id, task_type " +
            "FROM task ORDER BY create_time DESC")
    List<Task> selectTaskList(Task task);

    /**
     * 根据任务ID查询任务详情
     * 
     * @param taskId 任务ID
     * @return 任务详情
     */
    @Select("SELECT task_id, task_description, create_time, status, start_time, end_time, " +
            "run_duration, create_user, update_time, process_id, task_type " +
            "FROM task WHERE task_id = #{taskId}")
    Task selectTaskById(@Param("taskId") Integer taskId);

    /**
     * 根据用户ID查询任务列表
     * 
     * @param userId 用户ID
     * @return 任务列表
     */
    @Select("SELECT task_id, task_description, create_time, status, start_time, end_time, " +
            "run_duration, create_user, update_time, process_id, task_type " +
            "FROM task WHERE create_user = #{userId} ORDER BY create_time DESC")
    List<Task> selectTaskListByUserId(@Param("userId") Integer userId);

    /**
     * 根据任务类型查询任务列表
     * 
     * @param taskType 任务类型
     * @return 任务列表
     */
    @Select("SELECT task_id, task_description, create_time, status, start_time, end_time, " +
            "run_duration, create_user, update_time, process_id, task_type " +
            "FROM task WHERE task_type = #{taskType} ORDER BY create_time DESC")
    List<Task> selectTaskListByType(@Param("taskType") String taskType);

    /**
     * 根据任务状态查询任务列表
     * 
     * @param status 任务状态
     * @return 任务列表
     */
    @Select("SELECT task_id, task_description, create_time, status, start_time, end_time, " +
            "run_duration, create_user, update_time, process_id, task_type " +
            "FROM task WHERE status = #{status} ORDER BY create_time DESC")
    List<Task> selectTaskListByStatus(@Param("status") Integer status);

    /**
     * 统计任务数量
     * 
     * @param task 查询条件
     * @return 任务数量
     */
    @Select("SELECT COUNT(*) FROM task")
    Long countTasks(Task task);
}
