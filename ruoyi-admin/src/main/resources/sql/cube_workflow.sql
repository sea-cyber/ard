-- 立方体工作流表
CREATE TABLE IF NOT EXISTS public.cube_workflow
(
    workflow_id character varying(50) NOT NULL,
    workflow_name character varying(100) COLLATE pg_catalog."default" NOT NULL,
    user_id bigint NOT NULL,
    upload_time timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description text COLLATE pg_catalog."default",
    category character varying(50) COLLATE pg_catalog."default" NOT NULL,
    architecture character varying(10) COLLATE pg_catalog."default" NOT NULL,
    version character varying(20) COLLATE pg_catalog."default" DEFAULT '1.0'::character varying,
    is_public boolean DEFAULT false,
    status character varying(20) COLLATE pg_catalog."default" DEFAULT 'active'::character varying,
    executor_path text COLLATE pg_catalog."default",
    algorithm_code character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT cube_workflow_pkey PRIMARY KEY (workflow_id),
    CONSTRAINT cube_workflow_algorithm_code_key UNIQUE (algorithm_code),
    CONSTRAINT fk_workflow_user FOREIGN KEY (user_id)
        REFERENCES public.sys_user (user_id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
)
TABLESPACE pg_default;

ALTER TABLE IF NOT EXISTS public.cube_workflow
    OWNER to postgres;

-- 不再需要序列，因为workflow_id是字符串类型

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_workflow_status_public
    ON public.cube_workflow USING btree
    (status COLLATE pg_catalog."default" ASC NULLS LAST, is_public ASC NULLS LAST)
    TABLESPACE pg_default;

CREATE INDEX IF NOT EXISTS idx_workflow_user_category
    ON public.cube_workflow USING btree
    (user_id ASC NULLS LAST, category COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- 插入示例数据
INSERT INTO public.cube_workflow (workflow_id, workflow_name, user_id, description, category, architecture, version, is_public, status, algorithm_code) VALUES
('flow_0001', '植被指数计算', 1, '用于处理卫星遥感原始数据', 'image_processing', 'python', '1.0', true, 'active', 'workflow_20231026_101522'),
('flow_0002', '土地覆盖分类模型训练', 1, '训练土地覆盖分类的机器学习模型', 'machine_learning', 'python', '1.0', true, 'active', 'workflow_20231027_143045'),
('flow_0003', '遥感影像变化检测分析流程', 1, '完整的遥感影像变化检测工作流，包含数据读取、预处理、算法执行和结果输出。', 'change_detection', 'python', '1.0', true, 'active', 'workflow_20241119_160000'),
('flow_0004', '目标识别与提取流程', 1, '专门用于从高分辨率卫星影像中识别和提取舰船等水上目标。', 'object_detection', 'python', '1.0', true, 'active', 'workflow_20250820_110000');
