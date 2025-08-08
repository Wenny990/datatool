package com.wnhuang.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wnhuang.common.domain.entity.RepositoryMapping;
import com.wnhuang.common.service.RepositoryMappingService;
import com.wnhuang.common.mapper.RepositoryMappingMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【repository_mapping】的数据库操作Service实现
 * @createDate 2024-11-02 20:33:00
 */
@Service
@CacheConfig(cacheNames = "mapping")
public class RepositoryMappingServiceImpl extends ServiceImpl<RepositoryMappingMapper, RepositoryMapping>
        implements RepositoryMappingService {

    @Override
    @Cacheable(key = "'list'")
    public List<RepositoryMapping> list() {
        return this.lambdaQuery()
                .select(
                        RepositoryMapping::getId,
                        RepositoryMapping::getSourceId,
                        RepositoryMapping::getTargetId,
                        RepositoryMapping::getSourceType,
                        RepositoryMapping::getTargetType
                ).orderByDesc(RepositoryMapping::getId).list();
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(RepositoryMapping entity) {
        return super.saveOrUpdate(entity);
    }
}




