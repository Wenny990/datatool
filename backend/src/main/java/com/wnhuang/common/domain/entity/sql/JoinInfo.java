package com.wnhuang.common.domain.entity.sql;

import lombok.Data;

/**
 * @author wnhuang
 * @Package com.wnhuang.common.domain.entity.sql
 * @date 2025/8/9 22:44
 */
@Data
public class JoinInfo {

    private String owner;
    private String tableName;
    private String tableAlias;
    private String joinType;
    private String joinCondition;
}
