# PostgreSQL + PostGIS 空间分析优化指南

## 1. 数据库配置优化

### 1.1 PostgreSQL 配置优化
```sql
-- 在 postgresql.conf 中添加以下配置
shared_buffers = 256MB                    -- 根据内存调整
effective_cache_size = 1GB                -- 根据内存调整
work_mem = 4MB                            -- 用于排序和哈希操作
maintenance_work_mem = 64MB               -- 用于维护操作
random_page_cost = 1.1                    -- SSD存储优化
effective_io_concurrency = 200            -- SSD并发优化
```

### 1.2 PostGIS 扩展配置
```sql
-- 检查PostGIS版本和功能
SELECT PostGIS_Version();
SELECT PostGIS_Full_Version();

-- 启用PostGIS扩展
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
```

## 2. 空间索引优化

### 2.1 创建空间索引
```sql
-- 为boundary字段创建GIST空间索引
CREATE INDEX CONCURRENTLY idx_rs_tif_file_boundary_gist 
ON rs_tif_file USING GIST (boundary);

-- 检查索引创建状态
SELECT schemaname, tablename, indexname, indexdef 
FROM pg_indexes 
WHERE tablename = 'rs_tif_file';
```

### 2.2 索引维护
```sql
-- 更新表统计信息
ANALYZE rs_tif_file;

-- 重建索引（如果需要）
REINDEX INDEX CONCURRENTLY idx_rs_tif_file_boundary_gist;

-- 检查索引使用情况
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes 
WHERE tablename = 'rs_tif_file';
```

## 3. 查询优化策略

### 3.1 边界框预过滤
```sql
-- 使用 && 操作符进行边界框预过滤（利用空间索引）
SELECT * FROM rs_tif_file 
WHERE boundary && ST_GeomFromGeoJSON('{"type":"Polygon",...}')
AND ST_Intersects(boundary, ST_GeomFromGeoJSON('{"type":"Polygon",...}'));
```

### 3.2 查询计划分析
```sql
-- 分析查询执行计划
EXPLAIN (ANALYZE, BUFFERS) 
SELECT * FROM rs_tif_file 
WHERE boundary && ST_GeomFromGeoJSON('{"type":"Polygon",...}')
AND ST_Intersects(boundary, ST_GeomFromGeoJSON('{"type":"Polygon",...}'));
```

### 3.3 空间查询优化技巧
1. **使用边界框预过滤**：先用 `&&` 操作符过滤，再用精确的空间函数
2. **避免重复计算**：将 `ST_GeomFromGeoJSON()` 结果存储在变量中
3. **合理使用索引**：确保空间索引存在且被正确使用
4. **分页查询**：对于大量结果使用 LIMIT 和 OFFSET

## 4. 性能监控

### 4.1 查询性能监控
```sql
-- 查看慢查询
SELECT query, mean_time, calls, total_time
FROM pg_stat_statements 
WHERE query LIKE '%ST_Intersects%'
ORDER BY mean_time DESC;

-- 查看索引使用统计
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read
FROM pg_stat_user_indexes 
WHERE tablename = 'rs_tif_file';
```

### 4.2 空间数据统计
```sql
-- 查看空间数据分布
SELECT 
    COUNT(*) as total_records,
    ST_Area(ST_Union(boundary)) as total_area,
    ST_AsText(ST_Centroid(ST_Union(boundary))) as centroid
FROM rs_tif_file;

-- 查看边界框范围
SELECT 
    ST_XMin(ST_Extent(boundary)) as min_x,
    ST_YMin(ST_Extent(boundary)) as min_y,
    ST_XMax(ST_Extent(boundary)) as max_x,
    ST_YMax(ST_Extent(boundary)) as max_y
FROM rs_tif_file;
```

## 5. 应用层优化

### 5.1 连接池配置
```yaml
# application.yml
spring:
  datasource:
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
```

### 5.2 查询缓存
```java
// 使用Redis缓存空间查询结果
@Cacheable(value = "spatialQuery", key = "#geoJson")
public List<RsTifFile> selectBySpatialIntersection(String geoJson) {
    return baseMapper.selectBySpatialIntersection(geoJson);
}
```

## 6. 最佳实践

### 6.1 数据准备
1. **数据质量**：确保GeoJSON格式正确，坐标系统一致
2. **数据简化**：对于复杂几何体，考虑使用ST_Simplify进行简化
3. **坐标系**：统一使用WGS84坐标系（EPSG:4326）

### 6.2 查询优化
1. **批量操作**：对于大量数据，使用批量插入和更新
2. **事务管理**：合理使用事务，避免长时间锁定
3. **连接管理**：使用连接池，避免频繁创建连接

### 6.3 监控和维护
1. **定期分析**：定期运行ANALYZE更新统计信息
2. **索引维护**：监控索引使用情况，及时重建
3. **性能监控**：使用pg_stat_statements监控查询性能

## 7. 常见问题解决

### 7.1 空间索引未使用
```sql
-- 检查查询计划
EXPLAIN (ANALYZE, BUFFERS) SELECT * FROM rs_tif_file WHERE boundary && ST_GeomFromGeoJSON('...');

-- 强制使用索引
SET enable_seqscan = off;
```

### 7.2 查询性能慢
1. 检查是否有空间索引
2. 使用边界框预过滤
3. 优化GeoJSON格式
4. 考虑数据分区

### 7.3 内存不足
1. 调整work_mem参数
2. 使用LIMIT限制结果集
3. 优化查询条件
4. 增加数据库内存配置
