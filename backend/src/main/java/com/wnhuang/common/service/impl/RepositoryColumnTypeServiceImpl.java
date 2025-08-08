package com.wnhuang.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wnhuang.common.domain.entity.RepositoryColumnType;
import com.wnhuang.common.service.RepositoryColumnTypeService;
import com.wnhuang.common.mapper.RepositoryColumnTypeMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【repository_column_type】的数据库操作Service实现
 * @createDate 2024-11-03 12:22:04
 */
@Service
@CacheConfig( cacheNames = "columnType")
public class RepositoryColumnTypeServiceImpl extends ServiceImpl<RepositoryColumnTypeMapper, RepositoryColumnType>
        implements RepositoryColumnTypeService {

    @Override
    @Cacheable( key = "'list'")
    public List<RepositoryColumnType> list() {
        return this.lambdaQuery().select(
                        RepositoryColumnType::getId,
                        RepositoryColumnType::getRepoTypeId,
                        RepositoryColumnType::getColumnType
                ).orderByAsc(RepositoryColumnType::getRepoTypeId, RepositoryColumnType::getColumnType)
                .list();
    }
}




