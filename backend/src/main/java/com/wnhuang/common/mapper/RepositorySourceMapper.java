package com.wnhuang.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.wnhuang.common.domain.entity.RepositorySource;
import org.apache.ibatis.annotations.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Entity com.base.domain.repo.RepositorySource
 */
@Mapper
public interface RepositorySourceMapper extends BaseMapper<RepositorySource> {
    @Select("${sql}")
    @Options(timeout = 120)
    List<LinkedHashMap<String,Object>> execQuerySql(@Param("sql") String sql);

    @Select("select *\n" +
            "        from (\n" +
            "                 ${sqlText}\n" +
            "                 ) temp")
    List<LinkedHashMap<String, Object>> selectBySqlText(@Param("sqlText") String sqlText);

    @Select("${sql}")
    @Options(timeout = 60)
    IPage<LinkedHashMap<String, Object>> execPageQuerySql(IPage page, @Param("sql") String sql);

    @Update("${sql}")
    int execUpdateSql(@Param("sql") String sql);
}




