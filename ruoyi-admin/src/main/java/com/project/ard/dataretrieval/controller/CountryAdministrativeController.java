package com.project.ard.dataretrieval.controller;

import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import com.project.ard.dataretrieval.domain.CountryAdministrative;
import com.project.ard.dataretrieval.service.ICountryAdministrativeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 国家行政区划Controller
 * 
 * @author ard
 */
@Api(tags = "国家行政区划管理")
@RestController
@RequestMapping("/ard/dataretrieval/country")
public class CountryAdministrativeController extends BaseController {

    @Autowired
    private ICountryAdministrativeService countryAdministrativeService;

   

    /**
     * 根据代码查询国家行政区划
     */
    @ApiOperation("根据代码查询国家行政区划")
    @PreAuthorize("@ss.hasPermi('dataretrieval:country:query')")
    @GetMapping("/code")
    public AjaxResult getByCode(@ApiParam("行政区划代码") @RequestParam("code") String code) {
        CountryAdministrative country = countryAdministrativeService.selectByCode(code);
        if (country != null) {
            return success(country);
        } else {
            return error("未找到代码为 " + code + " 的行政区划");
        }
    }

    /**
     * 测试几何数据转换
     */
    @ApiOperation("测试几何数据转换")
    @GetMapping("/test-geom")
    public AjaxResult testGeomConversion(@ApiParam("行政区划代码") @RequestParam("code") String code) {
        try {
            CountryAdministrative country = countryAdministrativeService.selectByCode(code);
            if (country != null) {
                return success(country);
            } else {
                return error("未找到代码为 " + code + " 的行政区划");
            }
        } catch (Exception e) {
            return error("几何数据转换失败: " + e.getMessage());
        }
    }
    
   
}
