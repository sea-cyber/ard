/*
 Navicat PostgreSQL Dump SQL

 Source Server         : postgres
 Source Server Type    : PostgreSQL
 Source Server Version : 170006 (170006)
 Source Host           : localhost:5432
 Source Catalog        : ARD_CUBE
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 170006 (170006)
 File Encoding         : 65001

 Date: 07/09/2025 14:06:12
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
  "acquisition_time" timestamptz(6) NOT NULL,
  "input_time" timestamptz(6) NOT NULL,
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
  "tar_input_time" timestamptz(6)
)
;

-- ----------------------------
-- Primary Key structure for table rs_tif_file
-- ----------------------------
ALTER TABLE "public"."rs_tif_file" ADD CONSTRAINT "rs_tif_file_pkey" PRIMARY KEY ("id");
