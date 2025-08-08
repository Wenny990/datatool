package com.wnhuang.common.domain.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TestConnectionResp {
    private String databaseVersion;
    private String driverVersion;
}
