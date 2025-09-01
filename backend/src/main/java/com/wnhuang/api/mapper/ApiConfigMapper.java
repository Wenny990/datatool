package com.wnhuang.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wnhuang.api.domain.entity.ApiConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * API配置Mapper接口
 * @author wnhuang
 * @date 2024/12/01
 */
@Mapper
public interface ApiConfigMapper extends BaseMapper<ApiConfig> {

    /**
     * 根据API编码查询配置
     *
     * @param apiCode API编码
     * @return API配置
     */
    @Select("SELECT * FROM t_api_config WHERE api_code = #{apiCode} AND status = 1")
    ApiConfig findByApiCode(@Param("apiCode") String apiCode);

    /**
     * 检查API编码是否已存在
     *
     * @param apiCode API编码
     * @param id      排除的ID（更新时使用）
     * @return 存在的记录数
     */
    @Select("SELECT COUNT(*) FROM t_api_config WHERE api_code = #{apiCode} AND id != #{id}")
    int checkApiCodeExists(@Param("apiCode") String apiCode, @Param("id") Integer id);
}