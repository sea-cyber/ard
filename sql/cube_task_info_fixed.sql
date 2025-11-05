-- Table: public.cube_task_info
-- 立方体任务信息表（已删除 workflow_id 外键约束）

-- DROP TABLE IF EXISTS public.cube_task_info;

CREATE TABLE IF NOT EXISTS public.cube_task_info
(
    task_id character varying(50) COLLATE pg_catalog."default" NOT NULL,
    task_name character varying(200) COLLATE pg_catalog."default" NOT NULL,
    task_description text COLLATE pg_catalog."default",
    task_type character varying(50) COLLATE pg_catalog."default" NOT NULL,
    user_id bigint NOT NULL,
    created_by character varying(50) COLLATE pg_catalog."default",
    created timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    time_start timestamp with time zone,
    time_end timestamp with time zone,
    processing_center character varying(100) COLLATE pg_catalog."default",
    output_resolution character varying(50) COLLATE pg_catalog."default",
    output_format character varying(20) COLLATE pg_catalog."default" DEFAULT 'TIF'::character varying,
    workflow_id character varying(50) COLLATE pg_catalog."default",
    workflow_name character varying(100) COLLATE pg_catalog."default",
    workflow_description text COLLATE pg_catalog."default",
    status character varying(20) COLLATE pg_catalog."default" DEFAULT 'pending'::character varying,
    progress integer DEFAULT 0,
    error_message text COLLATE pg_catalog."default",
    result_count integer DEFAULT 0,
    result_directory character varying(500) COLLATE pg_catalog."default",
    completion_time timestamp with time zone,
    priority integer DEFAULT 5,
    estimated_duration integer,
    actual_duration integer,
    resource_usage jsonb,
    browse_image_path text COLLATE pg_catalog."default",
    CONSTRAINT cube_task_info_pkey PRIMARY KEY (task_id),
    CONSTRAINT fk_task_user FOREIGN KEY (user_id)
        REFERENCES public.sys_user (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    -- 注意：已删除 workflow_id 外键约束，允许存储任意值（包括压缩算法名称等）
    -- CONSTRAINT fk_task_workflow FOREIGN KEY (workflow_id)
    --     REFERENCES public.cube_workflow (workflow_id) MATCH SIMPLE
    --     ON UPDATE NO ACTION
    --     ON DELETE NO ACTION,
    CONSTRAINT cube_task_info_priority_check CHECK (priority >= 1 AND priority <= 10),
    CONSTRAINT cube_task_info_progress_check CHECK (progress >= 0 AND progress <= 100)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.cube_task_info
    OWNER to postgres;

-- Index: idx_task_created
-- DROP INDEX IF EXISTS public.idx_task_created;
CREATE INDEX IF NOT EXISTS idx_task_created
    ON public.cube_task_info USING btree
    (created ASC NULLS LAST)
    TABLESPACE pg_default;

-- Index: idx_task_status
-- DROP INDEX IF EXISTS public.idx_task_status;
CREATE INDEX IF NOT EXISTS idx_task_status
    ON public.cube_task_info USING btree
    (status COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- Index: idx_task_type
-- DROP INDEX IF EXISTS public.idx_task_type;
CREATE INDEX IF NOT EXISTS idx_task_type
    ON public.cube_task_info USING btree
    (task_type COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- Index: idx_task_user_id
-- DROP INDEX IF EXISTS public.idx_task_user_id;
CREATE INDEX IF NOT EXISTS idx_task_user_id
    ON public.cube_task_info USING btree
    (user_id ASC NULLS LAST)
    TABLESPACE pg_default;

-- Index: idx_task_workflow
-- DROP INDEX IF EXISTS public.idx_task_workflow;
CREATE INDEX IF NOT EXISTS idx_task_workflow
    ON public.cube_task_info USING btree
    (workflow_id COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;



