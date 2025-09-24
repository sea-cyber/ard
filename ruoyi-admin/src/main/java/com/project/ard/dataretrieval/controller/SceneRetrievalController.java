package com.project.ard.dataretrieval.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.ard.dataretrieval.domain.Scene;
import com.project.ard.dataretrieval.service.SceneService;
import com.project.common.core.controller.BaseController;
import com.project.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 场景检索控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@Api(tags = "场景检索管理")
@RestController
@RequestMapping("/ard/dataretrieval/scene")
public class SceneRetrievalController extends BaseController {

    @Autowired
    private SceneService sceneService;

    /**
     * 查询场景列表
     */
    @ApiOperation("查询场景列表")
    @GetMapping("/list")
    public TableDataInfo list(Scene scene) {
        startPage();
        List<Scene> list = sceneService.searchSceneData(scene);
        return getDataTable(list);
    }

    /**
     * 分页搜索场景数据
     */
    @ApiOperation("分页搜索场景数据")
    @PostMapping("/search")
    public TableDataInfo searchSceneData(@RequestBody Scene scene) {
        try {
            logger.info("收到场景数据搜索请求: {}", scene);
            IPage<Scene> pageResult = sceneService.searchSceneDataPage(scene);
            logger.info("搜索完成，返回 {} 条数据，总计 {} 条", pageResult.getRecords().size(), pageResult.getTotal());
            return getDataTable(pageResult.getRecords());
        } catch (Exception e) {
            logger.error("场景数据搜索失败", e);
            return getDataTable(new java.util.ArrayList<>());
        }
    }



    /**
     * 新增场景
     */
    @ApiOperation("新增场景")
    @PostMapping
    public TableDataInfo add(@RequestBody Scene scene) {
        boolean result = sceneService.save(scene);
        return getDataTable(java.util.Arrays.asList(result ? scene : null));
    }

}
