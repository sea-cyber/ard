package com.project.ard.dataretrieval.service.impl;

import java.util.List;
import com.project.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.ard.dataretrieval.mapper.CubeWorkflowMapper;
import com.project.ard.dataretrieval.entity.CubeWorkflow;
import com.project.ard.dataretrieval.service.ICubeWorkflowService;

/**
 * 立方体工作流Service业务层处理
 * 
 * @author project
 * @date 2024-01-01
 */
@Service
public class CubeWorkflowServiceImpl implements ICubeWorkflowService 
{
    @Autowired
    private CubeWorkflowMapper cubeWorkflowMapper;

    /**
     * 查询立方体工作流
     * 
     * @param workflowId 立方体工作流主键
     * @return 立方体工作流
     */
    @Override
    public CubeWorkflow selectCubeWorkflowByWorkflowId(String workflowId)
    {
        return cubeWorkflowMapper.selectCubeWorkflowByWorkflowId(workflowId);
    }

    /**
     * 查询立方体工作流列表
     * 
     * @param cubeWorkflow 立方体工作流
     * @return 立方体工作流
     */
    @Override
    public List<CubeWorkflow> selectCubeWorkflowList(CubeWorkflow cubeWorkflow)
    {
        return cubeWorkflowMapper.selectCubeWorkflowList(cubeWorkflow);
    }

    /**
     * 查询公开的立方体工作流列表
     * 
     * @param cubeWorkflow 立方体工作流
     * @return 立方体工作流集合
     */
    @Override
    public List<CubeWorkflow> selectPublicCubeWorkflowList(CubeWorkflow cubeWorkflow)
    {
        return cubeWorkflowMapper.selectPublicCubeWorkflowList(cubeWorkflow);
    }

    /**
     * 查询用户可访问的立方体工作流列表（公开的 + 用户自己的私有工作流）
     * 
     * @param cubeWorkflow 立方体工作流
     * @return 立方体工作流集合
     */
    @Override
    public List<CubeWorkflow> selectAccessibleCubeWorkflowList(CubeWorkflow cubeWorkflow)
    {
        return cubeWorkflowMapper.selectAccessibleCubeWorkflowList(cubeWorkflow);
    }

    /**
     * 根据用户ID查询立方体工作流列表
     * 
     * @param userId 用户ID
     * @return 立方体工作流集合
     */
    @Override
    public List<CubeWorkflow> selectCubeWorkflowListByUserId(Long userId)
    {
        return cubeWorkflowMapper.selectCubeWorkflowListByUserId(userId);
    }

    /**
     * 根据分类查询立方体工作流列表
     * 
     * @param category 分类
     * @return 立方体工作流集合
     */
    @Override
    public List<CubeWorkflow> selectCubeWorkflowListByCategory(String category)
    {
        return cubeWorkflowMapper.selectCubeWorkflowListByCategory(category);
    }

    /**
     * 新增立方体工作流
     * 
     * @param cubeWorkflow 立方体工作流
     * @return 结果
     */
    @Override
    public int insertCubeWorkflow(CubeWorkflow cubeWorkflow)
    {
        cubeWorkflow.setUploadTime(DateUtils.getNowDate());
        return cubeWorkflowMapper.insertCubeWorkflow(cubeWorkflow);
    }

    /**
     * 修改立方体工作流
     * 
     * @param cubeWorkflow 立方体工作流
     * @return 结果
     */
    @Override
    public int updateCubeWorkflow(CubeWorkflow cubeWorkflow)
    {
        return cubeWorkflowMapper.updateCubeWorkflow(cubeWorkflow);
    }

    /**
     * 批量删除立方体工作流
     * 
     * @param workflowIds 需要删除的立方体工作流主键
     * @return 结果
     */
    @Override
    public int deleteCubeWorkflowByWorkflowIds(String[] workflowIds)
    {
        return cubeWorkflowMapper.deleteCubeWorkflowByWorkflowIds(workflowIds);
    }

    /**
     * 删除立方体工作流信息
     * 
     * @param workflowId 立方体工作流主键
     * @return 结果
     */
    @Override
    public int deleteCubeWorkflowByWorkflowId(String workflowId)
    {
        return cubeWorkflowMapper.deleteCubeWorkflowByWorkflowId(workflowId);
    }

    /**
     * 根据算法代码查询立方体工作流
     * 
     * @param algorithmCode 算法代码
     * @return 立方体工作流
     */
    @Override
    public CubeWorkflow selectCubeWorkflowByAlgorithmCode(String algorithmCode)
    {
        return cubeWorkflowMapper.selectCubeWorkflowByAlgorithmCode(algorithmCode);
    }

    /**
     * 检查算法代码是否唯一
     * 
     * @param algorithmCode 算法代码
     * @return 结果
     */
    @Override
    public boolean checkAlgorithmCodeUnique(String algorithmCode)
    {
        return cubeWorkflowMapper.checkAlgorithmCodeUnique(algorithmCode) == 0;
    }
}
