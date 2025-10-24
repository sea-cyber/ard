package com.project.ard.dataretrieval.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.common.annotation.Excel;
import com.project.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 立方体工作流对象 cube_workflow
 * 
 * @author project
 * @date 2024-01-01
 */
public class CubeWorkflow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 工作流ID */
    private String workflowId;

    /** 工作流名称 */
    @Excel(name = "工作流名称")
    private String workflowName;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 分类 */
    @Excel(name = "分类")
    private String category;

    /** 架构 */
    @Excel(name = "架构")
    private String architecture;

    /** 版本 */
    @Excel(name = "版本")
    private String version;

    /** 是否公开 */
    @Excel(name = "是否公开")
    private Boolean isPublic;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 执行器路径 */
    @Excel(name = "执行器路径")
    private String executorPath;

    /** 算法代码 */
    @Excel(name = "算法代码")
    private String algorithmCode;

    public void setWorkflowId(String workflowId) 
    {
        this.workflowId = workflowId;
    }

    public String getWorkflowId() 
    {
        return workflowId;
    }
    public void setWorkflowName(String workflowName) 
    {
        this.workflowName = workflowName;
    }

    public String getWorkflowName() 
    {
        return workflowName;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setUploadTime(Date uploadTime) 
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime() 
    {
        return uploadTime;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setArchitecture(String architecture) 
    {
        this.architecture = architecture;
    }

    public String getArchitecture() 
    {
        return architecture;
    }
    public void setVersion(String version) 
    {
        this.version = version;
    }

    public String getVersion() 
    {
        return version;
    }
    public void setIsPublic(Boolean isPublic) 
    {
        this.isPublic = isPublic;
    }

    public Boolean getIsPublic() 
    {
        return isPublic;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setExecutorPath(String executorPath) 
    {
        this.executorPath = executorPath;
    }

    public String getExecutorPath() 
    {
        return executorPath;
    }
    public void setAlgorithmCode(String algorithmCode) 
    {
        this.algorithmCode = algorithmCode;
    }

    public String getAlgorithmCode() 
    {
        return algorithmCode;
    }
    
    /**
     * 获取工作流处理器类型
     * 注意：这个方法需要在有 WorkflowTypeRegistry 的上下文中调用
     * 建议在 Service 层调用，而不是在实体类中直接调用
     * @return 处理器类型
     */
    public String getProcessorType() {
        // 这个方法现在只返回算法代码，具体的映射在 Service 层处理
        return algorithmCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("workflowId", getWorkflowId())
            .append("workflowName", getWorkflowName())
            .append("userId", getUserId())
            .append("uploadTime", getUploadTime())
            .append("description", getDescription())
            .append("category", getCategory())
            .append("architecture", getArchitecture())
            .append("version", getVersion())
            .append("isPublic", getIsPublic())
            .append("status", getStatus())
            .append("executorPath", getExecutorPath())
            .append("algorithmCode", getAlgorithmCode())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
