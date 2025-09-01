-- API接口模块数据库升级脚本
-- 用于已有环境添加HTTP可视化配置支持

-- 1. 为t_api_config表添加新的HTTP配置字段
ALTER TABLE `t_api_config` 
ADD COLUMN `http_request_config` longtext DEFAULT NULL COMMENT 'HTTP请求完整配置（JSON格式，当api_type=1时使用）' 
AFTER `http_headers`;

-- 2. 修改http_headers字段注释，标记为已废弃
ALTER TABLE `t_api_config` 
MODIFY COLUMN `http_headers` text DEFAULT NULL COMMENT 'HTTP请求头（JSON格式，当api_type=1时使用，已废弃）';

-- 3. 更新现有HTTP接口配置，将旧格式转换为新格式
-- 注意：此步骤需要根据实际数据情况手动执行

-- 示例：将旧的HTTP配置转换为新格式
-- UPDATE `t_api_config` 
-- SET `http_request_config` = JSON_OBJECT(
--     'method', IFNULL(`http_method`, 'GET'),
--     'url', IFNULL(`http_url`, ''),
--     'headers', CASE 
--         WHEN `http_headers` IS NOT NULL AND `http_headers` != '' 
--         THEN JSON_EXTRACT(CONCAT('[', (
--             SELECT GROUP_CONCAT(
--                 JSON_OBJECT('key', k.key_name, 'value', k.key_value, 'enabled', true, 'description', '')
--             ) FROM (
--                 SELECT 
--                     JSON_UNQUOTE(JSON_EXTRACT(keys.value, '$.key')) as key_name,
--                     JSON_UNQUOTE(JSON_EXTRACT(keys.value, '$.value')) as key_value
--                 FROM JSON_TABLE(
--                     CONCAT('[', (
--                         SELECT GROUP_CONCAT(
--                             CONCAT('{"key":"', k, '","value":"', v, '"}')
--                         ) FROM (
--                             SELECT 
--                                 SUBSTRING_INDEX(SUBSTRING_INDEX(pair, ':', 1), '"', -1) as k,
--                                 SUBSTRING_INDEX(SUBSTRING_INDEX(pair, ':', -1), '"', 2) as v
--                             FROM (
--                                 SELECT TRIM(BOTH '{}' FROM `http_headers`) as headers_str
--                             ) h1
--                             CROSS JOIN (
--                                 SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(h1.headers_str, ',', n.n), ',', -1) as pair
--                                 FROM (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) n
--                                 WHERE n.n <= (LENGTH(h1.headers_str) - LENGTH(REPLACE(h1.headers_str, ',', '')) + 1)
--                             ) pairs
--                         ) kv
--                     ), ']')
--                 , '$[*]' COLUMNS (value JSON PATH '$')) keys
--             ) k
--         ), ']'), '$')
--         ELSE JSON_ARRAY()
--     END,
--     'queryParams', JSON_ARRAY(),
--     'body', JSON_OBJECT('type', 'none'),
--     'timeout', IFNULL(`timeout`, 30000),
--     'followRedirects', true,
--     'verifySsl', true
-- )
-- WHERE `api_type` = 1 AND (`http_request_config` IS NULL OR `http_request_config` = '');

-- 4. 为新字段添加索引（可选）
-- CREATE INDEX `idx_http_request_config` ON `t_api_config` (`http_request_config`(255));

-- 完成升级后的验证查询
-- SELECT 
--     id, api_name, api_code, api_type, 
--     http_method, http_url, 
--     CASE 
--         WHEN LENGTH(http_headers) > 100 THEN CONCAT(LEFT(http_headers, 100), '...')
--         ELSE http_headers 
--     END as http_headers_preview,
--     CASE 
--         WHEN LENGTH(http_request_config) > 200 THEN CONCAT(LEFT(http_request_config, 200), '...')
--         ELSE http_request_config 
--     END as http_request_config_preview
-- FROM `t_api_config` 
-- WHERE `api_type` = 1 
-- ORDER BY `update_time` DESC;