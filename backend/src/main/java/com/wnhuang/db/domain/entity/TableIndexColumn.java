package com.wnhuang.db.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表索引列
 * @author wnhuang
 * @date 2024/11/17 15:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableIndexColumn {

    private String columnName;

    private short ordinalPosition;

}
