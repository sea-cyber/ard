package com.project.ard.dataretrieval.service.impl;

import com.project.ard.dataretrieval.domain.CubeTaskStep;
import com.project.ard.dataretrieval.mapper.CubeTaskStepMapper;
import com.project.ard.dataretrieval.service.CubeTaskStepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 立方体任务步骤服务实现类
 * 
 * @author project
 */
@Service
public class CubeTaskStepServiceImpl implements CubeTaskStepService {
    
    private static final Logger logger = LoggerFactory.getLogger(CubeTaskStepServiceImpl.class);
    
    @Autowired
    private CubeTaskStepMapper cubeTaskStepMapper;
    
    @Override
    public boolean createTaskStep(CubeTaskStep taskStep) {
        try {
            // 设置创建时间
            if (taskStep.getCreated() == null) {
                taskStep.setCreated(OffsetDateTime.now());
            }
            taskStep.setUpdated(OffsetDateTime.now());
            
            int result = cubeTaskStepMapper.insert(taskStep);
            
            if (result > 0) {
                logger.info("成功创建任务步骤 - 任务ID: {}, 步骤名称: {}, 步骤顺序: {}", 
                           taskStep.getTaskId(), taskStep.getStepName(), taskStep.getStepOrder());
                return true;
            } else {
                logger.error("创建任务步骤失败 - 任务ID: {}, 步骤名称: {}", 
                           taskStep.getTaskId(), taskStep.getStepName());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("创建任务步骤异常 - 任务ID: {}, 步骤名称: {}, 错误: {}", 
                        taskStep.getTaskId(), taskStep.getStepName(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<CubeTaskStep> getStepsByTaskId(String taskId) {
        try {
            logger.info("查询任务步骤 - 任务ID: {}", taskId);
            return cubeTaskStepMapper.selectStepsByTaskId(taskId);
        } catch (Exception e) {
            logger.error("查询任务步骤异常 - 任务ID: {}, 错误: {}", taskId, e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public CubeTaskStep getStepByTaskIdAndOrder(String taskId, Integer stepOrder) {
        try {
            logger.info("查询任务步骤 - 任务ID: {}, 步骤顺序: {}", taskId, stepOrder);
            return cubeTaskStepMapper.selectStepByTaskIdAndOrder(taskId, stepOrder);
        } catch (Exception e) {
            logger.error("查询任务步骤异常 - 任务ID: {}, 步骤顺序: {}, 错误: {}", 
                        taskId, stepOrder, e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean startStep(String taskId, Integer stepOrder, String stepDesc) {
        try {
            CubeTaskStep step = getStepByTaskIdAndOrder(taskId, stepOrder);
            if (step == null) {
                logger.error("步骤不存在 - 任务ID: {}, 步骤顺序: {}", taskId, stepOrder);
                return false;
            }
            
            int result = cubeTaskStepMapper.updateStepStatus(step.getStepId(), "processing", stepDesc, null);
            if (result > 0) {
                cubeTaskStepMapper.updateStepStartTime(step.getStepId(), OffsetDateTime.now());
                logger.info("步骤开始处理 - 任务ID: {}, 步骤名称: {}, 步骤说明: {}", 
                           taskId, step.getStepName(), stepDesc);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            logger.error("开始步骤异常 - 任务ID: {}, 步骤顺序: {}, 错误: {}", 
                        taskId, stepOrder, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean completeStep(String taskId, Integer stepOrder, String stepDesc) {
        try {
            CubeTaskStep step = getStepByTaskIdAndOrder(taskId, stepOrder);
            if (step == null) {
                logger.error("步骤不存在 - 任务ID: {}, 步骤顺序: {}", taskId, stepOrder);
                return false;
            }
            
            int result = cubeTaskStepMapper.updateStepStatus(step.getStepId(), "completed", stepDesc, null);
            if (result > 0) {
                cubeTaskStepMapper.updateStepEndTime(step.getStepId(), OffsetDateTime.now());
                logger.info("步骤完成 - 任务ID: {}, 步骤名称: {}, 步骤说明: {}", 
                           taskId, step.getStepName(), stepDesc);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            logger.error("完成步骤异常 - 任务ID: {}, 步骤顺序: {}, 错误: {}", 
                        taskId, stepOrder, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean failStep(String taskId, Integer stepOrder, String errorDetails) {
        try {
            CubeTaskStep step = getStepByTaskIdAndOrder(taskId, stepOrder);
            if (step == null) {
                logger.error("步骤不存在 - 任务ID: {}, 步骤顺序: {}", taskId, stepOrder);
                return false;
            }
            
            int result = cubeTaskStepMapper.updateStepStatus(step.getStepId(), "failed", null, errorDetails);
            if (result > 0) {
                cubeTaskStepMapper.updateStepEndTime(step.getStepId(), OffsetDateTime.now());
                logger.error("步骤失败 - 任务ID: {}, 步骤名称: {}, 错误详情: {}", 
                           taskId, step.getStepName(), errorDetails);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            logger.error("失败步骤异常 - 任务ID: {}, 步骤顺序: {}, 错误: {}", 
                        taskId, stepOrder, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean createAllTaskSteps(String taskId, String processingCenter) {
        try {
            logger.info("创建任务所有步骤 - 任务ID: {}, 执行中心: {}", taskId, processingCenter);
            
            // 创建4个步骤
            String[] stepNames = {"数据准备", "任务拆分", "算法初始化", "结果输出"};
            
            for (int i = 0; i < stepNames.length; i++) {
                CubeTaskStep step = new CubeTaskStep();
                step.setTaskId(taskId);
                step.setStepName(stepNames[i]);
                step.setStepOrder(i + 1);
                step.setStepStatus("pending");
                step.setProcessingCenter(processingCenter);
                
                if (!createTaskStep(step)) {
                    logger.error("创建步骤失败 - 任务ID: {}, 步骤名称: {}", taskId, stepNames[i]);
                    return false;
                }
            }
            
            logger.info("成功创建任务所有步骤 - 任务ID: {}", taskId);
            return true;
            
        } catch (Exception e) {
            logger.error("创建任务所有步骤异常 - 任务ID: {}, 错误: {}", taskId, e.getMessage(), e);
            return false;
        }
    }
}

