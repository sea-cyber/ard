-- 任务进度表模拟数据插入SQL

-- 插入任务数据
INSERT INTO public.task (task_id, task_description, status, run_duration, create_user, process_id, task_type, start_time, end_time, create_time, update_time) VALUES
(1, '构建2024年1月北京市遥感立方体数据', 3, '2小时30分钟', 1, 1, '立方体构建', '2024-01-15 09:00:00+08', '2024-01-15 11:30:00', '2024-01-15 08:45:00', '2024-01-15 11:30:00'),
(2, '分析2024年2月上海市时序变化', 2, '1小时15分钟', 1, 2, '时序分析', '2024-01-16 10:00:00+08', NULL, '2024-01-16 09:30:00', '2024-01-16 10:15:00'),
(3, '转换2024年3月广州市数据格式', 1, NULL, 1, 3, '格式转换', NULL, NULL, '2024-01-17 14:00:00', '2024-01-17 14:00:00'),
(4, '构建2024年4月深圳市遥感立方体数据', 4, '0分钟', 1, 4, '立方体构建', '2024-01-18 08:00:00+08', '2024-01-18 08:05:00', '2024-01-18 07:45:00', '2024-01-18 08:05:00'),
(5, '分析2024年5月杭州市时序变化', 3, '3小时45分钟', 1, 5, '时序分析', '2024-01-19 13:00:00+08', '2024-01-19 16:45:00', '2024-01-19 12:30:00', '2024-01-19 16:45:00');

-- 插入任务进度数据
INSERT INTO public.task_process (process_id, step_name, step_status, start_time, end_time, error_log, task_id) VALUES
-- 任务1: 立方体构建 (已完成)
(1, '任务分配', '已完成', '2024-01-15', '2024-01-15', NULL, 1),
(2, '数据准备', '已完成', '2024-01-15', '2024-01-15', NULL, 1),
(3, '构建执行', '已完成', '2024-01-15', '2024-01-15', NULL, 1),
(4, '结果校验', '已完成', '2024-01-15', '2024-01-15', NULL, 1),
(5, '结果保存', '已完成', '2024-01-15', '2024-01-15', NULL, 1),

-- 任务2: 时序分析 (进行中)
(6, '任务分配', '已完成', '2024-01-16', '2024-01-16', NULL, 2),
(7, '数据读取', '已完成', '2024-01-16', '2024-01-16', NULL, 2),
(8, '分析计算', '进行中', '2024-01-16', NULL, NULL, 2),
(9, '可视化生成', '待执行', NULL, NULL, NULL, 2),
(10, '结果保存', '待执行', NULL, NULL, NULL, 2),

-- 任务3: 格式转换 (待执行)
(11, '数据输入', '待执行', NULL, NULL, NULL, 3),
(12, '数据读取', '待执行', NULL, NULL, NULL, 3),
(13, '格式转换', '待执行', NULL, NULL, NULL, 3),
(14, '结果保存', '待执行', NULL, NULL, NULL, 3),

-- 任务4: 立方体构建 (失败)
(15, '任务分配', '已完成', '2024-01-18', '2024-01-18', NULL, 4),
(16, '数据准备', '失败', '2024-01-18', '2024-01-18', '数据源连接超时', 4),
(17, '构建执行', '待执行', NULL, NULL, NULL, 4),
(18, '结果校验', '待执行', NULL, NULL, NULL, 4),
(19, '结果保存', '待执行', NULL, NULL, NULL, 4),

-- 任务5: 时序分析 (已完成)
(20, '任务分配', '已完成', '2024-01-19', '2024-01-19', NULL, 5),
(21, '数据读取', '已完成', '2024-01-19', '2024-01-19', NULL, 5),
(22, '分析计算', '已完成', '2024-01-19', '2024-01-19', NULL, 5),
(23, '可视化生成', '已完成', '2024-01-19', '2024-01-19', NULL, 5),
(24, '结果保存', '已完成', '2024-01-19', '2024-01-19', NULL, 5);

-- 更新序列值（如果需要）
-- 检查并更新task表的序列
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_sequences WHERE sequencename = 'task_task_id_seq') THEN
        PERFORM setval('task_task_id_seq', (SELECT MAX(task_id) FROM task));
    ELSIF EXISTS (SELECT 1 FROM pg_sequences WHERE sequencename = 'task_id_seq') THEN
        PERFORM setval('task_id_seq', (SELECT MAX(task_id) FROM task));
    END IF;
END $$;

-- 检查并更新task_process表的序列
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_sequences WHERE sequencename = 'task_process_process_id_seq') THEN
        PERFORM setval('task_process_process_id_seq', (SELECT MAX(process_id) FROM task_process));
    ELSIF EXISTS (SELECT 1 FROM pg_sequences WHERE sequencename = 'process_id_seq') THEN
        PERFORM setval('process_id_seq', (SELECT MAX(process_id) FROM task_process));
    END IF;
END $$;
