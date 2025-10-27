package com.project.ard.dataretrieval.service.impl;

import com.project.ard.dataretrieval.dto.SimpleTaskCreateRequest;
import com.project.ard.dataretrieval.service.TaskProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.OffsetDateTime;

@Service
public class TaskProcessingServiceImpl implements TaskProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(TaskProcessingServiceImpl.class);

    @Value("${ard.cube.root-path}")
    private String cubeRootPath;

    // 移除cubeResultSliceInfoMapper，不再进行文件复制和数据库记录

    @Override
    public Map<String, Object> processTask(SimpleTaskCreateRequest request) {
        List<Map<String, Object>> sliceDirectoryListings = new ArrayList<>();
        List<Map<String, Object>> copyResults = new ArrayList<>();

        if (request.getSelectedSlices() != null) {
            for (Map<String, Object> slice : request.getSelectedSlices()) {
                String cubeId = slice.get("cubeId") == null ? null : String.valueOf(slice.get("cubeId"));
                String quarter = slice.get("quarter") == null ? null : String.valueOf(slice.get("quarter"));
                Map<String, Object> item = new HashMap<>();
                item.put("cubeId", cubeId);
                item.put("quarter", quarter);

                if (cubeId == null || cubeId.isEmpty() || quarter == null || quarter.isEmpty()) {
                    item.put("path", null);
                    item.put("files", new ArrayList<>());
                    item.put("error", "cubeId 或 quarter 为空");
                    sliceDirectoryListings.add(item);
                    continue;
                }

                // 使用配置的原始数据路径：ARD_CUB_GRIDT0_OFF_RAW/grid_id/quarter
                Path dir = Paths.get(cubeRootPath, cubeId, quarter);
                item.put("path", dir.toString());
                try {
                    if (Files.exists(dir) && Files.isDirectory(dir)) {
                        List<String> files = Files.list(dir)
                                .map(p -> p.getFileName().toString())
                                .sorted()
                                .collect(Collectors.toList());
                        item.put("files", files);
                        logger.info("切片目录: {}, 文件数: {}", dir, files.size());

                        // 注释掉文件复制逻辑，只进行NDVI计算
                        // 文件复制逻辑已移至WorkflowTaskController中处理
                        logger.info("跳过文件复制，等待NDVI计算处理");
                        
                        // 不再复制文件，只记录文件信息用于NDVI计算
                        for (String fileName : files) {
                            Map<String, Object> copyItem = new HashMap<>();
                            copyItem.put("cubeId", cubeId);
                            copyItem.put("quarter", quarter);
                            copyItem.put("fileName", fileName);
                            copyItem.put("status", "ready_for_calculation");
                            
                            // 不再进行文件复制和数据库记录
                            // 这些操作将在NDVI计算完成后进行
                            copyResults.add(copyItem);
                        }
                    } else {
                        item.put("files", new ArrayList<>());
                        item.put("error", "目录不存在或不是文件夹");
                        logger.warn("目录不存在或不是文件夹: {}", dir);
                    }
                } catch (IOException ioEx) {
                    item.put("files", new ArrayList<>());
                    item.put("error", "读取目录失败: " + ioEx.getMessage());
                    logger.error("读取目录失败: {}", dir, ioEx);
                }
                sliceDirectoryListings.add(item);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sliceDirs", sliceDirectoryListings);
        result.put("copied", copyResults);
        return result;
    }
}


