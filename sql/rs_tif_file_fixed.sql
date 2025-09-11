/*
 修改后的rs_tif_file表结构 - 使用TIMESTAMP类型
*/

-- ----------------------------
-- Table structure for rs_tif_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."rs_tif_file";
CREATE TABLE "public"."rs_tif_file" (
  "id" int4 NOT NULL,
  "base_id" int8,
  "filename" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "file_path" varchar(255) COLLATE "pg_catalog"."default",
  "satellite_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "sensor_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "product_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "acquisition_time" timestamp(6) NOT NULL,
  "input_time" timestamp(6) NOT NULL,
  "cloud_percent" numeric NOT NULL,
  "orbit_id" int4 NOT NULL,
  "scene_path" int4 NOT NULL,
  "scene_row" int4 NOT NULL,
  "quick_view_uri" varchar COLLATE "pg_catalog"."default" NOT NULL,
  "has_pair" int2 NOT NULL,
  "has_entity" int2 NOT NULL,
  "laser_count" int4,
  "boundary" geometry(GEOMETRY) NOT NULL,
  "in_cart" text COLLATE "pg_catalog"."default",
  "tar_input_time" timestamp(6)
);

-- ----------------------------
-- Primary Key structure for table rs_tif_file
-- ----------------------------
ALTER TABLE "public"."rs_tif_file" ADD CONSTRAINT "rs_tif_file_pkey" PRIMARY KEY ("id");

-- 插入一些测试数据
INSERT INTO "public"."rs_tif_file" VALUES 
(1, 1001, 'test_file_001.tif', '/data/tif/test_file_001.tif', 'LANDSAT8', 'OLI', 'L1TP', '2024-01-15 10:30:00', '2024-01-15 11:00:00', 15.5, 12345, 120, 35, '/preview/test_file_001.jpg', 1, 1, 1000, ST_GeomFromText('POLYGON((116.0 39.0, 117.0 39.0, 117.0 40.0, 116.0 40.0, 116.0 39.0))'), NULL, NULL),
(2, 1002, 'test_file_002.tif', '/data/tif/test_file_002.tif', 'SENTINEL2', 'MSI', 'L1C', '2024-01-16 14:20:00', '2024-01-16 14:50:00', 8.2, 12346, 121, 36, '/preview/test_file_002.jpg', 0, 1, 2000, ST_GeomFromText('POLYGON((116.5 39.5, 117.5 39.5, 117.5 40.5, 116.5 40.5, 116.5 39.5))'), NULL, NULL);

