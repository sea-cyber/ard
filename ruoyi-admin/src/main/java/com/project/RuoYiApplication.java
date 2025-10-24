package com.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@MapperScan("com.project.**.mapper")
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        // GDAL测试
        testGdal();
        
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  若依启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'‘              ");
    }
    
    /**
     * 简单的GDAL测试
     */
    private static void testGdal() {
        try {
            System.out.println("=== GDAL 测试开始 ===");
            
            // 初始化GDAL
            gdal.AllRegister();
            System.out.println("✓ GDAL 初始化成功");
            
            // 获取版本信息
            String version = gdal.VersionInfo();
            System.out.println("✓ GDAL 版本: " + version);
            
            // 获取驱动数量
            int driverCount = gdal.GetDriverCount();
            System.out.println("✓ 可用驱动数量: " + driverCount);
            
            System.out.println("=== GDAL 测试完成 ===\n");
            
        } catch (Exception e) {
            System.err.println("✗ GDAL 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
    