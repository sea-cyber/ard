-- 插入测试立方体数据
INSERT INTO public.cube (
    cube_name, 
    create_user, 
    boundary, 
    create_time, 
    data_type, 
    data_describe, 
    compression_algorithm
) VALUES 
(
    'GF-1_PMS_001', 
    'admin', 
    ST_GeomFromText('POLYGON((116.0 39.0, 116.1 39.0, 116.1 39.1, 116.0 39.1, 116.0 39.0))', 4326), 
    '2023-01-15 10:30:00+08', 
    'TIF', 
    '高分一号PMS传感器数据', 
    'LZW'
),
(
    'GF-1_WFV_002', 
    'admin', 
    ST_GeomFromText('POLYGON((116.1 39.1, 116.2 39.1, 116.2 39.2, 116.1 39.2, 116.1 39.1))', 4326), 
    '2023-01-16 11:00:00+08', 
    'TIF', 
    '高分一号WFV传感器数据', 
    'DEFLATE'
),
(
    'GF-2_PMS_003', 
    'admin', 
    ST_GeomFromText('POLYGON((116.2 39.2, 116.3 39.2, 116.3 39.3, 116.2 39.3, 116.2 39.2))', 4326), 
    '2023-01-17 09:15:00+08', 
    'TIF', 
    '高分二号PMS传感器数据', 
    'JPEG2000'
),
(
    'Landsat8_OLI_004', 
    'admin', 
    ST_GeomFromText('POLYGON((116.3 39.3, 116.4 39.3, 116.4 39.4, 116.3 39.4, 116.3 39.3))', 4326), 
    '2023-01-18 14:20:00+08', 
    'TIF', 
    'Landsat8 OLI传感器数据', 
    'LZ4'
),
(
    'Sentinel2_MSI_005', 
    'admin', 
    ST_GeomFromText('POLYGON((116.4 39.4, 116.5 39.4, 116.5 39.5, 116.4 39.5, 116.4 39.4))', 4326), 
    '2023-01-19 08:45:00+08', 
    'TIF', 
    'Sentinel2 MSI传感器数据', 
    'ZSTD'
),
(
    'MODIS_Terra_006', 
    'admin', 
    ST_GeomFromText('POLYGON((116.5 39.5, 116.6 39.5, 116.6 39.6, 116.5 39.6, 116.5 39.5))', 4326), 
    '2023-01-20 12:30:00+08', 
    'HDF5', 
    'MODIS Terra传感器数据', 
    'NONE'
),
(
    'Cube_ARD_007', 
    'admin', 
    ST_GeomFromText('POLYGON((116.6 39.6, 116.7 39.6, 116.7 39.7, 116.6 39.7, 116.6 39.6))', 4326), 
    '2023-01-21 16:15:00+08', 
    'ARD', 
    '分析就绪数据立方体', 
    'LZW'
),
(
    'NetCDF_Climate_008', 
    'admin', 
    ST_GeomFromText('POLYGON((116.7 39.7, 116.8 39.7, 116.8 39.8, 116.7 39.8, 116.7 39.7))', 4326), 
    '2023-01-22 13:45:00+08', 
    'NETCDF', 
    '气候数据NetCDF格式', 
    'DEFLATE'
),
(
    'Zarr_Array_009', 
    'admin', 
    ST_GeomFromText('POLYGON((116.8 39.8, 116.9 39.8, 116.9 39.9, 116.8 39.9, 116.8 39.8))', 4326), 
    '2023-01-23 10:20:00+08', 
    'ZARR', 
    'Zarr数组格式数据', 
    'LZ4'
),
(
    'GF-1_PMS_010', 
    'admin', 
    ST_GeomFromText('POLYGON((116.9 39.9, 117.0 39.9, 117.0 40.0, 116.9 40.0, 116.9 39.9))', 4326), 
    '2023-01-24 15:30:00+08', 
    'TIF', 
    '高分一号PMS传感器数据（第二批）', 
    'JPEG2000'
);
