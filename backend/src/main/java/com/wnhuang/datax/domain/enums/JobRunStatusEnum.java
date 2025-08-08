package com.wnhuang.datax.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wnhuang
 * @date 2024/10/23 00:08
 */
@AllArgsConstructor
@Getter
public enum JobRunStatusEnum {
    NOT_START(-1, "未开始"),
    RUNNING(0, "运行中"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败");

    private final Integer status;
    private final String desc;

    private static Map<Integer, JobRunStatusEnum> cache;

    static {
        cache = Arrays.stream(JobRunStatusEnum.values()).collect(Collectors.toMap(JobRunStatusEnum::getStatus, Function.identity()));
    }

    public static JobRunStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
