/*
 立方体网格信息表
 用于存储立方体的基本信息
*/

-- ----------------------------
-- Table structure for cube_grid_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."cube_grid_info";
CREATE TABLE "public"."cube_grid_info" (
  "cube_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "grid_id" varchar(50) COLLATE "pg_catalog"."default",
  "secretlevel" varchar(20) COLLATE "pg_catalog"."default",
  "description" text COLLATE "pg_catalog"."default",
  "province" varchar(50) COLLATE "pg_catalog"."default",
  "city" varchar(50) COLLATE "pg_catalog"."default",
  "county" varchar(50) COLLATE "pg_catalog"."default",
  "city_district" varchar(50) COLLATE "pg_catalog"."default",
  "epsg" int4,
  "bbox" geometry(GEOMETRY),
  "grid_type" varchar(20) COLLATE "pg_catalog"."default",
  "organization" varchar(100) COLLATE "pg_catalog"."default",
  "department" varchar(100) COLLATE "pg_catalog"."default",
  "operator" varchar(50) COLLATE "pg_catalog"."default",
  "email" varchar(100) COLLATE "pg_catalog"."default",
  "role" varchar(50) COLLATE "pg_catalog"."default",
  "total_files" int4 DEFAULT 0,
  "original_files" int4 DEFAULT 0,
  "derived_files" int4 DEFAULT 0,
  "seasons_covered" varchar(100) COLLATE "pg_catalog"."default",
  "time_span" varchar(100) COLLATE "pg_catalog"."default",
  "resolution_level" varchar(20) COLLATE "pg_catalog"."default",
  "created" timestamptz(6) DEFAULT CURRENT_TIMESTAMP,
  "updated" timestamptz(6) DEFAULT CURRENT_TIMESTAMP,
  "created_by" varchar(50) COLLATE "pg_catalog"."default"
);

-- ----------------------------
-- Primary Key structure for table cube_grid_info
-- ----------------------------
ALTER TABLE "public"."cube_grid_info" ADD CONSTRAINT "cube_grid_info_pkey" PRIMARY KEY ("cube_id");

-- ----------------------------
-- Indexes structure for table cube_grid_info
-- ----------------------------
CREATE INDEX "idx_cube_grid_info_grid_id" ON "public"."cube_grid_info" USING btree ("grid_id");
CREATE INDEX "idx_cube_grid_info_province" ON "public"."cube_grid_info" USING btree ("province");
CREATE INDEX "idx_cube_grid_info_city" ON "public"."cube_grid_info" USING btree ("city");
CREATE INDEX "idx_cube_grid_info_created" ON "public"."cube_grid_info" USING btree ("created");

-- ----------------------------
-- 插入测试数据
-- ----------------------------
INSERT INTO "public"."cube_grid_info" VALUES 
('GRID_CUBE_T0_N51E016010', 'T0_N51E016010', 'PUBLIC', '北京市朝阳区遥感立方体数据', '北京市', '北京市', '朝阳区', '朝阳区', 4326, 
 ST_GeomFromText('POLYGON((116.0 39.0, 117.0 39.0, 117.0 40.0, 116.0 40.0, 116.0 39.0))', 4326), 
 'GRID', '中国科学院', '遥感与数字地球研究所', '张三', 'zhangsan@cas.cn', 'ADMIN', 5, 3, 2, 'Q1,Q2,Q3,Q4', '2024-01-01 to 2024-12-31', 'HIGH', 
 '2024-01-15 10:00:00+08', '2024-01-15 10:00:00+08', 'admin'),

('GRID_CUBE_T0_J50E012016', 'T0_J50E012016', 'PUBLIC', '上海市浦东新区遥感立方体数据', '上海市', '上海市', '浦东新区', '浦东新区', 4326, 
 ST_GeomFromText('POLYGON((121.0 31.0, 122.0 31.0, 122.0 32.0, 121.0 32.0, 121.0 31.0))', 4326), 
 'GRID', '上海交通大学', '电子信息与电气工程学院', '李四', 'lisi@sjtu.edu.cn', 'USER', 3, 2, 1, 'Q2,Q3', '2024-04-01 to 2024-09-30', 'MEDIUM', 
 '2024-01-16 10:00:00+08', '2024-01-16 10:00:00+08', 'admin'),

('GRID_CUBE_T0_K51E013017', 'T0_K51E013017', 'PUBLIC', '广州市天河区遥感立方体数据', '广东省', '广州市', '天河区', '天河区', 4326, 
 ST_GeomFromText('POLYGON((113.0 23.0, 114.0 23.0, 114.0 24.0, 113.0 24.0, 113.0 23.0))', 4326), 
 'GRID', '中山大学', '地理科学与规划学院', '王五', 'wangwu@sysu.edu.cn', 'USER', 4, 3, 1, 'Q1,Q2,Q3,Q4', '2024-01-01 to 2024-12-31', 'HIGH', 
 '2024-01-17 10:00:00+08', '2024-01-17 10:00:00+08', 'admin');

