package com.wnhuang.db.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wnhuang
 * @date 2024/11/17 15:58
 */
@Data
public class DbTableMetaRequest {

    @NotNull
    private Integer repositoryId;

    @NotNull
    private String schemaName;

    @NotNull
    private String tableName;

}
