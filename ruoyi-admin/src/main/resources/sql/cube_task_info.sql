-- 立方体任务信息表
DROP TABLE IF EXISTS public.cube_task_info;

CREATE TABLE IF NOT EXISTS public.cube_task_info (
    task_id character varying(100) NOT NULL,
    task_name character varying(200) NOT NULL,
    task_description text,
    task_type character varying(50) NOT NULL,
    user_id bigint NOT NULL,
    created timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    time_start timestamp with time zone,
    time_end timestamp with time zone,
    processing_center character varying(100),
    output_resolution character varying(50),
    output_format character varying(20),
    workflow_id character varying(50),
    workflow_name character varying(100),
    workflow_description text,
    status character varying(20) NOT NULL DEFAULT 'pending',
    progress integer DEFAULT 0,
    error_message text,
    result_count integer DEFAULT 0,
    result_directory character varying(500),
    completion_time timestamp with time zone,
    priority integer DEFAULT 5,
    estimated_duration integer,
    actual_duration integer,
    resource_usage text,
    browse_image_path text,
    CONSTRAINT cube_task_info_pkey PRIMARY KEY (task_id),
    CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES public.sys_user (user_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT
    -- 注意：workflow_id 字段不设置外键约束，允许存储任意值（包括压缩算法名称等）
    -- 这样可以避免数据导入任务时将压缩算法名称作为 workflow_id 时出现外键约束错误
) TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.cube_task_info OWNER to postgres;

-- 索引
CREATE INDEX IF NOT EXISTS idx_task_user_id ON public.cube_task_info USING btree (user_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS idx_task_status ON public.cube_task_info USING btree (status ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS idx_task_user_status ON public.cube_task_info USING btree (user_id ASC NULLS LAST, status ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS idx_task_created ON public.cube_task_info USING btree (created DESC NULLS LAST);
CREATE INDEX IF NOT EXISTS idx_task_workflow ON public.cube_task_info USING btree (workflow_id ASC NULLS LAST);

-- 注释
COMMENT ON TABLE public.cube_task_info IS '立方体任务信息表';
COMMENT ON COLUMN public.cube_task_info.task_id IS '任务ID';
COMMENT ON COLUMN public.cube_task_info.task_name IS '任务名称';
COMMENT ON COLUMN public.cube_task_info.task_description IS '任务描述';
COMMENT ON COLUMN public.cube_task_info.task_type IS '任务类型';
COMMENT ON COLUMN public.cube_task_info.user_id IS '用户ID';
COMMENT ON COLUMN public.cube_task_info.created IS '创建时间';
COMMENT ON COLUMN public.cube_task_info.updated IS '更新时间';
COMMENT ON COLUMN public.cube_task_info.time_start IS '开始时间';
COMMENT ON COLUMN public.cube_task_info.time_end IS '结束时间';
COMMENT ON COLUMN public.cube_task_info.processing_center IS '处理中心';
COMMENT ON COLUMN public.cube_task_info.output_resolution IS '输出分辨率';
COMMENT ON COLUMN public.cube_task_info.output_format IS '输出格式';
COMMENT ON COLUMN public.cube_task_info.workflow_id IS '工作流ID';
COMMENT ON COLUMN public.cube_task_info.workflow_name IS '工作流名称';
COMMENT ON COLUMN public.cube_task_info.workflow_description IS '工作流描述';
COMMENT ON COLUMN public.cube_task_info.status IS '任务状态';
COMMENT ON COLUMN public.cube_task_info.progress IS '任务进度';
COMMENT ON COLUMN public.cube_task_info.error_message IS '错误信息';
COMMENT ON COLUMN public.cube_task_info.result_count IS '结果数量';
COMMENT ON COLUMN public.cube_task_info.result_directory IS '结果目录';
COMMENT ON COLUMN public.cube_task_info.completion_time IS '完成时间';
COMMENT ON COLUMN public.cube_task_info.priority IS '优先级';
COMMENT ON COLUMN public.cube_task_info.estimated_duration IS '预计持续时间(分钟)';
COMMENT ON COLUMN public.cube_task_info.actual_duration IS '实际持续时间(分钟)';
COMMENT ON COLUMN public.cube_task_info.resource_usage IS '资源使用情况';
COMMENT ON COLUMN public.cube_task_info.browse_image_path IS '浏览图片路径';

