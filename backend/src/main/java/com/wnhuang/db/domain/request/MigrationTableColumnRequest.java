package com.wnhuang.db.domain.request;

import com.wnhuang.db.domain.entity.ConversionConfig;
import com.wnhuang.db.domain.entity.TableColumnInfo;
import lombok.Data;

import java.util.List;

/**
 * @author wnhuang
 * @date 2025/6/17 23:12
 */
@Data
public class MigrationTableColumnRequest {

    private List<TableColumnInfo> columnInfoList;

    private ConversionConfig conversionConfig;

}
