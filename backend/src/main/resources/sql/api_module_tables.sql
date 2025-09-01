-- API接口模块数据库表结构

-- 1. API配置表
CREATE TABLE `t_api_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api_name` varchar(100) NOT NULL COMMENT 'API接口名称',
  `api_code` varchar(50) NOT NULL COMMENT 'API接口编码，用于对外调用',
  `api_type` tinyint(4) NOT NULL COMMENT 'API接口类型：1-远程HTTP接口，2-数据源SQL查询',
  `http_method` varchar(10) DEFAULT NULL COMMENT 'HTTP请求方法：GET、POST等（当api_type=1时使用）',
  `http_url` varchar(500) DEFAULT NULL COMMENT 'HTTP请求URL（当api_type=1时使用）',
  `http_headers` text DEFAULT NULL COMMENT 'HTTP请求头（JSON格式，当api_type=1时使用，已废弃）',
  `http_request_config` longtext DEFAULT NULL COMMENT 'HTTP请求完整配置（JSON格式，当api_type=1时使用）',
  `repository_id` int(11) DEFAULT NULL COMMENT '数据源ID（当api_type=2时使用）',
  `sql_query` text DEFAULT NULL COMMENT 'SQL查询语句（当api_type=2时使用）',
  `param_template` text DEFAULT NULL COMMENT '入参模板（JSON格式），支持参数化配置',
  `post_process_script` text DEFAULT NULL COMMENT '返回值后处理脚本（支持JavaScript或表达式）',
  `timeout` int(11) DEFAULT 30000 COMMENT '超时时间（毫秒）',
  `status` tinyint(4) DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_api_code` (`api_code`),
  KEY `idx_api_type` (`api_type`),
  KEY `idx_status` (`status`),
  KEY `idx_repository_id` (`repository_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API接口配置表';

-- 2. API执行日志表
CREATE TABLE `t_api_execute_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api_config_id` int(11) NOT NULL COMMENT 'API配置ID',
  `api_code` varchar(50) NOT NULL COMMENT 'API编码',
  `request_params` text DEFAULT NULL COMMENT '请求参数（JSON格式）',
  `response_result` longtext DEFAULT NULL COMMENT '响应结果（JSON格式）',
  `execute_status` tinyint(4) NOT NULL COMMENT '执行状态：1-成功，0-失败',
  `error_message` text DEFAULT NULL COMMENT '错误信息',
  `execution_time` bigint(20) DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `execute_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
  `request_ip` varchar(50) DEFAULT NULL COMMENT '请求IP',
  PRIMARY KEY (`id`),
  KEY `idx_api_config_id` (`api_config_id`),
  KEY `idx_api_code` (`api_code`),
  KEY `idx_execute_status` (`execute_status`),
  KEY `idx_execute_time` (`execute_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API执行日志表';

-- 创建索引以提高查询性能
CREATE INDEX `idx_api_config_create_time` ON `t_api_config` (`create_time`);
CREATE INDEX `idx_api_config_update_time` ON `t_api_config` (`update_time`);
CREATE INDEX `idx_api_execute_log_execute_time_status` ON `t_api_execute_log` (`execute_time`, `execute_status`);

-- 插入示例数据

-- 示例1: HTTP接口配置
INSERT INTO `t_api_config` (`api_name`, `api_code`, `api_type`, `http_method`, `http_url`, `http_headers`, `param_template`, `description`, `status`) VALUES
('获取天气信息', 'weather_info', 1, 'GET', 'https://api.openweathermap.org/data/2.5/weather', '{"Content-Type": "application/json"}', '{"q": "${city}", "appid": "${api_key}"}', '获取指定城市的天气信息', 1);

-- 示例2: SQL查询配置
INSERT INTO `t_api_config` (`api_name`, `api_code`, `api_type`, `repository_id`, `sql_query`, `param_template`, `post_process_script`, `description`, `status`) VALUES
('用户列表查询', 'user_list', 2, 1, 'SELECT id, username, email, create_time FROM users WHERE status = 1 #{if(name != null)} AND username LIKE CONCAT("%", "${name}", "%") #{end} ORDER BY create_time DESC', '{"name": "${userName}"}', 'data.size() > 0 ? data : []', '查询用户列表信息', 1);

-- 示例3: HTTP POST接口配置
INSERT INTO `t_api_config` (`api_name`, `api_code`, `api_type`, `http_method`, `http_url`, `http_headers`, `param_template`, `description`, `status`) VALUES
('发送短信', 'send_sms', 1, 'POST', 'https://api.sms.com/send', '{"Content-Type": "application/json", "Authorization": "Bearer ${token}"}', '{"phone": "${mobile}", "message": "${content}"}', '发送短信接口', 1);

-- 示例4: 数据统计查询
INSERT INTO `t_api_config` (`api_name`, `api_code`, `api_type`, `repository_id`, `sql_query`, `param_template`, `post_process_script`, `description`, `status`) VALUES
('数据统计', 'data_stats', 2, 1, 'SELECT COUNT(*) as total, DATE_FORMAT(create_time, "%Y-%m-%d") as date FROM orders WHERE create_time >= "${startDate}" AND create_time <= "${endDate}" GROUP BY DATE_FORMAT(create_time, "%Y-%m-%d") ORDER BY date', '{"startDate": "${start}", "endDate": "${end}"}', 'data', '订单数据统计', 1);