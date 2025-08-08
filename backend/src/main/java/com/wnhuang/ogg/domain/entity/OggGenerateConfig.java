package com.wnhuang.ogg.domain.entity;

import com.wnhuang.common.domain.entity.MonitorServerInfo;

import com.wnhuang.common.domain.entity.RepositorySource;
import lombok.Data;

import java.util.List;

/**
 * 创建ogg进程的配置类
 *
 * @author wnhuang
 * @created by Administrator on 2023-04-23-21:59
 */

@Data
public class OggGenerateConfig {
    private String processType;

    private String processName;

    private String trailPath;

    private String remoteTrailPath;

    private int megaBytes;

    private int extSeqNo;

    private int extRba;

    private boolean addCheckPointTable;

    private String checkPointTable;

    private MonitorServerInfo sourceServer;

    private MonitorServerInfo targetServer;

    private List<String> tableList;

    private int sourceServerId;

    private int targetServerId;

    private String targetDbSchema;

    private String sourceDbSchema;

    /**
     * 源端数据库对象
     */
    private RepositorySource sourceRepo;

    /**
     * 源端进程文件
     */
    private String sourceTrailPath;

    /**
     * 目标端进程文件
     */
    private String targetTrailPath;

    /**
     * 初始化进程文件
     */
    private String targetRinTrailPath;

    /**
     * 目标端数据库
     */
    private RepositorySource targetRepo;

    /**
     * 抓取进程名称
     */
    private String extName;

    /**
     * 投递进程名称
     */
    private String pumName;

    /**
     * 复制进程名称
     */
    private String repName;

    /**
     * 创建子目录，首次需要
     */
    private Boolean createSubdirs;

    /**
     * 开启表日志
     */
    private Boolean addTranData;
    /**
     * 全部列，oracle 12版本及以上需要
     */
    private Boolean allCol;

    /**
     * mysql_home 目录
     */
    private String mysqlHome;
    private String tranlogOptions;
    private String dbOptions;
    /**
     * 目标端端nls_lang 环境变量
     */
    private String targetNlsLang;

    /**
     * 源端nls_lang 环境变量
     */
    private String sourceNlsLang;

    /**
     * 是否格式化
     */
    private Boolean format;

    /**
     * 格式化版本
     */
    private String formatRelease;

    /**
     * 是否初始化
     */
    private Boolean isInit;

    /**
     * 多线程初始化
     */
    private Boolean isRange;
    /**
     * 初始化线程数量
     */
    private Integer rinRange;

    /**
     * 当前进程
     */
    private Integer currThread;

    /**
     * 源端初始化进程名称
     */
    private String einName;

    /**
     * 目标端初始化进程名称
     */
    private String rinName;

}
