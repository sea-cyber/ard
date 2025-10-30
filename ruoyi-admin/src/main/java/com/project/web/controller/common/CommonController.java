package com.project.web.controller.common;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.project.common.config.RuoYiConfig;
import com.project.common.core.domain.AjaxResult;
import com.project.common.utils.StringUtils;
import com.project.common.utils.file.FileUploadUtils;
import com.project.common.utils.file.FileUtils;
import com.project.framework.config.ServerConfig;
import com.project.ard.dataretrieval.config.UserDataConfig;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.project.common.annotation.Anonymous;

/**
 * 通用请求处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/common")
public class CommonController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;
    
    @Autowired
    private UserDataConfig userDataConfig;

    private static final String FILE_DELIMETER = ",";

    /**
     * 通用下载请求
     * 
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            if (!FileUtils.checkAllowDownload(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = RuoYiConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            for (MultipartFile file : files)
            {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载/预览
     * 支持图片预览（inline模式）和文件下载（attachment模式）
     */
    @Anonymous
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        try
        {
            if (!FileUtils.checkAllowDownload(resource))
            {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            
            // 检查是否为图片文件（用于预览）
            boolean isImage = resource != null && (
                resource.toLowerCase().endsWith(".jpg") ||
                resource.toLowerCase().endsWith(".jpeg") ||
                resource.toLowerCase().endsWith(".png") ||
                resource.toLowerCase().endsWith(".gif") ||
                resource.toLowerCase().endsWith(".bmp") ||
                resource.toLowerCase().endsWith(".webp")
            );
            
            // 如果是用户数据目录下的文件，使用用户数据根目录
            String downloadPath;
            if (resource != null && (resource.contains("ARD_CUB_GRIDT0_USR") || resource.startsWith("/default_user"))) {
                // 用户数据文件，使用用户数据根目录配置
                // 从配置获取用户数据根目录
                String userDataRootPath = userDataConfig.getDataRootPath();
                // 移除开头的斜杠（如果有）
                String resourcePath = resource.startsWith("/") ? resource.substring(1) : resource;
                downloadPath = userDataRootPath + "/" + resourcePath;
            } else {
                // 本地资源路径（原有逻辑）
                String localPath = RuoYiConfig.getProfile();
                // 数据库资源地址
                downloadPath = localPath + FileUtils.stripPrefix(resource);
            }
            
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            
            // 如果是图片文件，使用预览模式（inline），否则使用下载模式（attachment）
            if (isImage) {
                // 设置Content-Type为图片类型
                String contentType = "image/jpeg";
                if (resource.toLowerCase().endsWith(".png")) {
                    contentType = "image/png";
                } else if (resource.toLowerCase().endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (resource.toLowerCase().endsWith(".bmp")) {
                    contentType = "image/bmp";
                } else if (resource.toLowerCase().endsWith(".webp")) {
                    contentType = "image/webp";
                }
                response.setContentType(contentType);
                // 设置inline模式，让浏览器直接显示图片
                response.setHeader("Content-Disposition", "inline; filename=\"" + downloadName + "\"");
            } else {
                // 普通文件，使用下载模式
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                FileUtils.setAttachmentResponseHeader(response, downloadName);
            }
            
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 图片/资源预览（始终 inline），自动内容类型，支持用户数据目录及绝对路径
     * 用法：/common/view/resource?resource=/default_user/ARD_CUB_GRIDT0_default_user_VIZ/{cubeId}/xxx.jpg
     */
    @Anonymous
    @GetMapping("/view/resource")
    public void viewResource(String resource, HttpServletResponse response) {
        try {
            if (!FileUtils.checkAllowDownload(resource)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许预览。 ", resource));
            }

            // 解析物理路径：优先支持绝对路径（Windows），否则按用户数据根或profile解析
            String downloadPath;
            if (resource != null && resource.matches("^[A-Za-z]:[\\\\/].*")) {
                // 绝对路径（如 D:/... 或 D:\...）
                downloadPath = resource;
            } else if (resource != null && (resource.contains("ARD_CUB_GRIDT0_USR") || resource.startsWith("/default_user"))) {
                String userDataRootPath = userDataConfig.getDataRootPath();
                String resourcePath = resource.startsWith("/") ? resource.substring(1) : resource;
                downloadPath = userDataRootPath + "/" + resourcePath;
            } else {
                String localPath = RuoYiConfig.getProfile();
                downloadPath = localPath + FileUtils.stripPrefix(resource);
            }

            // 物理文件检查
            File file = new File(downloadPath);
            if (!file.exists() || !file.isFile()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                log.warn("资源不存在: {}", downloadPath);
                return;
            }

            // 猜测内容类型
            String contentType = null;
            try {
                contentType = Files.probeContentType(Paths.get(downloadPath));
            } catch (Exception ignore) { }
            if (StringUtils.isEmpty(contentType)) {
                String lower = resource != null ? resource.toLowerCase() : "";
                if (lower.endsWith(".png")) contentType = "image/png";
                else if (lower.endsWith(".gif")) contentType = "image/gif";
                else if (lower.endsWith(".bmp")) contentType = "image/bmp";
                else if (lower.endsWith(".webp")) contentType = "image/webp";
                else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) contentType = "image/jpeg";
                else contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            // 设置CORS与inline预览
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET,OPTIONS");
            response.setContentType(contentType);
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setHeader("Content-Disposition", "inline; filename=\"" + downloadName + "\"");
            response.setHeader("Cache-Control", "public, max-age=604800");
            try {
                response.setHeader("Content-Length", String.valueOf(file.length()));
            } catch (Exception ignore) { }

            // 输出内容
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        } catch (Exception e) {
            log.error("预览资源失败", e);
        }
    }
}
