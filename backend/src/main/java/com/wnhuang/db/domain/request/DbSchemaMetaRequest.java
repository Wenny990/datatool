package com.wnhuang.db.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wnhuang
 * @date 2024/11/17 16:00
 */
@Data
public class DbSchemaMetaRequest {

    @NotNull
    private Integer repositoryId;

    @NotNull
    private String schemaName;
}
