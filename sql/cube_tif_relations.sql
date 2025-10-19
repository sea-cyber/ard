/*
 立方体与TIF文件关联表
 用于建立立方体与切片数据之间的多对多关系
*/

-- ----------------------------
-- Table structure for cube_tif_relations
-- ----------------------------
DROP TABLE IF EXISTS "public"."cube_tif_relations";
CREATE TABLE "public"."cube_tif_relations" (
  "id" int4 NOT NULL,
  "cube_id" int4 NOT NULL,
  "tif_id" int4 NOT NULL,
  "status" varchar(20) COLLATE "pg_catalog"."default" DEFAULT 'active',
  "sort_order" int4 DEFAULT 0,
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------
-- Primary Key structure for table cube_tif_relations
-- ----------------------------
ALTER TABLE "public"."cube_tif_relations" ADD CONSTRAINT "cube_tif_relations_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table cube_tif_relations
-- ----------------------------
CREATE INDEX "idx_cube_tif_relations_cube_id" ON "public"."cube_tif_relations" USING btree ("cube_id");
CREATE INDEX "idx_cube_tif_relations_tif_id" ON "public"."cube_tif_relations" USING btree ("tif_id");
CREATE INDEX "idx_cube_tif_relations_status" ON "public"."cube_tif_relations" USING btree ("status");

-- ----------------------------
-- Foreign Key constraints for table cube_tif_relations
-- ----------------------------
-- 注意：这里假设存在cube表和rs_tif_file表
-- ALTER TABLE "public"."cube_tif_relations" ADD CONSTRAINT "fk_cube_tif_relations_cube_id" FOREIGN KEY ("cube_id") REFERENCES "public"."cube" ("id") ON DELETE CASCADE ON UPDATE CASCADE;
-- ALTER TABLE "public"."cube_tif_relations" ADD CONSTRAINT "fk_cube_tif_relations_tif_id" FOREIGN KEY ("tif_id") REFERENCES "public"."rs_tif_file" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- 插入测试数据
-- ----------------------------
-- 假设存在立方体ID为1的立方体，关联TIF文件ID为1和2
INSERT INTO "public"."cube_tif_relations" VALUES 
(1, 1, 1, 'active', 1, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(2, 1, 2, 'active', 2, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(3, 2, 1, 'active', 1, '2024-01-16 10:00:00', '2024-01-16 10:00:00'),
(4, 2, 2, 'active', 2, '2024-01-16 10:00:00', '2024-01-16 10:00:00');

-- 为立方体ID 1添加更多切片数据（假设有更多TIF文件）
INSERT INTO "public"."cube_tif_relations" VALUES 
(5, 1, 3, 'active', 3, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(6, 1, 4, 'active', 4, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(7, 1, 5, 'active', 5, '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- 为立方体ID 2添加更多切片数据
INSERT INTO "public"."cube_tif_relations" VALUES 
(8, 2, 3, 'active', 3, '2024-01-16 10:00:00', '2024-01-16 10:00:00'),
(9, 2, 4, 'active', 4, '2024-01-16 10:00:00', '2024-01-16 10:00:00');

-- 添加一些非活跃状态的关联（用于测试）
INSERT INTO "public"."cube_tif_relations" VALUES 
(10, 1, 6, 'inactive', 6, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(11, 2, 7, 'inactive', 7, '2024-01-16 10:00:00', '2024-01-16 10:00:00');

