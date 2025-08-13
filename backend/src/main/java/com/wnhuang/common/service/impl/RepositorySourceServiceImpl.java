package com.wnhuang.common.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.druid.DruidConfig;
import com.baomidou.dynamic.datasource.creator.druid.DruidDataSourceCreator;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wnhuang.common.domain.entity.RepositoryConfig;
import com.wnhuang.common.domain.entity.RepositorySource;
import com.wnhuang.common.domain.response.RepositoryBasePageResp;
import com.wnhuang.common.domain.response.TestConnectionResp;
import com.wnhuang.common.exception.BusinessException;
import com.wnhuang.common.exception.DatabaseException;
import com.wnhuang.common.mapper.RepositorySourceMapper;
import com.wnhuang.common.service.RepositoryConfigService;
import com.wnhuang.common.service.RepositorySourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "db")
public class RepositorySourceServiceImpl extends ServiceImpl<RepositorySourceMapper, RepositorySource>
        implements RepositorySourceService {

    @Autowired
    DataSource dataSource;

    @Autowired
    DruidDataSourceCreator dataSourceCreator;

    @Autowired
    RepositoryConfigService repositoryConfigService;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    /**
     * 执行查询语句，不分页
     *
     * @param sql
     * @return
     */
    @Override
    public List<LinkedHashMap<String, Object>> execQuery(String sql) {
        try {
            return this.baseMapper.execQuerySql(sql);
        } finally {
            DynamicDataSourceContextHolder.poll();
            DynamicDataSourceContextHolder.clear();
        }
    }

    /**
     * 执行查询，不分页
     *
     * @param repoId 数据源id
     * @param sql    sql语句
     * @return 查询结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> execQuery(String repoId, String sql) {
        try {
            this.changeDataSource(repoId);
            return this.baseMapper.execQuerySql(sql);
        } finally {
            this.resetRepository();
        }
    }

    /**
     * 获取记录数
     *
     * @param repoId 数据源
     * @param sql    sql
     * @return 记录数量
     */
    @Override
    public Long getTotalBySql(String repoId, String sql) {
        sql = "select count(*) total from (" + sql + ") temp";
        List<LinkedHashMap<String, Object>> totalMapResultList = this.execQuery(repoId, sql);
        LinkedHashMap<String, Object> totalMap = totalMapResultList.get(0);
        String key = totalMap.keySet().stream().iterator().next();
        return Convert.toLong(totalMap.get(key));
    }

    /**
     * 执行分页查询
     *
     * @param page
     * @param sql
     * @return
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> execPageQuery(IPage page, String sql) {
        try {
            return this.baseMapper.execPageQuerySql(page, sql);
        } finally {
            DynamicDataSourceContextHolder.poll();
            DynamicDataSourceContextHolder.clear();
        }
    }

    public IPage<LinkedHashMap<String, Object>> execPageQuery(RepositoryBasePageResp repoSelectPageRO) {
        Integer repoId = repoSelectPageRO.getRepoId();
        Page<Object> page = Page.of(repoSelectPageRO.getCurrent(), repoSelectPageRO.getPageSize());
        String sqlText = repoSelectPageRO.getSqlText();
        return execPageQuery(String.valueOf(repoId), page, sqlText);
    }

    /**
     * 执行分页查询
     *
     * @param repoId 数据源id
     * @param page   分页对象
     * @param sql    sql语句
     * @return 查询结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> execPageQuery(String repoId, IPage page, String sql) {
        Assert.notNull(repoId, "数据源id不能为空");
        Assert.isTrue(StrUtil.isNotBlank(sql), "sql语句不能为空");
        try {
            this.changeDataSource(repoId);
            return this.baseMapper.execPageQuerySql(page, sql);
        } finally {
            DynamicDataSourceContextHolder.poll();
            DynamicDataSourceContextHolder.clear();
        }
    }

    /**
     * 校验连接
     *
     * @param repositorySource
     * @return
     */
    @Override
    public TestConnectionResp testConnection(RepositorySource repositorySource) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        DruidConfig druidConfig = new DruidConfig();
        druidConfig.setMaxWait(3000);
        druidConfig.setBreakAfterAcquireFailure(true); // 获取连接失败后标记数据源为不可用
        druidConfig.setConnectionErrorRetryAttempts(0); // 连接错误后重试次数
        dataSourceProperty.setDruid(druidConfig);
        BeanUtils.copyProperties(repositorySource, dataSourceProperty);
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        Connection connection = null;

        try {
            log.info("测试数据源，开始连接数据源：{}", dataSourceProperty);
            DataSource ds = dataSourceCreator.createDataSource(dataSourceProperty);
            connection = ds.getConnection();
            log.info("连接成功");
            StringBuilder sb = new StringBuilder();
            DatabaseMetaData metaData = connection.getMetaData();
            sb.append(metaData.getDatabaseProductName()).append(" ")
                    .append(metaData.getDatabaseProductVersion()).append(" ")
                    .append(metaData.getDatabaseMajorVersion())
                    .append( ".").append(metaData.getDatabaseMinorVersion());
            sb.append(" \r\n");
            log.info("数据库信息：{}", sb);
            // 3. 获取 JDBC 驱动的版本信息
            String driverName = metaData.getDriverName();
            String driverVersion = metaData.getDriverVersion();
            return TestConnectionResp
                    .builder()
                    .databaseVersion(sb.toString())
                    .driverVersion(driverName + " " + driverVersion)
                    .build();
        } catch (Exception ex) {
            log.error("连接数据库失败，请检查配置信息", ex);
            throw new BusinessException(ex.getMessage() + ": " + ex.getCause().getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 切换数据源
     *
     * @param key 数据库配置表主键
     */
    @Override
    public void changeDataSource(String key) {
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
        synchronized (RepositorySourceServiceImpl.class) {
            if (!dynamicRoutingDataSource.getDataSources().containsKey(key)) {
                DynamicDataSourceContextHolder.push("master");
                RepositorySource repositorySource = this.getById(key);
                Assert.notNull(repositorySource, "根据主键{" + key + "}获取数据库配置信息失败，请检查！");
                DataSource dataSource;
                try {
                    dataSource = createDataSource(repositorySource);
                    dynamicRoutingDataSource.addDataSource(key, dataSource);
                } catch (Exception e) {
                    log.error("添加数据源失败, key: {}", key, e);
                    throw new DatabaseException("创建数据源失败: " + e.getMessage()  + e.getCause().getMessage());
                }

            }
        }
        DynamicDataSourceContextHolder.push(key);
        log.info("切换到数据源：{}", key);
    }


    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(RepositorySource entity) {
        return super.saveOrUpdate(entity);
    }


    /**
     * 创建数据源
     *
     * @param repositorySource 数据源对象
     * @return 返回数据源
     */
    private DataSource createDataSource(RepositorySource repositorySource) {
        log.info("开始创建数据源，repositorySource: {}", repositorySource);

        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(repositorySource, dataSourceProperty);

        dataSourceProperty.setLazy(false);
        // 添加获取 remarks 的连接属性
        Map<String, String> connectParams = new HashMap<>();

        // MySQL
        if (dataSourceProperty.getUrl() != null && dataSourceProperty.getUrl().contains("mysql")) {

            connectParams.put("remarks", "true");

        }

        // Oracle
        if (dataSourceProperty.getUrl() != null && dataSourceProperty.getUrl().contains("oracle")) {
            connectParams.put("remarks", "true");
            connectParams.put("remarksReporting", "true");
        }

        // PostgreSQL
        if (dataSourceProperty.getUrl() != null && dataSourceProperty.getUrl().contains("postgresql")) {
            connectParams.put("remarks", "true");
            connectParams.put("preferQueryMode", "extended");
        }

        // 设置连接参数
        if (!connectParams.isEmpty()) {
            dataSourceProperty.setDriverClassName(dataSourceProperty.getDriverClassName());
            // 可以通过URL参数或属性设置
            StringBuilder urlBuilder = new StringBuilder(dataSourceProperty.getUrl());
            if (!dataSourceProperty.getUrl().contains("?")) {
                urlBuilder.append("?");
            } else {
                urlBuilder.append("&");
            }

            boolean first = true;
            for (Map.Entry<String, String> entry : connectParams.entrySet()) {
                if (!first) {
                    urlBuilder.append("&");
                }
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }

            dataSourceProperty.setUrl(urlBuilder.toString());
        }

        return dataSourceCreator.createDataSource(dataSourceProperty);
    }


    @Override
    public int execSqlScript(String key, String sql) {
        changeDataSource(key);
        String[] split = sql.split(";");
        for (String s : split) {
            if (StrUtil.isNotBlank(s)) {
                this.baseMapper.execUpdateSql(s);
            }
        }
        resetRepository();
        return split.length;
    }

    /**
     *
     */
    @Override
    public void resetRepository() {
        DynamicDataSourceContextHolder.poll();
        DynamicDataSourceContextHolder.clear();
        log.info("切换到默认数据源。。。");
    }

    /**
     * 获取数据库配置列表
     *
     * @return 数据库配置列表
     */
    @Override
    public List<RepositoryConfig> getRepositoryConfigList() {
        return repositoryConfigService.list();
    }
}




