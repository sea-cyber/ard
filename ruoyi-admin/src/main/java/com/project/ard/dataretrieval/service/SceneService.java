package com.project.ard.dataretrieval.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.ard.dataretrieval.domain.Scene;

import java.util.List;

/**
 * 场景服务接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface SceneService extends IService<Scene> {

    /**
     * 搜索场景数据
     * 
     * @param scene 查询条件
     * @return 场景列表
     */
    List<Scene> searchSceneData(Scene scene);

    /**
     * 分页搜索场景数据
     * 
     * @param scene 查询条件
     * @return 分页结果
     */
    IPage<Scene> searchSceneDataPage(Scene scene);
}



