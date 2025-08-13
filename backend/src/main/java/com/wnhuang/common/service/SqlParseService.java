package com.wnhuang.common.service;

import com.wnhuang.common.domain.entity.SelectSqlObject;
import com.wnhuang.common.domain.request.SqlParseBaseRequest;

/**
 * @author wnhuang
 * @Package com.wnhuang.common.service
 * @date 2025/8/9 22:34
 */
public interface SqlParseService {


    SelectSqlObject parseSelectSql(SqlParseBaseRequest sqlParseBaseRequest);
}
