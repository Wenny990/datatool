package com.wnhuang.common.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wnhuang
 * @Package com.wnhuang.common.domain.request
 * @date 2025/8/9 22:36
 */
@Data
public class SqlParseBaseRequest {

    @NotNull
    private Integer repoId;

    @NotBlank
    private String sql;
}
