package com.project.ard.dataretrieval.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.common.annotation.Log;
import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import com.project.common.enums.BusinessType;
import com.project.ard.dataretrieval.entity.CubeWorkflow;
import com.project.ard.dataretrieval.service.ICubeWorkflowService;
import com.project.common.utils.poi.ExcelUtil;
import com.project.common.core.page.TableDataInfo;

/**
 * 立方体工作流Controller
 * 
 * @author project
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/ard/dataretrieval/workflow")
public class CubeWorkflowController extends BaseController
{
    @Autowired
    private ICubeWorkflowService cubeWorkflowService;

    /**
     * 查询立方体工作流列表
     */
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:workflow:list')")
    @GetMapping("/list")
    public TableDataInfo list(CubeWorkflow cubeWorkflow)
    {
        startPage();
        List<CubeWorkflow> list = cubeWorkflowService.selectCubeWorkflowList(cubeWorkflow);
        return getDataTable(list);
    }

    /**
     * 查询公开的立方体工作流列表
     */
    @GetMapping("/public/list")
    public TableDataInfo publicList(CubeWorkflow cubeWorkflow)
    {
        startPage();
        List<CubeWorkflow> list = cubeWorkflowService.selectPublicCubeWorkflowList(cubeWorkflow);
        return getDataTable(list);
    }

    /**
     * 根据用户ID查询立方体工作流列表
     */
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:workflow:list')")
    @GetMapping("/user/{userId}")
    public TableDataInfo listByUserId(@PathVariable("userId") Long userId)
    {
        startPage();
        List<CubeWorkflow> list = cubeWorkflowService.selectCubeWorkflowListByUserId(userId);
        return getDataTable(list);
    }

    /**
     * 根据分类查询立方体工作流列表
     */
    @GetMapping("/category/{category}")
    public TableDataInfo listByCategory(@PathVariable("category") String category)
    {
        startPage();
        List<CubeWorkflow> list = cubeWorkflowService.selectCubeWorkflowListByCategory(category);
        return getDataTable(list);
    }

    /**
     * 获取所有可用的工作流选项（用于前端下拉选择）
     * 返回：公开的工作流 + 当前用户的私有工作流
     */
    @GetMapping("/options")
    public AjaxResult getWorkflowOptions()
    {
        CubeWorkflow queryParam = new CubeWorkflow();
        queryParam.setUserId(getUserId()); // 设置当前用户ID
        List<CubeWorkflow> list = cubeWorkflowService.selectAccessibleCubeWorkflowList(queryParam);
        return success(list);
    }

    /**
     * 调试用：获取所有工作流（不限制条件）
     */
    @GetMapping("/debug/all")
    public AjaxResult getAllWorkflows()
    {
        try {
            CubeWorkflow queryParam = new CubeWorkflow();
            List<CubeWorkflow> list = cubeWorkflowService.selectCubeWorkflowList(queryParam);
            return success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 调试用：测试数据库连接
     */
    @GetMapping("/debug/test")
    public AjaxResult testConnection()
    {
        try {
            // 简单的计数查询
            CubeWorkflow queryParam = new CubeWorkflow();
            List<CubeWorkflow> list = cubeWorkflowService.selectCubeWorkflowList(queryParam);
            return success("数据库连接正常，工作流数量: " + list.size());
        } catch (Exception e) {
            e.printStackTrace();
            return error("数据库连接失败: " + e.getMessage());
        }
    }

    /**
     * 导出立方体工作流列表
     */
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:workflow:export')")
    @Log(title = "立方体工作流", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CubeWorkflow cubeWorkflow)
    {
        List<CubeWorkflow> list = cubeWorkflowService.selectCubeWorkflowList(cubeWorkflow);
        ExcelUtil<CubeWorkflow> util = new ExcelUtil<CubeWorkflow>(CubeWorkflow.class);
        util.exportExcel(response, list, "立方体工作流数据");
    }

    /**
     * 获取立方体工作流详细信息
     */
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:workflow:query')")
    @GetMapping(value = "/{workflowId}")
    public AjaxResult getInfo(@PathVariable("workflowId") String workflowId)
    {
        return success(cubeWorkflowService.selectCubeWorkflowByWorkflowId(workflowId));
    }

    /**
     * 新增立方体工作流
     */
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:workflow:add')")
    @Log(title = "立方体工作流", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CubeWorkflow cubeWorkflow)
    {
        // 检查算法代码是否唯一
        if (!cubeWorkflowService.checkAlgorithmCodeUnique(cubeWorkflow.getAlgorithmCode()))
        {
            return error("算法代码已存在");
        }
        
        // 设置当前用户ID
        cubeWorkflow.setUserId(getUserId());
        
        return toAjax(cubeWorkflowService.insertCubeWorkflow(cubeWorkflow));
    }

    /**
     * 修改立方体工作流
     */
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:workflow:edit')")
    @Log(title = "立方体工作流", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CubeWorkflow cubeWorkflow)
    {
        // 检查算法代码是否唯一（排除当前记录）
        CubeWorkflow existingWorkflow = cubeWorkflowService.selectCubeWorkflowByAlgorithmCode(cubeWorkflow.getAlgorithmCode());
        if (existingWorkflow != null && !existingWorkflow.getWorkflowId().equals(cubeWorkflow.getWorkflowId()))
        {
            return error("算法代码已存在");
        }
        
        return toAjax(cubeWorkflowService.updateCubeWorkflow(cubeWorkflow));
    }

    /**
     * 删除立方体工作流
     */
    @PreAuthorize("@ss.hasPermi('ard:dataretrieval:workflow:remove')")
    @Log(title = "立方体工作流", businessType = BusinessType.DELETE)
	@DeleteMapping("/{workflowIds}")
    public AjaxResult remove(@PathVariable String[] workflowIds)
    {
        return toAjax(cubeWorkflowService.deleteCubeWorkflowByWorkflowIds(workflowIds));
    }
}
