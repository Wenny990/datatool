package com.wnhuang.ogg.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wnhuang
 * @date 2025/7/9 15:58
 */
@Data
public class OggCommandRequest {

    @NotNull
    private Integer serverId;

    @NotBlank
    private String command;
}
