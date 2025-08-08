package com.wnhuang.chat.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wnhuang
 * @date 2025/3/1 01:46
 */
@Data
public class ChatBaseRequest {

    @NotBlank
    private String prompt;

    @NotBlank
    private String groupId;

    @NotBlank
    private String model;
}
