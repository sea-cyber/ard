package com.project.ard.dataretrieval.service;

import com.project.ard.dataretrieval.dto.SimpleTaskCreateRequest;

import java.util.Map;

/**
 * 任务处理业务：目录枚举、结果复制等
 */
public interface TaskProcessingService {

    /**
     * 处理任务（列出每个切片目录并将原始文件复制到结果目录）
     * @param request 任务请求
     * @return 包含 sliceDirs 与 copied 的结果映射
     */
    Map<String, Object> processTask(SimpleTaskCreateRequest request);
}




