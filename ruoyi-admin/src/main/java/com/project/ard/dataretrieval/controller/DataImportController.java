package com.project.ard.dataretrieval.controller;

import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import com.project.common.utils.SecurityUtils;
import com.project.ard.dataretrieval.domain.CubeTaskInfo;
import com.project.ard.dataretrieval.domain.CubeSlice;
import com.project.ard.dataretrieval.mapper.CubeSliceMapper;
import com.project.ard.dataretrieval.service.CubeTaskInfoService;
import com.project.ard.dataretrieval.service.CubeTaskStepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 数据导入控制器
 * 
 * @author project
 * @date 2025-01-27
 */
@RestController
@RequestMapping("/system/data-import")
public class DataImportController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(DataImportController.class);
    
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/data/upload";
    private static final String EXTRACT_DIR = System.getProperty("user.dir") + "/data/extract";
    
    @Autowired
    private CubeTaskInfoService cubeTaskInfoService;
    
    @Autowired
    private CubeTaskStepService cubeTaskStepService;
    
    @Autowired
    private CubeSliceMapper cubeSliceMapper;
    
    // 固定的立方体ID（根据用户要求）
    private static final String DEFAULT_CUBE_ID = "GRID_CUBE_T0_J49E017017";
    
    /**
     * 扫描文件/文件夹
     * 对于文件夹模式：前端已经打包成ZIP上传
     * 对于压缩包模式：直接上传压缩包
     * 统一按ZIP格式处理，只读取文件结构，不解压
     */
    @PostMapping("/scan")
    public AjaxResult scan(@RequestParam("file") MultipartFile file, 
                          @RequestParam("sourceType") String sourceType) {
        try {
            log.info("开始扫描文件，类型: {}, 文件名: {}, 大小: {} bytes", 
                    sourceType, file.getOriginalFilename(), file.getSize());
            
            // 统一按ZIP格式处理（文件夹已打包成ZIP，压缩包也是ZIP格式）
            List<FileItem> fileList = scanZipFile(file);
            
            log.info("扫描完成，找到 {} 个第一级文件/文件夹", fileList.size());
            
            // 打印扫描结果
            for (FileItem item : fileList) {
                log.info("  - {} ({})", item.getName(), item.isDirectory() ? "目录" : "文件");
            }
            
            return AjaxResult.success(fileList);
            
        } catch (Exception e) {
            log.error("扫描文件失败", e);
            return AjaxResult.error("扫描失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行数据导入
     * 1. 保存上传的文件（文件夹已打包成ZIP，压缩包直接上传）
     * 2. 解压ZIP文件
     * 3. 扫描解压后的第一级目录并打印
     * 4. 后续处理（读取元数据、保存到数据库等）
     */
    @PostMapping("/import")
    public AjaxResult importData(@RequestParam("file") MultipartFile file,
                                @RequestParam("sourceType") String sourceType,
                                @RequestParam("compressionAlgorithm") String compressionAlgorithm) {
        Path uploadPath = null;
        Path extractPath = null;
        
        try {
            log.info("开始导入数据，类型: {}, 算法: {}, 文件名: {}, 大小: {} bytes", 
                    sourceType, compressionAlgorithm, file.getOriginalFilename(), file.getSize());
            
            // 获取当前用户名
            String currentUsername = "system";
            try {
                currentUsername = SecurityUtils.getUsername();
            } catch (Exception e) {
                log.warn("获取当前用户名失败，使用默认值: {}", e.getMessage());
            }
            
            // 1. 保存上传的文件到临时目录
            uploadPath = saveUploadedFileToTemp(file);
            log.info("文件已保存到: {}", uploadPath);
            
            // 2. 解压文件
            extractPath = extractArchive(uploadPath);
            log.info("文件已解压到: {}", extractPath);
            
            // 3. 扫描解压后的第一级目录并打印
            List<FileItem> extractedItems = scanExtractedDirectory(extractPath);
            log.info("解压后找到 {} 个第一级文件/文件夹:", extractedItems.size());
            for (FileItem item : extractedItems) {
                log.info("  - {} ({})", item.getName(), item.isDirectory() ? "目录" : "文件");
            }
            
            // 3.1 提取第一个非目录文件的名称作为任务名称
            String internalFileName = null;
            
            // 首先尝试从第一级目录中找到第一个文件
            for (FileItem item : extractedItems) {
                if (!item.isDirectory()) {
                    internalFileName = item.getName();
                    log.info("从第一级目录找到第一个文件: {}, 将用作任务名称", internalFileName);
                    break;
                }
            }
            
            // 如果第一级都是目录，递归查找第一个文件
            if (internalFileName == null) {
                log.info("第一级目录都是文件夹，开始递归查找第一个文件...");
                String firstFilePath = findFirstFile(extractPath);
                if (firstFilePath != null) {
                    Path firstFile = Paths.get(firstFilePath);
                    internalFileName = firstFile.getFileName().toString();
                    log.info("递归查找到第一个文件: {} (完整路径: {}), 将用作任务名称", internalFileName, firstFilePath);
                } else {
                    log.warn("递归查找未找到任何文件，将使用压缩包名称");
                }
            }
            
            // 如果仍然没有找到文件，使用压缩包名称
            if (internalFileName == null || internalFileName.isEmpty()) {
                internalFileName = file.getOriginalFilename();
                log.warn("未找到内部文件，使用压缩包名称作为任务名称: {}", internalFileName);
            }
            
            // 记录最终使用的文件名
            log.info("最终使用的任务名称文件名: {} (原始压缩包名称: {})", internalFileName, file.getOriginalFilename());
            
            // 4. 创建数据导入任务（使用内部文件名称）
            String taskId = createDataImportTask(internalFileName, compressionAlgorithm, extractedItems.size(), extractPath.toString());
            
            if (taskId == null) {
                log.warn("创建数据导入任务失败，但数据已解压");
                return AjaxResult.success("数据导入成功，共处理 " + extractedItems.size() + " 个文件/文件夹，但任务创建失败");
            }
            
            log.info("数据导入任务创建成功，任务ID: {}", taskId);
            
            // 5. 扫描并读取文件，插入到 cube_slice_info 表
            log.info("准备调用 processAndInsertSlices - 解压路径: {}, 任务ID: {}, 创建者: {}", extractPath, taskId, currentUsername);
            int insertedSliceCount = processAndInsertSlices(extractPath, taskId, currentUsername);
            log.info("processAndInsertSlices 调用完成，返回插入数量: {}", insertedSliceCount);
            log.info("数据导入完成，共插入 {} 条切片记录", insertedSliceCount);
            
            // 6. 更新任务状态和步骤为完成
            if (insertedSliceCount > 0) {
                log.info("开始更新任务状态和步骤为完成 - 任务ID: {}", taskId);
                
                // 完成所有步骤
                // 步骤1: 数据准备
                cubeTaskStepService.completeStep(taskId, 1, "数据准备完成，插入 " + insertedSliceCount + " 条切片记录");
                
                // 步骤2: 任务拆分
                cubeTaskStepService.completeStep(taskId, 2, "任务拆分完成");
                
                // 步骤3: 算法初始化
                cubeTaskStepService.completeStep(taskId, 3, "算法初始化完成");
                
                // 步骤4: 结果输出
                cubeTaskStepService.completeStep(taskId, 4, "结果输出完成，共插入 " + insertedSliceCount + " 条切片记录");
                
                // 更新任务状态为完成，进度为100%
                cubeTaskInfoService.updateTaskStatus(taskId, "completed", 100, null);
                
                log.info("成功更新任务状态和步骤为完成 - 任务ID: {}", taskId);
            } else {
                log.warn("未插入任何切片记录，任务状态不更新 - 任务ID: {}", taskId);
            }
            
            // 返回任务ID和文件数量
            Map<String, Object> result = new HashMap<>();
            result.put("taskId", taskId);
            result.put("fileCount", extractedItems.size());
            result.put("sliceCount", insertedSliceCount);
            result.put("message", "数据导入成功，共处理 " + extractedItems.size() + " 个文件/文件夹，插入 " + insertedSliceCount + " 条切片记录");
            
            return AjaxResult.success(result);
            
        } catch (Exception e) {
            log.error("导入数据失败", e);
            // 如果任务创建失败，记录详细信息
            if (extractPath != null) {
                log.error("数据已解压到: {}, 但导入过程中发生异常", extractPath);
            }
            return AjaxResult.error("导入失败: " + e.getMessage());
        } finally {
            // 清理临时文件（可选，根据实际需求决定是否保留）
            // try {
            //     if (uploadPath != null) Files.deleteIfExists(uploadPath);
            //     if (extractPath != null) deleteDirectory(extractPath);
            // } catch (Exception e) {
            //     log.warn("清理临时文件失败", e);
            // }
        }
    }
    
    /**
     * 扫描ZIP文件内容（下一级）
     * 只读取ZIP文件结构，不解压文件内容，获取第一级目录/文件名
     */
    private List<FileItem> scanZipFile(MultipartFile file) throws IOException {
        List<FileItem> fileList = new ArrayList<>();
        Set<String> seenEntries = new HashSet<>();
        
        log.info("开始扫描ZIP文件: {}", file.getOriginalFilename());
        
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                
                // 跳过空路径或只有斜杠的路径
                if (entryName == null || entryName.trim().isEmpty() || entryName.equals("/")) {
                    zipInputStream.closeEntry();
                    continue;
                }
                
                // 获取第一层路径
                String firstLevel = getFirstLevel(entryName);
                if (firstLevel != null && !seenEntries.contains(firstLevel)) {
                    seenEntries.add(firstLevel);
                    
                    FileItem item = new FileItem();
                    item.setName(firstLevel);
                    // 判断是否为目录：entry是目录，或者第一层后面还有路径
                    boolean isDir = entry.isDirectory() || entryName.substring(firstLevel.length()).contains("/");
                    item.setDirectory(isDir);
                    if (!isDir && entry.getSize() > 0) {
                        item.setSize(entry.getSize());
                    }
                    
                    fileList.add(item);
                }
                
                zipInputStream.closeEntry();
            }
        }
        
        log.info("ZIP文件扫描完成，找到 {} 个第一级项", fileList.size());
        
        // 按名称排序
        fileList.sort((a, b) -> {
            // 目录排在前面
            if (a.isDirectory() && !b.isDirectory()) {
                return -1;
            }
            if (!a.isDirectory() && b.isDirectory()) {
                return 1;
            }
            return a.getName().compareTo(b.getName());
        });
        
        return fileList;
    }
    
    /**
     * 扫描压缩文件（压缩包和文件夹打包的ZIP都用这个方法）
     * 只读取ZIP文件结构，不解压，获取第一级目录/文件名
     */
    private List<FileItem> scanCompressedFile(MultipartFile file) throws IOException {
        log.info("开始扫描压缩文件: {}", file.getOriginalFilename());
        
        // 压缩包和文件夹打包的ZIP都用相同的扫描方式
        // 只需要读取ZIP文件结构，不需要解压
        return scanZipFile(file);
    }
    
    /**
     * 获取第一层目录名
     */
    private String getFirstLevel(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        
        // 移除开头的斜杠
        path = path.startsWith("/") ? path.substring(1) : path;
        
        // 取第一部分
        int index = path.indexOf('/');
        if (index > 0) {
            return path.substring(0, index);
        }
        
        return path;
    }
    
    /**
     * 保存上传的文件到临时目录
     */
    private Path saveUploadedFileToTemp(MultipartFile file) throws IOException {
        // 创建上传目录
        Path uploadDir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 保存文件，使用时间戳避免文件名冲突
        String originalFilename = file.getOriginalFilename();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = timestamp + "_" + originalFilename;
        Path savePath = uploadDir.resolve(filename);
        file.transferTo(savePath);
        
        return savePath;
    }
    
    /**
     * 解压ZIP文件
     */
    private Path extractArchive(Path archivePath) throws IOException {
        // 创建解压目录
        Path extractDir = Paths.get(EXTRACT_DIR);
        if (!Files.exists(extractDir)) {
            Files.createDirectories(extractDir);
        }
        
        // 创建唯一的解压目录
        String archiveName = archivePath.getFileName().toString();
        String extractDirName = archiveName.substring(0, archiveName.lastIndexOf('.')) + "_" + System.currentTimeMillis();
        Path extractPath = extractDir.resolve(extractDirName);
        Files.createDirectories(extractPath);
        
        log.info("开始解压文件: {} 到: {}", archivePath, extractPath);
        
        // 解压ZIP文件
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(archivePath))) {
            ZipEntry entry;
            long totalSize = 0;
            int fileCount = 0;
            
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                
                // 安全检查：防止路径遍历攻击
                Path entryPath = extractPath.resolve(entryName).normalize();
                if (!entryPath.startsWith(extractPath)) {
                    log.warn("跳过不安全的路径: {}", entryName);
                    zipInputStream.closeEntry();
                    continue;
                }
                
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (OutputStream os = Files.newOutputStream(entryPath)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                            totalSize += len;
                        }
                    }
                    fileCount++;
                }
                
                zipInputStream.closeEntry();
            }
            
            log.info("解压完成，共解压 {} 个文件，总大小: {} bytes", fileCount, totalSize);
        }
        
        return extractPath;
    }
    
    /**
     * 扫描解压后的目录（只扫描第一级）
     */
    private List<FileItem> scanExtractedDirectory(Path directory) throws IOException {
        List<FileItem> fileList = new ArrayList<>();
        
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return fileList;
        }
        
        try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                FileItem item = new FileItem();
                item.setName(entry.getFileName().toString());
                item.setDirectory(Files.isDirectory(entry));
                if (Files.isRegularFile(entry)) {
                    item.setSize(Files.size(entry));
                }
                fileList.add(item);
            }
        }
        
        // 按名称排序，目录排在前面
        fileList.sort((a, b) -> {
            if (a.isDirectory() && !b.isDirectory()) {
                return -1;
            }
            if (!a.isDirectory() && b.isDirectory()) {
                return 1;
            }
            return a.getName().compareTo(b.getName());
        });
        
        return fileList;
    }
    
    /**
     * 递归查找第一个文件（非目录）
     * 
     * @param directory 要搜索的目录
     * @return 第一个文件的完整路径，如果没找到返回null
     */
    private String findFirstFile(Path directory) {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            log.warn("findFirstFile: 目录不存在或不是目录: {}", directory);
            return null;
        }
        
        log.info("findFirstFile: 开始搜索目录: {}", directory);
        
        try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                log.debug("findFirstFile: 检查项: {}, 是文件: {}, 是目录: {}", 
                         entry, Files.isRegularFile(entry), Files.isDirectory(entry));
                
                if (Files.isRegularFile(entry)) {
                    String filePath = entry.toString();
                    log.info("findFirstFile: 找到第一个文件: {}", filePath);
                    return filePath;
                } else if (Files.isDirectory(entry)) {
                    // 递归查找子目录
                    log.debug("findFirstFile: 进入子目录递归查找: {}", entry);
                    String found = findFirstFile(entry);
                    if (found != null) {
                        log.info("findFirstFile: 在子目录中找到文件: {}", found);
                        return found;
                    }
                }
            }
        } catch (IOException e) {
            log.error("查找第一个文件时出错: {}", e.getMessage(), e);
        }
        
        log.warn("findFirstFile: 在目录 {} 中未找到任何文件", directory);
        return null;
    }
    
    /**
     * 递归删除目录
     */
    private void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        deleteDirectory(entry);
                    } else {
                        Files.delete(entry);
                    }
                }
            }
            Files.delete(directory);
        }
    }
    
    /**
     * 创建数据导入任务
     * 
     * @param fileName 文件名
     * @param compressionAlgorithm 压缩算法（作为算法/工作流名称）
     * @param fileCount 文件数量
     * @param extractPath 解压路径
     * @return 任务ID，如果创建失败返回null
     */
    private String createDataImportTask(String fileName, String compressionAlgorithm, int fileCount, String extractPath) {
        // 生成任务ID（在方法级别声明，以便在 catch 块中使用）
        String taskId = "TASK_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
        
        try {
            log.info("开始创建数据导入任务 - 任务ID: {}, 文件名: {}, 压缩算法: {}", taskId, fileName, compressionAlgorithm);
            
            // 获取当前用户ID和用户名
            Long currentUserId = null;
            String currentUsername = "system";
            try {
                currentUserId = SecurityUtils.getUserId();
                currentUsername = SecurityUtils.getUsername();
                log.info("当前用户ID: {}, 用户名: {}", currentUserId, currentUsername);
            } catch (Exception e) {
                log.error("获取当前用户信息失败: {}", e.getMessage(), e);
                // 如果获取用户ID失败，返回null，不创建任务
                return null;
            }
            
            // 确保用户ID不为空
            if (currentUserId == null) {
                log.error("用户ID为空，无法创建任务");
                return null;
            }
            
            // 创建任务信息
            CubeTaskInfo taskInfo = new CubeTaskInfo();
            taskInfo.setTaskId(taskId);
            taskInfo.setTaskName("数据导入任务: " + fileName);
            taskInfo.setTaskDescription("数据导入任务，压缩算法: " + compressionAlgorithm + "，文件数量: " + fileCount);
            taskInfo.setTaskType("DATA_IMPORT");
            taskInfo.setUserId(currentUserId);
            taskInfo.setCreatedBy(currentUsername);
            taskInfo.setCreated(OffsetDateTime.now());
            taskInfo.setUpdated(OffsetDateTime.now());
            taskInfo.setProcessingCenter("默认数据中心");
            taskInfo.setOutputResolution("30m×30m");
            taskInfo.setOutputFormat("TIF");
            // 将压缩算法作为工作流ID和名称（外键约束已删除，可以直接存储压缩算法名称）
            taskInfo.setWorkflowId(compressionAlgorithm);
            taskInfo.setWorkflowName(compressionAlgorithm);
            taskInfo.setWorkflowDescription("数据导入压缩算法: " + compressionAlgorithm);
            taskInfo.setStatus("pending");
            taskInfo.setProgress(0);
            taskInfo.setPriority(5);
            taskInfo.setResultDirectory(extractPath);
            
            // 保存任务记录
            log.info("准备保存任务记录 - 任务ID: {}, 任务名称: {}, 用户ID: {}, 任务类型: {}", 
                    taskId, taskInfo.getTaskName(), taskInfo.getUserId(), taskInfo.getTaskType());
            
            boolean taskCreated = cubeTaskInfoService.createTask(taskInfo);
            if (!taskCreated) {
                log.error("创建数据导入任务失败 - 任务ID: {}, 任务名称: {}", taskId, taskInfo.getTaskName());
                return null;
            }
            
            log.info("任务创建服务返回成功 - 任务ID: {}", taskId);
            
            // 验证任务是否真的创建成功
            CubeTaskInfo verifyTask = cubeTaskInfoService.getTaskById(taskId);
            if (verifyTask == null) {
                log.error("任务创建后验证失败 - 任务ID: {} 在数据库中不存在", taskId);
                log.error("任务信息: 任务ID={}, 任务名称={}, 用户ID={}, 任务类型={}", 
                        taskInfo.getTaskId(), taskInfo.getTaskName(), taskInfo.getUserId(), taskInfo.getTaskType());
                return null;
            }
            
            log.info("成功创建数据导入任务记录 - 任务ID: {}, 任务名称: {}, 用户ID: {}, 验证通过", 
                    taskId, taskInfo.getTaskName(), verifyTask.getUserId());
            
            // 创建任务的所有步骤
            log.info("开始创建任务步骤 - 任务ID: {}, 处理中心: {}", taskId, taskInfo.getProcessingCenter());
            boolean stepsCreated = cubeTaskStepService.createAllTaskSteps(taskId, taskInfo.getProcessingCenter());
            if (!stepsCreated) {
                log.warn("创建数据导入任务步骤失败 - 任务ID: {}, 但任务已创建", taskId);
            } else {
                log.info("成功创建数据导入任务所有步骤 - 任务ID: {}", taskId);
            }
            
            log.info("数据导入任务创建完成 - 任务ID: {}, 任务名称: {}, 用户ID: {}", 
                    taskId, taskInfo.getTaskName(), verifyTask.getUserId());
            
            return taskId;
            
        } catch (Exception e) {
            log.error("创建数据导入任务异常 - 任务ID: {}, 错误信息: {}", taskId, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 处理并插入切片数据到 cube_slice_info 表
     * 用于演示：直接插入预定义的演示数据
     * 
     * @param extractPath 解压后的目录路径
     * @param taskId 任务ID
     * @param createdBy 创建者
     * @return 插入的切片记录数量
     */
    private int processAndInsertSlices(Path extractPath, String taskId, String createdBy) {
        int insertedCount = 0;
        
        log.info("========== processAndInsertSlices 方法被调用 ==========");
        log.info("方法参数 - extractPath: {}, taskId: {}, createdBy: {}", extractPath, taskId, createdBy);
        
        try {
            log.info("========== 开始插入演示切片数据 ==========");
            log.info("任务ID: {}", taskId);
            log.info("创建者: {}", createdBy);
            
            // 检查 cubeSliceMapper 是否注入成功
            if (cubeSliceMapper == null) {
                log.error("✗ cubeSliceMapper 为 null，无法插入数据！");
                return 0;
            }
            log.info("✓ cubeSliceMapper 已注入");
            
            // 创建第一条演示数据
            CubeSlice slice1 = new CubeSlice();
            slice1.setCubeId("GRID_CUBE_T0_J49E017017");
            slice1.setQuarter("2025q1");
            slice1.setSlicePath("/data/cube_slices/2025q1/GRID_CUBE_T0_J49E017017_slice_q1");
            slice1.setFileName("GRID_CUBE_T0_J49E017017_slice_q1");
            slice1.setFileFormat("tif");
            slice1.setBrowseImagePath("GRID_CUBE_T0_J49E017017/GRID_CUBE_T0_J49E017017_slice_q1.jpg");
            slice1.setBrowseFileName("GRID_CUBE_T0_J49E017017_browse_q1.jpg");
            slice1.setBrowseFormat("jpg");
            slice1.setImagingTime(OffsetDateTime.of(LocalDateTime.of(2025, 10, 2, 14, 54, 40), ZoneOffset.of("+08:00")));
            slice1.setLocation("黑龙江");
            slice1.setSliceDesc("2025年Q1 N51E16区域立方体切片，含7个波段数据及可视化浏览图");
            slice1.setResolution("10m×10m");
            slice1.setCreated(OffsetDateTime.now(ZoneOffset.of("+08:00")));
            slice1.setUpdated(OffsetDateTime.now(ZoneOffset.of("+08:00")));
            slice1.setCreatedBy(createdBy != null ? createdBy : "system_user");
            
            // 打印第一条数据的所有字段
            log.info("=== 第一条演示切片数据 ===");
            log.info("cubeId: {}", slice1.getCubeId());
            log.info("quarter: {}", slice1.getQuarter());
            log.info("fileName: {}", slice1.getFileName());
            log.info("fileFormat: {}", slice1.getFileFormat());
            log.info("sliceDesc: {}", slice1.getSliceDesc());
            log.info("resolution: {}", slice1.getResolution());
            log.info("slicePath: {}", slice1.getSlicePath());
            log.info("browseImagePath: {}", slice1.getBrowseImagePath());
            log.info("browseFileName: {}", slice1.getBrowseFileName());
            log.info("browseFormat: {}", slice1.getBrowseFormat());
            log.info("imagingTime: {}", slice1.getImagingTime());
            log.info("location: {}", slice1.getLocation());
            log.info("created: {}", slice1.getCreated());
            log.info("updated: {}", slice1.getUpdated());
            log.info("createdBy: {}", slice1.getCreatedBy());
            
            // 插入第一条数据
            try {
                int result1 = cubeSliceMapper.insert(slice1);
                log.info("第一条数据插入结果: {}", result1);
                if (result1 > 0) {
                    insertedCount++;
                    log.info("✓ 成功插入第一条演示切片记录 - sliceId: {}, 文件名: {}", slice1.getSliceId(), slice1.getFileName());
                } else {
                    log.error("✗ 插入第一条演示切片记录失败 - 返回结果: {}, 文件名: {}", result1, slice1.getFileName());
                }
            } catch (Exception e1) {
                log.error("✗ 插入第一条演示切片记录异常 - 文件名: {}, 错误: {}", slice1.getFileName(), e1.getMessage(), e1);
                log.error("异常堆栈: ", e1);
            }
            
            // 创建第二条演示数据
            CubeSlice slice2 = new CubeSlice();
            slice2.setCubeId("GRID_CUBE_T0_J49E017017");
            slice2.setQuarter("2025q1");
            slice2.setSlicePath("/data/cube_slices/2025q1/GRID_CUBE_T0_J49E017017_slice_q1");
            slice2.setFileName("GRID_CUBE_T0_J49E017017_slice_q1.sat.tif");
            slice2.setFileFormat("tif");
            slice2.setBrowseImagePath("GRID_CUBE_T0_J49E017017/GRID_CUBE_T0_J49E017017_slice_q1.sat.png");
            slice2.setBrowseFileName("GRID_CUBE_T0_J49E017017_browse_q1.jpg");
            slice2.setBrowseFormat("png");
            slice2.setImagingTime(OffsetDateTime.of(LocalDateTime.of(2025, 10, 2, 14, 54, 40), ZoneOffset.of("+08:00")));
            slice2.setLocation("黑龙江");
            slice2.setSliceDesc("第一季度原始数据载荷描述文件");
            slice2.setResolution("2798, 2336");
            slice2.setCreated(OffsetDateTime.now(ZoneOffset.of("+08:00")));
            slice2.setUpdated(OffsetDateTime.now(ZoneOffset.of("+08:00")));
            slice2.setCreatedBy(createdBy != null ? createdBy : "system_user");
            
            // 打印第二条数据的所有字段
            log.info("=== 第二条演示切片数据 ===");
            log.info("cubeId: {}", slice2.getCubeId());
            log.info("quarter: {}", slice2.getQuarter());
            log.info("fileName: {}", slice2.getFileName());
            log.info("fileFormat: {}", slice2.getFileFormat());
            log.info("sliceDesc: {}", slice2.getSliceDesc());
            log.info("resolution: {}", slice2.getResolution());
            log.info("slicePath: {}", slice2.getSlicePath());
            log.info("browseImagePath: {}", slice2.getBrowseImagePath());
            log.info("browseFileName: {}", slice2.getBrowseFileName());
            log.info("browseFormat: {}", slice2.getBrowseFormat());
            log.info("imagingTime: {}", slice2.getImagingTime());
            log.info("location: {}", slice2.getLocation());
            log.info("created: {}", slice2.getCreated());
            log.info("updated: {}", slice2.getUpdated());
            log.info("createdBy: {}", slice2.getCreatedBy());
            
            // 插入第二条数据
            try {
                int result2 = cubeSliceMapper.insert(slice2);
                log.info("第二条数据插入结果: {}", result2);
                if (result2 > 0) {
                    insertedCount++;
                    log.info("✓ 成功插入第二条演示切片记录 - sliceId: {}, 文件名: {}", slice2.getSliceId(), slice2.getFileName());
                } else {
                    log.error("✗ 插入第二条演示切片记录失败 - 返回结果: {}, 文件名: {}", result2, slice2.getFileName());
                }
            } catch (Exception e2) {
                log.error("✗ 插入第二条演示切片记录异常 - 文件名: {}, 错误: {}", slice2.getFileName(), e2.getMessage(), e2);
                log.error("异常堆栈: ", e2);
            }
            
            log.info("========== 演示切片数据插入完成 - 共插入 {} 条记录 ==========", insertedCount);
            
        } catch (Exception e) {
            log.error("========== 插入演示切片数据异常 ==========");
            log.error("错误信息: {}", e.getMessage(), e);
            log.error("异常类型: {}", e.getClass().getName());
            log.error("异常堆栈: ", e);
            e.printStackTrace();
        }
        
        return insertedCount;
    }
    
    /**
     * 递归扫描目录下的所有文件
     */
    private List<Path> scanAllFiles(Path directory) throws IOException {
        List<Path> fileList = new ArrayList<>();
        
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return fileList;
        }
        
        // FileVisitor 不是 AutoCloseable，不能使用 try-with-resources
        java.nio.file.FileVisitor<Path> visitor = new java.nio.file.SimpleFileVisitor<Path>() {
            @Override
            public java.nio.file.FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) {
                fileList.add(file);
                return java.nio.file.FileVisitResult.CONTINUE;
            }
            
            @Override
            public java.nio.file.FileVisitResult visitFileFailed(Path file, IOException exc) {
                log.warn("访问文件失败: {}", file);
                return java.nio.file.FileVisitResult.CONTINUE;
            }
        };
        
        try {
            Files.walkFileTree(directory, visitor);
        } catch (IOException e) {
            log.error("遍历目录失败: {}", directory, e);
        }
        
        return fileList;
    }
    
    /**
     * 解析文件并创建 CubeSlice 对象
     * 
     * @param filePath 文件路径
     * @param extractPath 解压根目录
     * @param taskId 任务ID
     * @param createdBy 创建者
     * @return CubeSlice 对象，如果解析失败返回null
     */
    private CubeSlice parseFileToSlice(Path filePath, Path extractPath, String taskId, String createdBy) {
        try {
            String fileName = filePath.getFileName().toString();
            String relativePath = extractPath.relativize(filePath).toString().replace('\\', '/');
            
            log.debug("解析文件 - 文件名: {}, 相对路径: {}", fileName, relativePath);
            
            // 获取文件扩展名
            String fileExtension = getFileExtension(fileName).toLowerCase();
            
            // 跳过非图像和数据文件
            if (!isDataFile(fileExtension) && !isImageFile(fileExtension)) {
                log.debug("跳过非数据/图像文件: {}", fileName);
                return null;
            }
            
            // 解析季度信息（从文件名或路径中提取）
            String quarter = extractQuarter(fileName, relativePath);
            
            // 构建切片路径（使用相对路径，确保所有路径都使用固定的立方体ID）
            String slicePath = buildSlicePath(relativePath, quarter);
            
            // 判断是否为浏览图文件
            boolean isBrowseImage = isBrowseImageFile(fileName);
            
            // 创建 CubeSlice 对象
            CubeSlice slice = new CubeSlice();
            
            // 使用固定的立方体ID（根据用户要求）
            slice.setCubeId(DEFAULT_CUBE_ID);
            slice.setQuarter(quarter);
            slice.setSlicePath(slicePath);
            slice.setFileName(replaceCubeIdInFileName(fileName));
            slice.setFileFormat(fileExtension);
            
            // 如果是浏览图文件，设置浏览图相关字段
            if (isBrowseImage) {
                slice.setBrowseImagePath(buildBrowseImagePath(relativePath));
                slice.setBrowseFileName(replaceCubeIdInFileName(fileName));
                slice.setBrowseFormat(fileExtension);
            } else {
                // 尝试查找对应的浏览图
                Path browseImagePath = findBrowseImage(filePath);
                if (browseImagePath != null) {
                    String browseRelativePath = extractPath.relativize(browseImagePath).toString().replace('\\', '/');
                    slice.setBrowseImagePath(buildBrowseImagePath(browseRelativePath));
                    slice.setBrowseFileName(replaceCubeIdInFileName(browseImagePath.getFileName().toString()));
                    slice.setBrowseFormat(getFileExtension(browseImagePath.getFileName().toString()).toLowerCase());
                }
            }
            
            // 设置其他字段
            slice.setImagingTime(OffsetDateTime.of(LocalDateTime.of(2025, 2, 15, 10, 30, 0), ZoneOffset.of("+08:00")));
            slice.setLocation("黑龙江");
            
            // 根据文件类型设置描述
            if (fileName.contains(".sat.") || fileName.contains("sat")) {
                slice.setSliceDesc("第一季度原始数据载荷描述文件");
                slice.setResolution("2798, 2336");
            } else {
                slice.setSliceDesc("2025年Q1 " + DEFAULT_CUBE_ID.replace("GRID_CUBE_T0_", "") + "区域立方体切片，含7个波段数据及可视化浏览图");
                slice.setResolution("10m×10m");
            }
            
            slice.setCreated(OffsetDateTime.now());
            slice.setUpdated(OffsetDateTime.now());
            slice.setCreatedBy(createdBy);
            
            return slice;
            
        } catch (Exception e) {
            log.error("解析文件失败 - 文件路径: {}, 错误: {}", filePath, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1);
        }
        return "";
    }
    
    /**
     * 判断是否为数据文件
     */
    private boolean isDataFile(String extension) {
        return extension.matches("(?i)(tif|tiff|hdf|hdf5|nc|netcdf|zarr|cog|jp2|jpg|jpeg|png)");
    }
    
    /**
     * 判断是否为图像文件
     */
    private boolean isImageFile(String extension) {
        return extension.matches("(?i)(jpg|jpeg|png|gif|bmp|webp)");
    }
    
    /**
     * 判断是否为浏览图文件
     */
    private boolean isBrowseImageFile(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.contains("browse") || lowerName.contains("preview") || 
               lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png");
    }
    
    /**
     * 从文件名或路径中提取季度信息
     */
    private String extractQuarter(String fileName, String relativePath) {
        // 尝试从文件名中提取季度信息
        Pattern quarterPattern = Pattern.compile("(?i)(q[1-4]|2025q[1-4]|q[1-4]2025)");
        Matcher matcher = quarterPattern.matcher(fileName + " " + relativePath);
        if (matcher.find()) {
            String quarter = matcher.group(1).toLowerCase();
            // 标准化格式为 2025q1
            if (quarter.startsWith("q")) {
                return "2025" + quarter;
            } else if (quarter.contains("q")) {
                return quarter;
            }
        }
        
        // 默认返回 2025q1
        return "2025q1";
    }
    
    /**
     * 构建切片路径（替换所有出现的立方体ID为固定的ID）
     */
    private String buildSlicePath(String relativePath, String quarter) {
        // 替换路径中的立方体ID为固定的ID
        String path = replaceCubeIdInPath(relativePath);
        // 构建标准路径格式：/data/cube_slices/{quarter}/{cube_id}_slice_{quarter}
        return "/data/cube_slices/" + quarter + "/" + DEFAULT_CUBE_ID + "_slice_" + quarter.replace("2025", "");
    }
    
    /**
     * 构建浏览图路径
     */
    private String buildBrowseImagePath(String relativePath) {
        // 替换路径中的立方体ID为固定的ID
        String path = replaceCubeIdInPath(relativePath);
        // 构建标准路径格式：{cube_id}/{browse_file_name}
        String fileName = Paths.get(path).getFileName().toString();
        return DEFAULT_CUBE_ID + "/" + replaceCubeIdInFileName(fileName);
    }
    
    /**
     * 替换路径中的立方体ID为固定的ID
     */
    private String replaceCubeIdInPath(String path) {
        // 匹配 GRID_CUBE_T0_ 开头的立方体ID模式
        Pattern cubeIdPattern = Pattern.compile("GRID_CUBE_T0_[A-Z]\\d+E\\d+");
        return cubeIdPattern.matcher(path).replaceAll(DEFAULT_CUBE_ID);
    }
    
    /**
     * 替换文件名中的立方体ID为固定的ID
     */
    private String replaceCubeIdInFileName(String fileName) {
        // 匹配 GRID_CUBE_T0_ 开头的立方体ID模式
        Pattern cubeIdPattern = Pattern.compile("GRID_CUBE_T0_[A-Z]\\d+E\\d+");
        return cubeIdPattern.matcher(fileName).replaceAll(DEFAULT_CUBE_ID);
    }
    
    /**
     * 查找对应的浏览图文件
     */
    private Path findBrowseImage(Path dataFile) {
        Path parent = dataFile.getParent();
        String baseName = getBaseNameWithoutExtension(dataFile.getFileName().toString());
        
        // 查找同名的图像文件
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        for (String ext : imageExtensions) {
            Path browseImage = parent.resolve(baseName + ext);
            if (Files.exists(browseImage)) {
                return browseImage;
            }
        }
        
        // 查找包含 "browse" 或 "preview" 的文件
        try {
            if (parent != null && Files.isDirectory(parent)) {
                try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(parent)) {
                    for (Path file : stream) {
                        String fileName = file.getFileName().toString().toLowerCase();
                        if (fileName.contains("browse") || fileName.contains("preview")) {
                            if (isImageFile(getFileExtension(fileName))) {
                                return file;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.warn("查找浏览图文件失败: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 获取不带扩展名的文件名
     */
    private String getBaseNameWithoutExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(0, lastDot);
        }
        return fileName;
    }
    
    /**
     * 文件项实体
     */
    public static class FileItem {
        private String name;
        private boolean isDirectory;
        private long size;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public boolean isDirectory() {
            return isDirectory;
        }
        
        public void setDirectory(boolean directory) {
            isDirectory = directory;
        }
        
        public long getSize() {
            return size;
        }
        
        public void setSize(long size) {
            this.size = size;
        }
    }
}

