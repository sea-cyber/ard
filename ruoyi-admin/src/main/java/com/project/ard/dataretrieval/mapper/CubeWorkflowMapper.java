package com.project.ard.dataretrieval.mapper;

import java.util.List;
import com.project.ard.dataretrieval.entity.CubeWorkflow;

/**
 * 立方体工作流Mapper接口
 * 
 * @author project
 * @date 2024-01-01
 */
public interface CubeWorkflowMapper 
{
    /**
     * 查询立方体工作流
     * 
     * @param workflowId 立方体工作流主键
     * @return 立方体工作流
     */
    public CubeWorkflow selectCubeWorkflowByWorkflowId(String workflowId);

    /**
     * 查询立方体工作流列表
     * 
     * @param cubeWorkflow 立方体工作流
     * @return 立方体工作流集合
     */
    public List<CubeWorkflow> selectCubeWorkflowList(CubeWorkflow cubeWorkflow);

    /**
     * 查询公开的立方体工作流列表
     * 
     * @param cubeWorkflow 立方体工作流
     * @return 立方体工作流集合
     */
    public List<CubeWorkflow> selectPublicCubeWorkflowList(CubeWorkflow cubeWorkflow);

    /**
     * 查询用户可访问的立方体工作流列表（公开的 + 用户自己的私有工作流）
     * 
     * @param cubeWorkflow 立方体工作流
     * @return 立方体工作流集合
     */
    public List<CubeWorkflow> selectAccessibleCubeWorkflowList(CubeWorkflow cubeWorkflow);

    /**
     * 根据用户ID查询立方体工作流列表
     * 
     * @param userId 用户ID
     * @return 立方体工作流集合
     */
    public List<CubeWorkflow> selectCubeWorkflowListByUserId(Long userId);

    /**
     * 根据分类查询立方体工作流列表
     * 
     * @param category 分类
     * @return 立方体工作流集合
     */
    public List<CubeWorkflow> selectCubeWorkflowListByCategory(String category);

    /**
     * 新增立方体工作流
     * 
     * @param cubeWorkflow 立方体工作流
     * @return 结果
     */
    public int insertCubeWorkflow(CubeWorkflow cubeWorkflow);

    /**
     * 修改立方体工作流
     * 
     * @param cubeWorkflow 立方体工作流
     * @return 结果
     */
    public int updateCubeWorkflow(CubeWorkflow cubeWorkflow);

    /**
     * 删除立方体工作流
     * 
     * @param workflowId 立方体工作流主键
     * @return 结果
     */
    public int deleteCubeWorkflowByWorkflowId(String workflowId);

    /**
     * 批量删除立方体工作流
     * 
     * @param workflowIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCubeWorkflowByWorkflowIds(String[] workflowIds);

    /**
     * 根据算法代码查询立方体工作流
     * 
     * @param algorithmCode 算法代码
     * @return 立方体工作流
     */
    public CubeWorkflow selectCubeWorkflowByAlgorithmCode(String algorithmCode);

    /**
     * 检查算法代码是否存在
     * 
     * @param algorithmCode 算法代码
     * @return 结果
     */
    public int checkAlgorithmCodeUnique(String algorithmCode);
}
