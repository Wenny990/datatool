package com.wnhuang.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wnhuang.api.domain.entity.ApiConfig;
import com.wnhuang.api.domain.request.ApiConfigRequest;

/**
 * API配置服务接口
 * @author wnhuang
 * @date 2024/12/01
 */
public interface ApiConfigService extends IService<ApiConfig> {

    /**
     * 创建API配置
     *
     * @param request API配置请求
     * @return 是否成功
     */
    boolean createApiConfig(ApiConfigRequest request);

    /**
     * 更新API配置
     *
     * @param request API配置请求
     * @return 是否成功
     */
    boolean updateApiConfig(ApiConfigRequest request);

    /**
     * 删除API配置
     *
     * @param id API配置ID
     * @return 是否成功
     */
    boolean deleteApiConfig(Integer id);

    /**
     * 分页查询API配置
     *
     * @param current  当前页码
     * @param pageSize 页面大小
     * @param apiName  API名称（可选）
     * @param apiType  API类型（可选）
     * @param status   状态（可选）
     * @return 分页结果
     */
    IPage<ApiConfig> pageApiConfigs(Integer current, Integer pageSize, String apiName, Integer apiType, Integer status);

    /**
     * 根据API编码查询配置
     *
     * @param apiCode API编码
     * @return API配置
     */
    ApiConfig getByApiCode(String apiCode);

    /**
     * 检查API编码是否已存在
     *
     * @param apiCode API编码
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean checkApiCodeExists(String apiCode, Integer excludeId);

    /**
     * 启用/禁用API配置
     *
     * @param id     API配置ID
     * @param status 状态：1-启用，0-禁用
     * @return 是否成功
     */
    boolean updateStatus(Integer id, Integer status);
}