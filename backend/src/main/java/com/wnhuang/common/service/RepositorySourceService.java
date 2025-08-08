package com.wnhuang.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wnhuang.common.domain.entity.RepositoryConfig;
import com.wnhuang.common.domain.entity.RepositorySource;
import com.wnhuang.common.domain.response.RepositoryBasePageResp;
import com.wnhuang.common.domain.response.TestConnectionResp;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface RepositorySourceService extends IService<RepositorySource> {

    /**
     * 执行查询，不分页
     * @param sql sql语句
     * @return
     */
    List<LinkedHashMap<String, Object>> execQuery(String sql);

    /**
     * 执行查询，不分页
     * @param repoId 数据源id
     * @param sql sql语句
     * @return 查询结果
     */
    List<LinkedHashMap<String, Object>> execQuery(String repoId,String sql);

    /**
     * 获取记录数
     * @param repoId 数据源
     * @param sql sql
     * @return 记录数量
     */
    Long getTotalBySql(String repoId,String sql);

    /**
     * 执行分页查询
     * @param
     * @return
     */
    IPage<LinkedHashMap<String, Object>> execPageQuery(IPage page,String sql);

    /**
     * 执行分页查询
     * @param repoId 数据源id
     * @param page 分页对象
     * @param sql sql语句
     * @return 查询结果
     */
    IPage<LinkedHashMap<String, Object>> execPageQuery(String repoId,IPage page,String sql);

    IPage<LinkedHashMap<String, Object>> execPageQuery(RepositoryBasePageResp repoSelectPageRO);

    /**
     * 测试连接是否正常
     * @param repositorySource
     * @return
     */
    TestConnectionResp testConnection(RepositorySource repositorySource);

    /**
     * 切换数据源
     *
     * @param key 数据库配置表主键
     */
    void changeDataSource(String key);



    /**
     * 执行SQL脚本
     *
     * @param key 数据源key
     * @param sql sql脚本
     * @return 返回影响的行数
     */
    int execSqlScript(String key, String sql);

    void resetRepository();

    /**
     * 获取数据库配置列表
     * @return
     */
    List<RepositoryConfig> getRepositoryConfigList();
}
