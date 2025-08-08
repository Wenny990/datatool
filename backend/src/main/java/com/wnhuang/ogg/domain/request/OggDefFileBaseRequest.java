package com.wnhuang.ogg.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * @author wnhuang
 * @date 2024/10/14 20:46
 */
@Data
public class OggDefFileBaseRequest {

    @NotNull
    private Integer serverId;

    @NotNull
    private String processName;

}
