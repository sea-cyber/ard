# TIF数据检索模块

## 概述
基于MyBatis Plus的TIF数据检索模块，提供完整的数据查询、下载、预览等功能。

## 技术栈
- Spring Boot 2.5.15
- MyBatis Plus 3.5.3.1
- PostgreSQL 17
- Lombok

## 模块结构

### 1. 实体类 (Domain)
- `RsTifFile.java` - TIF文件信息实体类
  - 使用MyBatis Plus注解
  - 包含所有数据库字段映射
  - 支持Lombok自动生成getter/setter

### 2. 数据访问层 (Mapper)
- `RsTifFileMapper.java` - 数据访问接口
  - 继承MyBatis Plus的BaseMapper
  - 提供自定义查询方法
  - 支持分页查询

- `RsTifFileMapper.xml` - SQL映射文件
  - 复杂查询SQL
  - 支持PostGIS空间查询
  - 动态SQL条件

### 3. 业务逻辑层 (Service)
- `IRsTifFileService.java` - 服务接口
  - 继承MyBatis Plus的IService
  - 定义业务方法

- `RsTifFileServiceImpl.java` - 服务实现
  - 实现具体业务逻辑
  - 文件下载链接生成
  - 预览图链接生成

### 4. 控制器层 (Controller)
- `TifRetrievalController.java` - REST API控制器
  - 提供完整的RESTful接口
  - 支持数据检索、详情查看、下载、预览等功能

### 5. 配置类
- `MybatisPlusConfig.java` - MyBatis Plus配置
  - 分页插件配置
  - PostgreSQL数据库支持

## API接口

### 1. 数据检索
```
POST /ard/dataretrieval/tif/search
```
根据条件检索TIF数据，支持分页。

### 2. 数据详情
```
GET /ard/dataretrieval/tif/detail/{dataId}
```
获取指定数据的详细信息。

### 3. 数据下载
```
GET /ard/dataretrieval/tif/download/{dataId}
```
生成数据下载链接。

### 4. 预览图
```
GET /ard/dataretrieval/tif/preview/{dataId}
```
获取数据预览图链接。

### 5. 批量下载
```
POST /ard/dataretrieval/tif/batchDownload
```
批量下载多个数据文件。

### 6. 卫星列表
```
GET /ard/dataretrieval/tif/satellites
```
获取可用的卫星列表。

### 7. 数据类型
```
GET /ard/dataretrieval/tif/dataTypes
```
获取可用的数据类型列表。

## 数据库表结构

### rs_tif_file 表
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | int4 | 主键ID |
| base_id | int8 | 基础ID |
| filename | varchar(255) | 文件名 |
| file_path | varchar(255) | 文件路径 |
| satellite_id | varchar(50) | 卫星ID |
| sensor_id | varchar(50) | 传感器ID |
| product_id | varchar(50) | 产品ID |
| acquisition_time | timestamptz(6) | 获取时间 |
| input_time | timestamptz(6) | 输入时间 |
| cloud_percent | numeric | 云量百分比 |
| orbit_id | int4 | 轨道ID |
| scene_path | int4 | 场景路径 |
| scene_row | int4 | 场景行 |
| quick_view_uri | varchar | 快视图URI |
| has_pair | int2 | 是否有配对 |
| has_entity | int2 | 是否有实体 |
| laser_count | int4 | 激光计数 |
| boundary | geometry(GEOMETRY) | 边界几何信息 |
| in_cart | text | 购物车信息 |
| tar_input_time | timestamptz(6) | 目标输入时间 |

## 配置说明

### application.yml
```yaml
# MyBatis Plus配置
mybatis-plus:
  type-aliases-package: com.project.**.domain
  mapper-locations: classpath*:mapper/**/*Mapper.xml
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: delFlag
      logic-delete-value: 2
      logic-not-delete-value: 0
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    call-setters-on-nulls: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 使用说明

1. 确保PostgreSQL数据库已创建并导入表结构
2. 配置数据库连接信息
3. 启动应用
4. 通过Swagger UI测试接口：http://localhost:8080/swagger-ui/

## 注意事项

1. 需要PostGIS扩展支持空间查询
2. 文件存储路径需要根据实际环境配置
3. 下载链接生成逻辑需要根据实际文件存储方式调整
4. 预览图生成需要根据实际需求实现