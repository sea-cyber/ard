# 立方体数据测试说明

## 数据库表结构

根据提供的SQL创建了`cube`表，包含以下字段：

- `id`: 主键ID (自增)
- `cube_name`: 立方体名称 (唯一)
- `create_user`: 创建用户
- `boundary`: 边界几何信息 (PostGIS geometry类型)
- `create_time`: 创建时间
- `data_type`: 数据类型 (ARD, HDF5, TIF, NETCDF, ZARR)
- `data_describe`: 数据描述
- `compression_algorithm`: 压缩算法 (LZW, DEFLATE, JPEG2000, ZSTD, LZ4, NONE)

## 测试数据

`insert_test_cube_data.sql` 文件包含了10条测试数据，涵盖：

1. **TIF格式数据**: GF-1, GF-2, Landsat8, Sentinel2等卫星数据
2. **HDF5格式数据**: MODIS数据
3. **ARD格式数据**: 分析就绪数据
4. **NetCDF格式数据**: 气候数据
5. **Zarr格式数据**: 数组格式数据

## 使用方法

1. 确保PostgreSQL数据库已安装PostGIS扩展
2. 执行`insert_test_cube_data.sql`插入测试数据
3. 启动后端服务
4. 前端调用`/ard/dataretrieval/cube/search`接口进行查询

## API接口

### 查询立方体数据
- **URL**: `POST /ard/dataretrieval/cube/search`
- **请求体**: TifRetrievalRequest对象
- **响应**: TableDataInfo格式的分页数据

### 查询参数说明
- `dataType.type`: 数据类型过滤
- `timeRange.beginTime/endTime`: 时间范围过滤
- `page.current/size`: 分页参数

## 注意事项

1. 边界数据使用PostGIS的geometry类型存储
2. 查询时使用`ST_AsGeoJSON(boundary)`转换为GeoJSON格式
3. 支持按数据类型和时间范围进行过滤
4. 分页查询使用MyBatis-Plus的分页插件
