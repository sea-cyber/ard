package com.project.ard.dataretrieval.service.impl;

import com.project.ard.dataretrieval.domain.CubeTaskInfo;
import com.project.ard.dataretrieval.mapper.CubeTaskInfoMapper;
import com.project.ard.dataretrieval.service.CubeTaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 立方体任务信息服务实现类
 * 
 * @author project
 */
@Service
public class CubeTaskInfoServiceImpl implements CubeTaskInfoService {
    
    private static final Logger logger = LoggerFactory.getLogger(CubeTaskInfoServiceImpl.class);
    
    @Autowired
    private CubeTaskInfoMapper cubeTaskInfoMapper;
    
    @Override
    public boolean createTask(CubeTaskInfo taskInfo) {
        try {
            logger.info("开始创建任务 - 任务ID: {}, 任务名称: {}, 用户ID: {}", 
                       taskInfo.getTaskId(), taskInfo.getTaskName(), taskInfo.getUserId());
            
            // 设置创建时间
            if (taskInfo.getCreated() == null) {
                taskInfo.setCreated(OffsetDateTime.now());
            }
            
            // 设置更新时间
            taskInfo.setUpdated(OffsetDateTime.now());
            
            // 设置默认状态
            if (taskInfo.getStatus() == null) {
                taskInfo.setStatus("pending");
            }
            
            // 设置默认进度
            if (taskInfo.getProgress() == null) {
                taskInfo.setProgress(0);
            }
            
            // 设置默认优先级
            if (taskInfo.getPriority() == null) {
                taskInfo.setPriority(5);
            }
            
            // 设置默认输出格式
            if (taskInfo.getOutputFormat() == null) {
                taskInfo.setOutputFormat("TIF");
            }
            
            logger.info("任务信息准备完成 - 状态: {}, 进度: {}, 优先级: {}, 输出格式: {}", 
                       taskInfo.getStatus(), taskInfo.getProgress(), taskInfo.getPriority(), taskInfo.getOutputFormat());
            
            // 检查关键字段
            logger.info("关键字段检查 - workflowId: {}, userId: {}, createdBy: {}", 
                       taskInfo.getWorkflowId(), taskInfo.getUserId(), taskInfo.getCreatedBy());
            
            // 插入数据
            int result = cubeTaskInfoMapper.insert(taskInfo);
            
            logger.info("数据库插入结果 - 影响行数: {}", result);
            
            if (result > 0) {
                logger.info("成功创建任务 - 任务ID: {}, 任务名称: {}, 用户ID: {}", 
                           taskInfo.getTaskId(), taskInfo.getTaskName(), taskInfo.getUserId());
                return true;
            } else {
                logger.error("创建任务失败 - 任务ID: {}, 任务名称: {}, 影响行数: {}", 
                           taskInfo.getTaskId(), taskInfo.getTaskName(), result);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("创建任务异常 - 任务ID: {}, 任务名称: {}, 错误: {}", 
                        taskInfo.getTaskId(), taskInfo.getTaskName(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public CubeTaskInfo getTaskById(String taskId) {
        try {
            logger.info("查询任务信息 - 任务ID: {}", taskId);
            return cubeTaskInfoMapper.selectById(taskId);
        } catch (Exception e) {
            logger.error("查询任务信息异常 - 任务ID: {}, 错误: {}", taskId, e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public List<CubeTaskInfo> getTasksByUserId(Long userId) {
        try {
            logger.info("查询用户任务列表 - 用户ID: {}", userId);
            return cubeTaskInfoMapper.selectTasksByUserId(userId);
        } catch (Exception e) {
            logger.error("查询用户任务列表异常 - 用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public List<CubeTaskInfo> getTasksByStatus(String status) {
        try {
            logger.info("查询状态任务列表 - 状态: {}", status);
            return cubeTaskInfoMapper.selectTasksByStatus(status);
        } catch (Exception e) {
            logger.error("查询状态任务列表异常 - 状态: {}, 错误: {}", status, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public List<CubeTaskInfo> getTasksByUserIdAndStatus(Long userId, String status) {
        try {
            logger.info("查询用户状态任务列表 - 用户ID: {}, 状态: {}", userId, status);
            return cubeTaskInfoMapper.selectTasksByUserIdAndStatus(userId, status);
        } catch (Exception e) {
            logger.error("查询用户状态任务列表异常 - 用户ID: {}, 状态: {}, 错误: {}", 
                        userId, status, e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public boolean updateTaskStatus(String taskId, String status, Integer progress, String errorMessage) {
        try {
            logger.info("更新任务状态 - 任务ID: {}, 状态: {}, 进度: {}", taskId, status, progress);
            
            // 先检查任务是否存在
            CubeTaskInfo existingTask = cubeTaskInfoMapper.selectById(taskId);
            if (existingTask == null) {
                logger.error("任务不存在 - 任务ID: {}", taskId);
                return false;
            }
            logger.info("找到任务记录 - 任务ID: {}, 当前状态: {}", taskId, existingTask.getStatus());
            
            int result = cubeTaskInfoMapper.updateTaskStatus(taskId, status, progress, errorMessage);
            
            if (result > 0) {
                logger.info("成功更新任务状态 - 任务ID: {}, 状态: {}", taskId, status);
                return true;
            } else {
                logger.error("更新任务状态失败 - 任务ID: {}, 状态: {}, 影响行数: {}", taskId, status, result);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("更新任务状态异常 - 任务ID: {}, 状态: {}, 错误: {}", 
                        taskId, status, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean updateTaskProgress(String taskId, Integer progress) {
        try {
            logger.info("更新任务进度 - 任务ID: {}, 进度: {}", taskId, progress);
            
            // 先检查任务是否存在
            CubeTaskInfo existingTask = cubeTaskInfoMapper.selectById(taskId);
            if (existingTask == null) {
                logger.error("任务不存在 - 任务ID: {}", taskId);
                return false;
            }
            logger.info("找到任务记录 - 任务ID: {}, 当前进度: {}", taskId, existingTask.getProgress());
            
            int result = cubeTaskInfoMapper.updateTaskProgress(taskId, progress);
            
            if (result > 0) {
                logger.info("成功更新任务进度 - 任务ID: {}, 进度: {}", taskId, progress);
                return true;
            } else {
                logger.error("更新任务进度失败 - 任务ID: {}, 进度: {}, 影响行数: {}", taskId, progress, result);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("更新任务进度异常 - 任务ID: {}, 进度: {}, 错误: {}", 
                        taskId, progress, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean completeTask(String taskId, Integer resultCount, String resultDirectory) {
        try {
            logger.info("完成任务 - 任务ID: {}, 结果数量: {}, 结果目录: {}", taskId, resultCount, resultDirectory);
            
            // 先检查任务是否存在
            CubeTaskInfo existingTask = cubeTaskInfoMapper.selectById(taskId);
            if (existingTask == null) {
                logger.error("任务不存在 - 任务ID: {}", taskId);
                return false;
            }
            logger.info("找到任务记录 - 任务ID: {}, 当前状态: {}, 当前进度: {}", 
                       taskId, existingTask.getStatus(), existingTask.getProgress());
            
            int result = cubeTaskInfoMapper.completeTask(taskId, resultCount, resultDirectory);
            
            if (result > 0) {
                logger.info("成功完成任务 - 任务ID: {}", taskId);
                return true;
            } else {
                logger.error("完成任务失败 - 任务ID: {}, 影响行数: {}", taskId, result);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("完成任务异常 - 任务ID: {}, 错误: {}", taskId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean deleteTask(String taskId) {
        try {
            logger.info("删除任务 - 任务ID: {}", taskId);
            
            int result = cubeTaskInfoMapper.deleteById(taskId);
            
            if (result > 0) {
                logger.info("成功删除任务 - 任务ID: {}", taskId);
                return true;
            } else {
                logger.error("删除任务失败 - 任务ID: {}", taskId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("删除任务异常 - 任务ID: {}, 错误: {}", taskId, e.getMessage(), e);
            return false;
        }
    }
}
