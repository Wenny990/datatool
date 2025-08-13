package com.wnhuang.ogg.domain.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wnhuang
 * @date 2025/7/9 15:58
 */
@Data
public class OggCommandListRequest {

    @NotNull
    private Integer serverId;

    @NotEmpty
    private List<String> commandList;
}
