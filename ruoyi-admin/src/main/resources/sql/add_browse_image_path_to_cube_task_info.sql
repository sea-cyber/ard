-- 给 cube_task_info 表添加 browse_image_path 字段
ALTER TABLE public.cube_task_info ADD COLUMN IF NOT EXISTS browse_image_path text;

-- 添加注释
COMMENT ON COLUMN public.cube_task_info.browse_image_path IS '浏览图片路径';

