package com.project.ard.dataretrieval.controller;

import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
            
            // 4. TODO: 后续处理
            // - 读取元数据
            // - 验证数据格式
            // - 保存到数据库
            // - 移动文件到最终存储目录
            
            return AjaxResult.success("数据导入成功，共处理 " + extractedItems.size() + " 个文件/文件夹");
            
        } catch (Exception e) {
            log.error("导入数据失败", e);
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

