package com.wnhuang.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wnhuang.api.domain.entity.ApiConfig;
import com.wnhuang.api.domain.request.ApiConfigRequest;
import com.wnhuang.api.mapper.ApiConfigMapper;
import com.wnhuang.api.service.ApiConfigService;
import com.wnhuang.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * API配置服务实现类
 * @author wnhuang
 * @date 2024/12/01
 */
@Slf4j
@Service
public class ApiConfigServiceImpl extends ServiceImpl<ApiConfigMapper, ApiConfig> implements ApiConfigService {

    @Override
    public boolean createApiConfig(ApiConfigRequest request) {
        // 检查API编码是否已存在
        if (checkApiCodeExists(request.getApiCode(), null)) {
            throw new BusinessException("API编码已存在");
        }

        // 验证配置
        validateApiConfig(request);

        ApiConfig apiConfig = new ApiConfig();
        BeanUtils.copyProperties(request, apiConfig);
        apiConfig.setCreateTime(LocalDateTime.now());
        apiConfig.setUpdateTime(LocalDateTime.now());
        
        // 设置默认值
        if (apiConfig.getTimeout() == null) {
            apiConfig.setTimeout(30000); // 默认30秒超时
        }
        if (apiConfig.getStatus() == null) {
            apiConfig.setStatus(1); // 默认启用
        }

        return save(apiConfig);
    }

    @Override
    public boolean updateApiConfig(ApiConfigRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("更新时ID不能为空");
        }

        ApiConfig existingConfig = getById(request.getId());
        if (existingConfig == null) {
            throw new BusinessException("API配置不存在");
        }

        // 检查API编码是否已存在（排除当前记录）
        if (checkApiCodeExists(request.getApiCode(), request.getId())) {
            throw new BusinessException("API编码已存在");
        }

        // 验证配置
        validateApiConfig(request);

        ApiConfig apiConfig = new ApiConfig();
        BeanUtils.copyProperties(request, apiConfig);
        apiConfig.setUpdateTime(LocalDateTime.now());

        return updateById(apiConfig);
    }

    @Override
    public boolean deleteApiConfig(Integer id) {
        ApiConfig existingConfig = getById(id);
        if (existingConfig == null) {
            throw new BusinessException("API配置不存在");
        }

        return removeById(id);
    }

    @Override
    public IPage<ApiConfig> pageApiConfigs(Integer current, Integer pageSize, String apiName, Integer apiType, Integer status) {
        Page<ApiConfig> page = new Page<>(current, pageSize);
        
        LambdaQueryWrapper<ApiConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(apiName), ApiConfig::getApiName, apiName)
                   .eq(apiType != null, ApiConfig::getApiType, apiType)
                   .eq(status != null, ApiConfig::getStatus, status)
                   .orderByDesc(ApiConfig::getUpdateTime);

        return page(page, queryWrapper);
    }

    @Override
    public ApiConfig getByApiCode(String apiCode) {
        return baseMapper.findByApiCode(apiCode);
    }

    @Override
    public boolean checkApiCodeExists(String apiCode, Integer excludeId) {
        int excludeIdVal = excludeId != null ? excludeId : -1;
        return baseMapper.checkApiCodeExists(apiCode, excludeIdVal) > 0;
    }

    @Override
    public boolean updateStatus(Integer id, Integer status) {
        ApiConfig existingConfig = getById(id);
        if (existingConfig == null) {
            throw new BusinessException("API配置不存在");
        }

        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setId(id);
        apiConfig.setStatus(status);
        apiConfig.setUpdateTime(LocalDateTime.now());

        return updateById(apiConfig);
    }

    /**
     * 验证API配置
     */
    private void validateApiConfig(ApiConfigRequest request) {
        if (request.getApiType() == 1) {
            // HTTP接口类型验证
            if (StrUtil.isBlank(request.getHttpMethod())) {
                throw new BusinessException("HTTP请求方法不能为空");
            }
            if (StrUtil.isBlank(request.getHttpUrl())) {
                throw new BusinessException("HTTP请求URL不能为空");
            }
        } else if (request.getApiType() == 2) {
            // 数据源SQL查询类型验证
            if (request.getRepositoryId() == null) {
                throw new BusinessException("数据源ID不能为空");
            }
            if (StrUtil.isBlank(request.getSqlQuery())) {
                throw new BusinessException("SQL查询语句不能为空");
            }
        } else {
            throw new BusinessException("不支持的API类型");
        }
    }
}