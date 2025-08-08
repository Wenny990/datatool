package com.wnhuang.ogg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wnhuang
 * @date 2024/11/1 01:17
 */
@AllArgsConstructor
@Getter
public enum OggProcessTypeEnum {

    OGG_MGR(0 , "管理进程" , "MGR"),
    OGG_EXT(1 , "抓取进程" , "EXT"),
    OGG_PUM(2 , "推送进程" , "PUM"),
    OGG_REP(3, "投递进程", "REP"),
    OGG_EIN(4, "源端初始化进程", "EIN"),
    OGG_RIN(5, "目标端初始化进程", "RIN");

    private Integer type;
    private String desc;
    private String startWith;

    private static Map<String , OggProcessTypeEnum> oggProcessTypeEnumMap = new HashMap<>();
    static {
        for (OggProcessTypeEnum oggProcessTypeEnum : OggProcessTypeEnum.values()) {
            oggProcessTypeEnumMap.put(oggProcessTypeEnum.startWith , oggProcessTypeEnum);
        }
    }

    public static OggProcessTypeEnum of(String startWith) {
        return oggProcessTypeEnumMap.get(startWith);
    }
}
