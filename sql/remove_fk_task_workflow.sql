-- 删除 cube_task_info 表的 workflow_id 外键约束
-- 允许 workflow_id 字段可以存储不在 cube_workflow 表中的值
-- 执行此脚本后，workflow_id 字段可以存储任意值（包括压缩算法名称等）
-- 
-- 使用方法：
-- 1. 在 PostgreSQL 数据库中执行此脚本
-- 2. 或者使用 psql 命令行工具执行：psql -U postgres -d your_database -f remove_fk_task_workflow.sql
-- 3. 或者在 pgAdmin 中执行此脚本

-- 检查约束是否存在，如果存在则删除
ALTER TABLE IF EXISTS public.cube_task_info 
DROP CONSTRAINT IF EXISTS fk_task_workflow;

-- 验证约束是否已删除（可选）
-- SELECT constraint_name, constraint_type 
-- FROM information_schema.table_constraints 
-- WHERE table_name = 'cube_task_info' AND constraint_name = 'fk_task_workflow';

