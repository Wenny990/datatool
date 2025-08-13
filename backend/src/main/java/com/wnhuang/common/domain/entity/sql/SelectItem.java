package com.wnhuang.common.domain.entity.sql;

import lombok.Data;

/**
 * @author wnhuang
 * @Package com.wnhuang.common.domain.entity.sql
 * @date 2025/8/9 22:43
 */
@Data
public class SelectItem {

    private String str;

    private String alias;

    private String expression;
}
