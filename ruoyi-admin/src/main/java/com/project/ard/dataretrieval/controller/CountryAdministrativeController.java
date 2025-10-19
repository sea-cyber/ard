package com.project.ard.dataretrieval.controller;

import com.project.common.core.controller.BaseController;
import com.project.common.core.domain.AjaxResult;
import com.project.ard.dataretrieval.domain.CountryAdministrative;
import com.project.ard.dataretrieval.service.ICountryAdministrativeService;
import com.project.ard.dataretrieval.service.IProvinceAdministrativeService;
import com.project.ard.dataretrieval.service.IMunicipalAdministrativeService;
import com.project.ard.dataretrieval.service.IDistrictAdministrativeService;
import com.project.ard.dataretrieval.domain.vo.AdminGeometryRequest;
import com.project.ard.dataretrieval.service.ICountryService;
import com.project.ard.dataretrieval.service.IProvinceService;
import com.project.ard.dataretrieval.service.ICityService;
import com.project.ard.dataretrieval.service.IDistrictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 国家行政区划Controller
 * 
 * @author ard
 */
@Api(tags = "国家行政区划管理")
@RestController
@RequestMapping("/ard/dataretrieval")
public class CountryAdministrativeController extends BaseController {

    @Autowired
    private ICountryAdministrativeService countryAdministrativeService;

    @Autowired
    private ICountryService countryService;

    @Autowired
    private IProvinceService provinceService;

    @Autowired
    private ICityService cityService;

    @Autowired
    private IDistrictService districtService;

    @Autowired
    private IProvinceAdministrativeService provinceAdministrativeService;

    @Autowired
    private IMunicipalAdministrativeService municipalAdministrativeService;

    @Autowired
    private IDistrictAdministrativeService districtAdministrativeService;

    /**
     * 根据类型与代码查询行政区几何(GeoJSON)
     * 行政区数据为公开数据,所有登录用户均可访问
     */
    @ApiOperation("根据类型与代码查询行政区几何(GeoJSON)")
    @PostMapping("/geometry/byType")
    public AjaxResult getGeometryByType(@RequestBody AdminGeometryRequest request) {
        if (request == null || request.getType() == null || request.getCode() == null) {
            return error("type 与 code 为必填");
        }
        switch (request.getType()) {
            case 1:
                return success(countryAdministrativeService.selectByCode(request.getCode()));
            case 2:
                return success(provinceAdministrativeService.selectByCode(request.getCode()));
            case 3:
                return success(municipalAdministrativeService.selectByCode(request.getCode()));
            case 4:
                return success(districtAdministrativeService.selectByCode(request.getCode()));
            default:
                return error("不支持的行政区类型: " + request.getType());
        }
    }
    /**
     * 根据名称查询国家行政区划
     * 行政区数据为公开数据,所有登录用户均可访问
     */
    @ApiOperation("根据名称查询国家行政区划")
    @GetMapping("/geometry/name")
//    AjaxResult
    public AjaxResult getByName(@ApiParam("行政区划代码") @RequestParam("name") String name) {
        CountryAdministrative country = countryAdministrativeService.selectByName(name);
        if (country != null) {
            return success(country);
        } else {
            return error("未找到代码为 " + name + " 的行政区划");
        }
//        String result="测试";
//        return result;
    }


    /**
     * 根据regionId查询国家(country)
     * 行政区数据为公开数据，所有登录用户均可访问
     */
    @ApiOperation("根据regionId查询国家(country)")
    @GetMapping("/region/country")
    public AjaxResult getCountryByRegionId(@ApiParam("regionId") @RequestParam("regionId") String regionId) {
        return success(countryService.selectByRegionId(regionId));
    }

    /**
     * 根据regionId查询省(province)
     * 行政区数据为公开数据,所有登录用户均可访问
     */
    @ApiOperation("根据regionId查询省(province)")
    @GetMapping("/region/province")
    public AjaxResult getProvinceByRegionId(@ApiParam("regionId") @RequestParam("regionId") String regionId) {
        // 返回该国家下所有省份列表
        return success(provinceService.selectByParentRegionId(regionId));
    }

    /**
     * 根据regionId查询市(city)
     * 行政区数据为公开数据,所有登录用户均可访问
     */
    @ApiOperation("根据regionId查询市(city)")
    @GetMapping("/region/city")
    public AjaxResult getCityByRegionId(@ApiParam("regionId") @RequestParam("regionId") String regionId) {
        // 返回该省份下所有城市列表
        return success(cityService.selectByParentRegionId(regionId));
    }

    /**
     * 根据regionId查询区县(district)
     * 行政区数据为公开数据,所有登录用户均可访问
     */
    @ApiOperation("根据regionId查询区县(district)")
    @GetMapping("/region/district")
    public AjaxResult getDistrictByRegionId(@ApiParam("regionId") @RequestParam("regionId") String regionId) {
        // 返回该城市下所有区县列表
        return success(districtService.selectByParentRegionId(regionId));
    }
    
}
