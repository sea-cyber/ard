-- 为协同处理平台左侧导航栏添加菜单权限记录
-- 需要在现有的sys_menu表基础上添加新的菜单项

-- 插入协同处理平台主菜单目录（作为顶级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2000, '协同处理平台', 0, 1, 'cube', '', '', 'Cube', 1, 0, 'M', '0', '0', '', 'dashboard', 'admin', NOW(), '', NULL, '协同处理平台主菜单');

-- 插入平台概览菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2001, '平台概览', 2000, 1, 'dashboard', 'cube/Dashboard', '', 'Dashboard', 1, 0, 'C', '0', '0', 'cube:dashboard:view', 'dashboard', 'admin', NOW(), '', NULL, '协同处理平台概览页面');

-- 插入立方体网格菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2002, '立方体格网', 2000, 2, 'cube-search', 'cube/CubeRetrieval', '', 'CubeSearch', 1, 0, 'C', '0', '0', 'cube:cube:search', 'search', 'admin', NOW(), '', NULL, '立方体格网检索页面');

-- 插入应用数据集菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2003, '应用数据集', 2000, 3, 'scene-search', 'cube/SceneRetrieval', '', 'SceneSearch', 1, 0, 'C', '0', '0', 'cube:scene:search', 'search', 'admin', NOW(), '', NULL, '时序场分析景检索页面');

-- 插入任务管理菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2004, '任务管理', 2000, 4, 'task-management', 'cube/TaskManagement', '', 'TaskManagement', 1, 0, 'C', '0', '0', 'cube:task:view', 'tasks', 'admin', NOW(), '', NULL, '任务管理页面');

-- 插入数据管理菜单（需要权限控制）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2005, '数据管理', 2000, 5, 'data-management', 'cube/DataManagerContent', '', 'DataManagement', 1, 0, 'C', '0', '0', 'cube:data:manage', 'database', 'admin', NOW(), '', NULL, '数据管理页面');

-- 插入资源管理菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2006, '资源管理', 2000, 6, 'resource-management', '', '', 'ResourceManagement', 1, 0, 'C', '0', '0', 'cube:resource:view', 'server', 'admin', NOW(), '', NULL, '资源管理页面');

-- 插入算法管理菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2007, '算法管理', 2000, 7, 'algorithm-management', '', '', 'AlgorithmManagement', 1, 0, 'C', '0', '0', 'cube:algorithm:view', 'cogs', 'admin', NOW(), '', NULL, '算法管理页面');

-- 插入流程管理菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2008, '流程管理', 2000, 8, 'workflow-combination', '', '', 'WorkflowCombination', 1, 0, 'C', '0', '0', 'cube:workflow:view', 'sitemap', 'admin', NOW(), '', NULL, '流程组合页面');

-- 插入统计分析菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2009, '统计分析', 2000, 9, 'statistics', '', '', 'Statistics', 1, 0, 'C', '0', '0', 'cube:statistics:view', 'bar-chart', 'admin', NOW(), '', NULL, '统计分析页面');

-- 为数据管理菜单添加详细的按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2010, '数据管理-查询', 2005, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'cube:data:query', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2011, '数据管理-新增', 2005, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'cube:data:add', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2012, '数据管理-修改', 2005, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'cube:data:edit', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2013, '数据管理-删除', 2005, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'cube:data:remove', '#', 'admin', NOW(), '', NULL, '');

-- 为任务管理添加按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2014, '任务管理-查询', 2004, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'cube:task:query', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2015, '任务管理-新增', 2004, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'cube:task:add', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2016, '任务管理-修改', 2004, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'cube:task:edit', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2017, '任务管理-删除', 2004, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'cube:task:remove', '#', 'admin', NOW(), '', NULL, '');





