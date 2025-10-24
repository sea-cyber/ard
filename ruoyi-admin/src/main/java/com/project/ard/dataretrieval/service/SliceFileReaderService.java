package com.project.ard.dataretrieval.service;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 切片文件读取服务
 * 用于读取和处理遥感切片文件
 */
@Service
public class SliceFileReaderService {
    
    static {
        // 初始化GDAL
        // 注意：PROJ数据库警告不影响计算结果的正确性
        gdal.AllRegister();
    }
    
    /**
     * 读取切片文件信息
     * @param filePath 文件路径
     * @return 文件信息
     */
    public Map<String, Object> readSliceFileInfo(String filePath) {
        Map<String, Object> fileInfo = new HashMap<>();
        
        try {
            // 检查文件是否存在
            File file = new File(filePath);
            if (!file.exists()) {
                fileInfo.put("status", "error");
                fileInfo.put("message", "文件不存在: " + filePath);
                return fileInfo;
            }
            
            // 使用GDAL打开文件
            Dataset dataset = gdal.Open(filePath, gdalconstConstants.GA_ReadOnly);
            if (dataset == null) {
                fileInfo.put("status", "error");
                fileInfo.put("message", "无法打开文件: " + filePath);
                return fileInfo;
            }
            
            // 读取基本信息
            fileInfo.put("status", "success");
            fileInfo.put("filePath", filePath);
            fileInfo.put("fileName", file.getName());
            fileInfo.put("fileSize", file.length());
            fileInfo.put("width", dataset.GetRasterXSize());
            fileInfo.put("height", dataset.GetRasterYSize());
            fileInfo.put("bandCount", dataset.GetRasterCount());
            fileInfo.put("dataType", dataset.GetRasterBand(1).GetRasterDataType());
            
            // 读取地理信息
            double[] geoTransform = new double[6];
            dataset.GetGeoTransform(geoTransform);
            fileInfo.put("geoTransform", geoTransform);
            
            // 读取投影信息
            String projection = dataset.GetProjection();
            fileInfo.put("projection", projection);
            
            // 读取波段信息
            Map<String, Object> bandInfo = new HashMap<>();
            for (int i = 1; i <= dataset.GetRasterCount(); i++) {
                Map<String, Object> band = new HashMap<>();
                band.put("bandNumber", i);
                band.put("dataType", dataset.GetRasterBand(i).GetRasterDataType());
                Double[] noDataValue = new Double[1];
                dataset.GetRasterBand(i).GetNoDataValue(noDataValue);
                band.put("noDataValue", noDataValue[0]);
                
                // 读取统计信息
                double[] minMax = new double[2];
                dataset.GetRasterBand(i).ComputeRasterMinMax(minMax);
                band.put("minValue", minMax[0]);
                band.put("maxValue", minMax[1]);
                
                bandInfo.put("band_" + i, band);
            }
            fileInfo.put("bands", bandInfo);
            
            // 关闭数据集
            dataset.delete();
            
        } catch (Exception e) {
            fileInfo.put("status", "error");
            fileInfo.put("message", "读取文件失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fileInfo;
    }
    
    /**
     * 读取切片文件的波段数据
     * @param filePath 文件路径
     * @param bandNumber 波段号（从1开始）
     * @return 波段数据
     */
    public Map<String, Object> readBandData(String filePath, int bandNumber) {
        Map<String, Object> bandData = new HashMap<>();
        
        try {
            Dataset dataset = gdal.Open(filePath, gdalconstConstants.GA_ReadOnly);
            if (dataset == null) {
                bandData.put("status", "error");
                bandData.put("message", "无法打开文件: " + filePath);
                return bandData;
            }
            
            if (bandNumber < 1 || bandNumber > dataset.GetRasterCount()) {
                bandData.put("status", "error");
                bandData.put("message", "波段号超出范围: " + bandNumber);
                dataset.delete();
                return bandData;
            }
            
            // 读取波段数据
            int width = dataset.GetRasterXSize();
            int height = dataset.GetRasterYSize();
            int[] data = new int[width * height];
            
            dataset.GetRasterBand(bandNumber).ReadRaster(0, 0, width, height, width, height, 
                gdalconstConstants.GDT_Int32, data);
            
            bandData.put("status", "success");
            bandData.put("filePath", filePath);
            bandData.put("bandNumber", bandNumber);
            bandData.put("width", width);
            bandData.put("height", height);
            bandData.put("data", data);
            
            // 读取地理空间信息
            double[] geoTransform = new double[6];
            dataset.GetGeoTransform(geoTransform);
            bandData.put("geotransform", geoTransform);
            
            String projection = dataset.GetProjection();
            bandData.put("projection", projection);
            
            // 计算统计信息
            double[] minMax = new double[2];
            dataset.GetRasterBand(bandNumber).ComputeRasterMinMax(minMax);
            bandData.put("minValue", minMax[0]);
            bandData.put("maxValue", minMax[1]);
            
            dataset.delete();
            
        } catch (Exception e) {
            bandData.put("status", "error");
            bandData.put("message", "读取波段数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bandData;
    }
    
    /**
     * 检查文件是否为有效的遥感数据
     * @param filePath 文件路径
     * @return 检查结果
     */
    public boolean isValidRasterFile(String filePath) {
        try {
            Dataset dataset = gdal.Open(filePath, gdalconstConstants.GA_ReadOnly);
            if (dataset == null) {
                return false;
            }
            
            boolean isValid = dataset.GetRasterCount() > 0 && 
                             dataset.GetRasterXSize() > 0 && 
                             dataset.GetRasterYSize() > 0;
            
            dataset.delete();
            return isValid;
            
        } catch (Exception e) {
            return false;
        }
    }
}
