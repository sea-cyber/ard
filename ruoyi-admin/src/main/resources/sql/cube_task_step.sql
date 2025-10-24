-- Table: public.cube_task_step
-- 功能：记录每个任务的子步骤详情，与 cube_task_info 表通过 task_id 关联

DROP TABLE IF EXISTS public.cube_task_step;

CREATE TABLE IF NOT EXISTS public.cube_task_step
(
    step_id bigserial NOT NULL, -- 步骤记录唯一ID（自增主键）
    task_id character varying(50) COLLATE pg_catalog."default" NOT NULL, -- 关联任务主表的任务ID
    step_name character varying(50) COLLATE pg_catalog."default" NOT NULL, -- 步骤名称（如：数据准备、任务拆分、算法初始化、结果输出）
    step_order integer NOT NULL, -- 步骤顺序（1-4，确保步骤执行逻辑顺序）
    step_status character varying(20) COLLATE pg_catalog."default" NOT NULL DEFAULT 'pending'::character varying, -- 步骤状态：pending/processing/completed/failed
    start_time timestamp with time zone, -- 步骤开始时间
    end_time timestamp with time zone, -- 步骤结束时间（完成/失败时更新）
    step_desc text COLLATE pg_catalog."default", -- 步骤说明（如：数据校验通过、执行中心分配结果）
    error_details text COLLATE pg_catalog."default", -- 步骤失败详情（成功时为NULL）
    cube_count integer, -- 该步骤涉及的立方体数量（如任务拆分步骤分配了5个立方体）
    processing_center character varying(100) COLLATE pg_catalog."default", -- 该步骤的执行中心（如：北京中心）
    created timestamp with time zone DEFAULT CURRENT_TIMESTAMP, -- 步骤记录创建时间
    updated timestamp with time zone DEFAULT CURRENT_TIMESTAMP, -- 步骤记录更新时间
    CONSTRAINT cube_task_step_pkey PRIMARY KEY (step_id),
    -- 外键关联任务主表：任务删除时，步骤记录同步删除
    CONSTRAINT fk_step_task FOREIGN KEY (task_id)
        REFERENCES public.cube_task_info (task_id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    -- 唯一约束：同一任务的同一步骤名称/顺序不能重复
    CONSTRAINT uk_task_step_unique UNIQUE (task_id, step_name),
    CONSTRAINT uk_task_step_order UNIQUE (task_id, step_order),
    -- 状态值校验：只能是指定的状态
    CONSTRAINT ck_step_status CHECK (step_status IN ('pending', 'processing', 'completed', 'failed')),
    -- 步骤顺序校验：1-4（对应4个固定步骤）
    CONSTRAINT ck_step_order CHECK (step_order BETWEEN 1 AND 4)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.cube_task_step
    OWNER to postgres;

-- 索引：核心查询场景优化
-- 1. 按 task_id 查询所有步骤（最常用，如前端展示任务步骤进度）
CREATE INDEX IF NOT EXISTS idx_step_task_id
    ON public.cube_task_step USING btree
    (task_id COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- 2. 按步骤状态+时间查询（如统计所有失败的步骤）
CREATE INDEX IF NOT EXISTS idx_step_status_time
    ON public.cube_task_step USING btree
    (step_status COLLATE pg_catalog."default" ASC NULLS LAST, end_time ASC NULLS LAST)
    TABLESPACE pg_default;

-- 3. 按执行中心+步骤名称查询（如查询北京中心的"算法初始化"步骤）
CREATE INDEX IF NOT EXISTS idx_step_center_name
    ON public.cube_task_step USING btree
    (processing_center COLLATE pg_catalog."default" ASC NULLS LAST, step_name COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

