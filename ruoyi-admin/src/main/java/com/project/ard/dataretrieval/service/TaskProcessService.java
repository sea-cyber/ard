package com.project.ard.dataretrieval.service;

import com.project.ard.dataretrieval.domain.TaskProcess;

import java.util.List;

/**
 * 任务进度Service接口
 * 
 * @author system
 * @date 2024-01-20
 */
public interface TaskProcessService {

    /**
     * 查询任务进度列表
     * 
     * @param taskProcess 任务进度
     * @return 任务进度集合
     */
    List<TaskProcess> selectTaskProcessList(TaskProcess taskProcess);

    /**
     * 根据任务ID查询任务进度列表
     * 
     * @param taskId 任务ID
     * @return 任务进度列表
     */
    List<TaskProcess> selectTaskProcessByTaskId(Integer taskId);

    /**
     * 新增任务进度
     * 
     * @param taskProcess 任务进度
     * @return 结果
     */
    int insertTaskProcess(TaskProcess taskProcess);

    /**
     * 修改任务进度
     * 
     * @param taskProcess 任务进度
     * @return 结果
     */
    int updateTaskProcess(TaskProcess taskProcess);

    /**
     * 批量删除任务进度
     * 
     * @param processIds 需要删除的任务进度主键集合
     * @return 结果
     */
    int deleteTaskProcessByIds(Integer[] processIds);

    /**
     * 删除任务进度信息
     * 
     * @param processId 任务进度主键
     * @return 结果
     */
    int deleteTaskProcessById(Integer processId);

    /**
     * 根据任务ID删除任务进度
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    int deleteTaskProcessByTaskId(Integer taskId);

    /**
     * 更新任务进度状态
     * 
     * @param processId 进程ID
     * @param stepStatus 步骤状态
     * @return 结果
     */
    int updateTaskProcessStatus(Integer processId, String stepStatus);

    /**
     * 批量插入任务进度
     * 
     * @param taskProcessList 任务进度列表
     * @return 结果
     */
    int batchInsertTaskProcess(List<TaskProcess> taskProcessList);
}
