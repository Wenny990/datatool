package com.wnhuang.ogg.domain.response;


import com.wnhuang.common.domain.entity.monitor.Server;
import com.wnhuang.ogg.domain.entity.OggProcess;
import lombok.Data;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class OggMonitorVo {

    private Integer id;

    private String name;

    private String ip;

    private String oggPath;

    private List<OggProcess> process;

    private String os;

    private Server server;


    /**
     * 发布订阅信息
     */
    private List<LinkedHashMap<String, Object>> pubSubInfo;

    /**
     * 更新时间
     */
    private Date updateTime;

}
