package com.wnhuang.ogg.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wnhuang
 * @date 2024/10/14 20:51
 */
@Data
public class OggDefFileSaveRequest {

    @NotNull
    private String processName;

    @NotNull
    private Integer serverId;

    @NotNull
    private String paramContent;
}
