package com.wnhuang.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wnhuang.api.domain.entity.ApiExecuteLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * API执行日志Mapper接口
 * @author wnhuang
 * @date 2024/12/01
 */
@Mapper
public interface ApiExecuteLogMapper extends BaseMapper<ApiExecuteLog> {

}