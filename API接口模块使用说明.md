# API接口模块使用说明

## 模块概述

API接口模块为系统提供了统一的接口定义、管理和调用功能，支持两种类型的接口：
1. **远程HTTP接口**：调用外部HTTP服务
2. **数据源SQL查询**：执行数据库SQL查询

## 功能特性

### 1. 接口定义功能
- 支持定义远程HTTP接口（GET、POST、PUT、DELETE）
- 支持定义数据源SQL查询接口
- 统一的参数模板配置
- 返回值后处理脚本支持

### 2. 参数处理功能
- 支持参数占位符（${paramName}）
- 参数模板JSON配置
- SQL参数动态替换
- Aviator表达式引擎支持

### 3. 统一调用入口
- RESTful API调用接口
- 支持GET和POST调用方式
- 多种响应格式（包装/原始数据）
- 请求IP记录和日志追踪

## 数据库表结构

### 1. t_api_config（API配置表）
```sql
CREATE TABLE `t_api_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api_name` varchar(100) NOT NULL COMMENT 'API接口名称',
  `api_code` varchar(50) NOT NULL COMMENT 'API接口编码',
  `api_type` tinyint(4) NOT NULL COMMENT '1-HTTP接口，2-SQL查询',
  `http_method` varchar(10) DEFAULT NULL COMMENT 'HTTP方法',
  `http_url` varchar(500) DEFAULT NULL COMMENT 'HTTP URL',
  `http_headers` text DEFAULT NULL COMMENT 'HTTP请求头',
  `repository_id` int(11) DEFAULT NULL COMMENT '数据源ID',
  `sql_query` text DEFAULT NULL COMMENT 'SQL查询语句',
  `param_template` text DEFAULT NULL COMMENT '参数模板',
  `post_process_script` text DEFAULT NULL COMMENT '后处理脚本',
  `timeout` int(11) DEFAULT 30000 COMMENT '超时时间',
  `status` tinyint(4) DEFAULT 1 COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_api_code` (`api_code`)
);
```

### 2. t_api_execute_log（执行日志表）
```sql
CREATE TABLE `t_api_execute_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `api_config_id` int(11) NOT NULL COMMENT 'API配置ID',
  `api_code` varchar(50) NOT NULL COMMENT 'API编码',
  `request_params` text DEFAULT NULL COMMENT '请求参数',
  `response_result` longtext DEFAULT NULL COMMENT '响应结果',
  `execute_status` tinyint(4) NOT NULL COMMENT '执行状态',
  `error_message` text DEFAULT NULL COMMENT '错误信息',
  `execution_time` bigint(20) DEFAULT NULL COMMENT '执行耗时',
  `execute_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `request_ip` varchar(50) DEFAULT NULL COMMENT '请求IP',
  PRIMARY KEY (`id`)
);
```

## 使用示例

### 1. 创建HTTP接口配置

```bash
POST /api/config/create
{
  "apiName": "获取天气信息",
  "apiCode": "weather_info",
  "apiType": 1,
  "httpMethod": "GET",
  "httpUrl": "https://api.openweathermap.org/data/2.5/weather",
  "httpHeaders": "{\"Content-Type\": \"application/json\"}",
  "paramTemplate": "{\"q\": \"${city}\", \"appid\": \"${api_key}\"}",
  "timeout": 30000,
  "status": 1,
  "description": "获取指定城市天气信息"
}
```

### 2. 创建SQL查询配置

```bash
POST /api/config/create
{
  "apiName": "用户列表查询",
  "apiCode": "user_list",
  "apiType": 2,
  "repositoryId": 1,
  "sqlQuery": "SELECT id, username, email FROM users WHERE status = 1 #{if(name != null)} AND username LIKE '%${name}%' #{end}",
  "paramTemplate": "{\"name\": \"${userName}\"}",
  "postProcessScript": "data.size() > 0 ? data : []",
  "status": 1,
  "description": "查询用户列表"
}
```

### 3. 调用API接口

#### 方式1：标准调用
```bash
POST /api/execute/call
{
  "apiCode": "weather_info",
  "params": {
    "city": "Beijing",
    "api_key": "your_api_key"
  }
}
```

#### 方式2：直接调用（GET）
```bash
GET /api/execute/call/user_list?userName=张三
```

#### 方式3：直接调用（POST）
```bash
POST /api/execute/call/user_list
{
  "userName": "张三"
}
```

#### 方式4：获取纯数据
```bash
GET /api/execute/data/user_list?userName=张三
```

### 4. 管理API配置

#### 查询配置列表
```bash
GET /api/config/page?current=1&pageSize=10&apiName=用户&apiType=2&status=1
```

#### 更新配置
```bash
POST /api/config/update
{
  "id": 1,
  "apiName": "用户详情查询",
  "status": 1
}
```

#### 启用/禁用配置
```bash
POST /api/config/status/1?status=0
```

### 5. 查询执行日志

```bash
GET /api/log/page?current=1&pageSize=10&apiCode=user_list&executeStatus=1
```

## 参数模板语法

### 1. 占位符替换
- 使用 `${paramName}` 格式定义占位符
- 支持嵌套JSON结构
- 自动类型转换

### 2. SQL参数语法
```sql
-- 基本参数替换
SELECT * FROM users WHERE id = ${userId}

-- 条件判断（Aviator语法）
SELECT * FROM users WHERE 1=1 
#{if(name != null)} AND name LIKE '%${name}%' #{end}
#{if(status != null)} AND status = ${status} #{end}
```

### 3. 后处理脚本
```javascript
// 返回数据条数
data.size()

// 返回第一条记录
data.size() > 0 ? data[0] : null

// 数据转换
data.map(item -> {
  'id': item.id,
  'name': item.username,
  'email': item.email
})
```

## 注意事项

1. **安全性**：
   - API编码需要全局唯一
   - 敏感信息不要硬编码在配置中
   - 建议对外部接口进行访问控制

2. **性能**：
   - 合理设置超时时间
   - 避免大数据量查询
   - 定期清理执行日志

3. **错误处理**：
   - 所有接口调用都有日志记录
   - 错误信息会被记录并返回
   - 支持重试机制

4. **扩展性**：
   - 支持自定义后处理脚本
   - 可扩展更多HTTP方法
   - 支持多数据源配置

## 部署说明

1. 执行建表SQL脚本：`backend/src/main/resources/sql/api_module_tables.sql`
2. 确保项目已配置多数据源功能
3. 确保Aviator依赖已添加到项目中
4. 启动应用并测试API功能

## 技术架构

- **框架**：Spring Boot 2.6.4 + MyBatis Plus
- **数据库**：MySQL（支持其他关系型数据库）
- **工具库**：Hutool（HTTP客户端）+ Aviator（表达式引擎）
- **设计模式**：Service层设计 + 统一异常处理