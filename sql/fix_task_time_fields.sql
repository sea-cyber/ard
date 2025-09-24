-- 修复task表的时间字段类型
-- 将TIME类型改为TIMESTAMP类型以支持完整的日期时间

-- 备份现有数据（如果需要）
-- CREATE TABLE task_backup AS SELECT * FROM task;

-- 修改时间字段类型
ALTER TABLE task 
ALTER COLUMN create_time TYPE TIMESTAMP USING create_time::TIMESTAMP;

ALTER TABLE task 
ALTER COLUMN start_time TYPE TIMESTAMP USING start_time::TIMESTAMP;

ALTER TABLE task 
ALTER COLUMN end_time TYPE TIMESTAMP USING end_time::TIMESTAMP;

ALTER TABLE task 
ALTER COLUMN update_time TYPE TIMESTAMP USING update_time::TIMESTAMP;

-- 更新现有数据，为只有时分秒的记录添加当前日期
UPDATE task 
SET create_time = CURRENT_DATE + create_time::TIME
WHERE create_time::TEXT ~ '^[0-9]{2}:[0-9]{2}:[0-9]{2}';

UPDATE task 
SET start_time = CURRENT_DATE + start_time::TIME
WHERE start_time::TEXT ~ '^[0-9]{2}:[0-9]{2}:[0-9]{2}';

UPDATE task 
SET end_time = CURRENT_DATE + end_time::TIME
WHERE end_time::TEXT ~ '^[0-9]{2}:[0-9]{2}:[0-9]{2}';

UPDATE task 
SET update_time = CURRENT_DATE + update_time::TIME
WHERE update_time::TEXT ~ '^[0-9]{2}:[0-9]{2}:[0-9]{2}';

-- 验证修改结果
SELECT 
    task_id,
    create_time,
    start_time,
    end_time,
    update_time
FROM task 
LIMIT 5;

