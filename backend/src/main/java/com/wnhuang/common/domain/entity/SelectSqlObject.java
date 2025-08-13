package com.wnhuang.common.domain.entity;

import com.wnhuang.common.domain.entity.sql.JoinInfo;
import com.wnhuang.common.domain.entity.sql.SelectItem;
import lombok.Data;

import java.util.List;

/**
 * 查询sql对象
 * @author wnhuang
 * @Package com.wnhuang.common.domain.entity
 * @date 2025/8/9 22:39
 */
@Data
public class SelectSqlObject {

    private String sql;

    private String dbType;

    private List<JoinInfo> joinList;

    private List<SelectItem> selectList;

    private String where;
}
