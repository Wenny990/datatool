package com.wnhuang.ogg.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.wnhuang.common.aviator.AviatorUtils;
import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.domain.entity.monitor.Cpu;
import com.wnhuang.common.domain.entity.monitor.Server;
import com.wnhuang.common.service.CommandExecuteService;
import com.wnhuang.common.service.MonitorServerInfoService;
import com.wnhuang.common.service.RepositorySourceService;
import com.wnhuang.common.utils.DesUtil;
import com.wnhuang.common.utils.JschUtil;
import com.wnhuang.common.websocket.handle.shell.CommandOutputCallback;
import com.wnhuang.common.websocket.handle.shell.SshSessionHolder;
import com.wnhuang.ogg.domain.entity.OggConfig;
import com.wnhuang.ogg.domain.entity.OggGenerateConfig;
import com.wnhuang.ogg.domain.entity.OggProcess;
import com.wnhuang.ogg.domain.entity.OggProcessDetail;
import com.wnhuang.ogg.domain.enums.OggProcessTypeEnum;
import com.wnhuang.ogg.domain.request.*;
import com.wnhuang.ogg.domain.response.OggDefFileResp;
import com.wnhuang.ogg.domain.response.OggMonitorVo;
import com.wnhuang.ogg.service.OggProcessService;
import com.wnhuang.ogg.service.ServerCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description OGG进程相关接口实现
 * @createDate 2023-04-17 21:45:39
 */
@Service
@Slf4j
public class OggProcessServiceImpl implements OggProcessService {

    @Autowired
    ServerCommandService serverCommandService;

    @Autowired
    MonitorServerInfoService serverInfoService;

    @Autowired
    RepositorySourceService repositorySourceService;


    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    CommandExecuteService commandExecuteService;


    @Override
    public List<OggProcess> getOggProcess(Integer serverId) {
        List<OggProcess> oggProcessList;
        MonitorServerInfo serverInfo = serverInfoService.getById(serverId);
        oggProcessList = parseToOggProcessList(serverInfo);
        return oggProcessList;
    }

    private List<OggProcess> parseToOggProcessList(MonitorServerInfo serverInfo) {
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 1);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo));
        command = command.replaceAll("\\r?\\n", " ");
        String result = commandExecuteService.executeCommand(serverInfo, command);
        String[] split = result.split("\\r?\\n");
        List<OggProcess> oggProcessList = new ArrayList<>();
        for (String str : split) {
            String[] split1 = str.split(",");
            if (split1.length >= 3) {
                if (split1[0].toUpperCase().startsWith("MANAGER")) {
                    split1[1] = "MGR";
                }
                OggProcess oggProcess = new OggProcess();
                oggProcess.setServerId(serverInfo.getId());
                oggProcess.setProcessName(split1[1]);
                oggProcess.setStatus("RUNNING".equals(split1[2]) ? 1 : "STOPPED".equals(split1[2]) ? 0 : 3);
                oggProcess.setType(getType(split1[1]));
                if (split1.length >= 5) {
                    oggProcess.setLagAtChkpt(split1[3]);
                    oggProcess.setTimeSinceChkpt(split1[4]);
                }
                oggProcessList.add(oggProcess);
            }
        }
        return oggProcessList;
    }

    private Integer getType(String processName) {
        return OggProcessTypeEnum.of(processName.toUpperCase().substring(0, 3)).getType();
    }


    /**
     * 获取指定服务器上的进程状态信息
     *
     * @param serverIdList 服务器id
     * @return
     */
    @Override
    public List<OggMonitorVo> getOggMonitorInfo(List<Integer> serverIdList) {
        List<MonitorServerInfo> list = serverInfoService.listByIds(serverIdList);
        return getOggMonitorInfoByServer(list, false);
    }

    /**
     * 获取服务器的ogg进程信息
     *
     * @param oggServerList 服务器列表
     * @param getDetail     是否获取详细信息
     * @return 返回服务进程列表
     */
    private List<OggMonitorVo> getOggMonitorInfoByServer(List<MonitorServerInfo> oggServerList, Boolean getDetail) {
        long start = System.currentTimeMillis();
        List<OggMonitorVo> oggMonitorVoList = new ArrayList<>(oggServerList.size());
        CountDownLatch countDownLatch = new CountDownLatch(oggServerList.size());
        final TimeInterval timer = new TimeInterval();
        oggServerList.forEach(t -> {
            threadPoolTaskExecutor.execute(() -> {
                try {
                    log.info("开始获取服务器 {} 进程信息", t.getServerName());
                    timer.start(t.getServerName());
                    OggMonitorVo oggMonitorVo = new OggMonitorVo();
                    oggMonitorVo.setId(t.getId());
                    oggMonitorVo.setIp(t.getServerIp());
                    oggMonitorVo.setName(t.getServerName());
                    oggMonitorVo.setOs(t.getServerOs());
                    oggMonitorVo.setOggPath(t.getOggPath());
                    List<OggProcess> oggProcessList = getOggProcess(t.getId());
//                    List<OggProcess> oggProcessListResult = new ArrayList<>(oggProcessList.size());
//                    CountDownLatch subCountDownLatch = new CountDownLatch(oggProcessList.size());
//                    for (OggProcess oggProcess : oggProcessList) {
//                        threadPoolTaskExecutor.execute(() -> {
//                            try {
//                                OggProcess processInfo = getProcessInfo(oggProcess.getServerId(), oggProcess.getProcessName());
//                                if (getDetail) {
//                                    OggProcess processDetail = getProcessDetail(oggProcess.getServerId(), oggProcess.getProcessName());
//                                    processInfo.setDetails(processDetail.getDetails());
//                                }
//                                oggProcessListResult.add(processInfo);
//                            } finally {
//                                subCountDownLatch.countDown();
//                            }
//                        });
//
//                    }
//                    subCountDownLatch.await(3, TimeUnit.MINUTES);
                    oggMonitorVo.setProcess(oggProcessList);
                    if (t.getServerName().contains("发布订阅")) {
                        oggMonitorVo.setPubSubInfo(getPubSubInfo(t));
                    }
                    oggMonitorVo.setUpdateTime(new Date());
                    oggMonitorVoList.add(oggMonitorVo);
                    log.info("获取服务器进程信息成功：{} ({}) {}/{} 耗时：{} ms", t.getServerName(), t.getServerIp(), countDownLatch.getCount(), oggServerList.size(), timer.intervalMs(t.getServerName()));
                } catch (Exception e) {
                    log.error("获取ogg服务器信息失败 {} ，耗时：{} ms, {}", t.getServerName(), timer.intervalMs(t.getServerName()), e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });
        try {
            boolean await = countDownLatch.await(5, TimeUnit.MINUTES);
            long endTime = System.currentTimeMillis();
            log.info("获取服务器进程信息完成，{}，总耗时：{}ms", await ? "成功" : "失败", endTime - start);
        } catch (InterruptedException e) {
            log.error("获取服务器进程出现异常，", e);
        }
        return oggMonitorVoList;
    }

    /**
     * 获取发布订阅监控信息
     *
     * @param monitorServerInfo
     * @return
     */
    private List<LinkedHashMap<String, Object>> getPubSubInfo(MonitorServerInfo monitorServerInfo) {
        Integer repositorySourceId = monitorServerInfo.getRepositorySourceId();
        List<LinkedHashMap<String, Object>> result = null;
        try {
            log.info("获取发布订阅监控信息，服务器：{}", monitorServerInfo.getServerName());
            repositorySourceService.changeDataSource(String.valueOf(repositorySourceId));
            result = repositorySourceService.execQuery("use distribution\n" +
                    " begin\n" +
                    " exec sp_replmonitorhelpsubscription   null,null ,null,0  \n" +
                    " end ");
        } finally {
            DynamicDataSourceContextHolder.poll();
            DynamicDataSourceContextHolder.clear();
        }

        return result;
    }

    private Server getServerInfo(MonitorServerInfo monitorServerInfo) {
        Server server = new Server();
        Cpu cpu = new Cpu();
        cpu.setUsed(0.1);
        server.setCpu(cpu);
        return server;
    }


    @Override
    public List<OggMonitorVo> getOggMonitorInfo() {
        List<MonitorServerInfo> list = serverInfoService.list();
        List<MonitorServerInfo> oggServerList = list.stream().filter(server -> server.getStatus() && "ogg".equals(server.getServerType())).collect(Collectors.toList());
        List<OggMonitorVo> oggMonitorVoList = getOggMonitorInfoByServer(oggServerList, false);
        return oggMonitorVoList;
    }

    @Override
    public Boolean editProcessParams(OggProcess oggProcess) {
        MonitorServerInfo serverInfo = serverInfoService.getById(oggProcess.getServerId());
        String splitChar = "01".equals(serverInfo.getServerOs()) ? "\\" : "/";
        splitChar = "/";
        String path = serverInfo.getOggPath() + splitChar + "dirprm" + splitChar + oggProcess.getProcessName().toLowerCase() + ".prm";
        path = path.replaceAll("\\\\", "/");
        //解密后保存
        return commandExecuteService.uploadFile(serverInfo, path, DesUtil.decrypt(oggProcess.getParams()));
    }

    @Override
    public OggProcess getProcessInfo(Integer serverId, String processName) {
        MonitorServerInfo serverInfo = serverInfoService.getById(serverId);
        OggProcess oggProcess = new OggProcess();
        oggProcess.setServerId(serverId);
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 2);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo).putOnce("processName", processName));
        String resultStr = commandExecuteService.executeCommand(serverInfo, command);
        String[] result = resultStr.split("\\r?\\n");
        if ("MGR".equals(processName)) {
            oggProcess.setProcessName(processName);
            String str = result[0].replace(",", " ");
            oggProcess.setStatus(str.toUpperCase().contains("RUNNING") ? 1 : 0);
            oggProcess.setType(getType(processName));
            return oggProcess;
        }
        if (result.length == 4) {
            // 创建一个临时数组，长度比原数组多 1
            String[] tempArray = new String[result.length + 1];

            // 将第三个位置之前的元素复制到临时数组中
            System.arraycopy(result, 0, tempArray, 0, 2);

            // 将第三个位置之后的元素往后移一位，留出空间给新元素
            System.arraycopy(result, 2, tempArray, 2 + 1, result.length - 2);

            // 在第三个位置插入新元素
            tempArray[2] = "0,0,0,0,0,0,0";
            // 将新元素插入到第三个位置
            result = tempArray;
        }
        String[] split0 = result[0].replace("Initialized", "Last,updated").split(",");
        String[] split1 = result[1].split(",");
        String[] split2 = result[2].split(",");
        String[] split3 = null;
        String[] split4 = null;
        if (result.length > 3) {
            split3 = result[3].split(",");
            split4 = result[4].split(",");
        }
        if (split0.length >= 8) {
            oggProcess.setProcessName(split0[1]);
            oggProcess.setLastStartDate(Convert.toLocalDateTime(split0[4] + " " + split0[5]));
            oggProcess.setStatus("RUNNING".equals(split0[7]) ? 1 : "STOPPED".equals(split0[7]) ? 0 : 3);
            oggProcess.setType(getType(split0[1]));
        }
        oggProcess.setCheckPointLag(split1[2]);
        oggProcess.setProcessId(Convert.toInt(split2[2], 0));
        if (result.length > 3 && split3.length >= 4 && "File".equals(split3[3])) {
            oggProcess.setCheckPointFile(split3[4]);
            oggProcess.setCheckPointTime(Convert.toLocalDateTime(split4[1] + " " + split4[2]));
            for (int i = 0; i < split4.length; i++) {
                if ("RBA".equals(split4[i])) {
                    oggProcess.setCheckPointRba(Convert.toInt(split4[i + 1], 0));
                    break;
                }
            }
        }
        return oggProcess;
    }


    /**
     * 分割字符串数组，按照关键字分成若干个小数组，以关键字开头
     *
     * @param input
     * @param keyword
     * @return
     */
    private List<String[]> splitArr(String[] input, String... keyword) {
        List<String[]> result = new ArrayList<>();
        int start = 0;
        for (int i = 0; i <= input.length; i++) {
            boolean startsWith = false;
            for (String prefix : keyword) {
                if (i == input.length || input[i].startsWith(prefix)) {
                    startsWith = true;
                    break;
                }
            }
            if (i == input.length || startsWith) {
                // 如果当前元素是最后一个元素，或者当前元素的第一个字符是 'a'，则表示需要结束当前子数组
                if (i > start) {
                    // 如果当前子数组不为空，则将其添加到结果列表中
                    String[] subArray = Arrays.copyOfRange(input, start, i);
                    result.add(subArray);
                }

                if (i < input.length && startsWith) {
                    // 如果当前元素的第一个字符是 'a'，则表示需要开始一个新的子数组
                    start = i;
                }
            }
        }
        return result;
    }

    @Override
    public OggProcess getProcessDetail(Integer serverId, String processName) {
        MonitorServerInfo serverInfo = serverInfoService.getById(serverId);
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 3);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo).putOnce("processName", processName));
        String result = commandExecuteService.executeCommand(serverInfo, command);

        OggProcess oggProcess = new OggProcess();
        if (!StringUtils.hasText(result)) {
            return oggProcess;
        }
        List<OggProcessDetail> list = new ArrayList<>();

        List<String[]> parts = splitArr(result.split("\\r?\\n"), "Replicating", "Extracting", "从");
        for (int j = 0; j < parts.size(); j++) {
            String[] split = parts.get(j);
            OggProcessDetail oggProcessDetail = new OggProcessDetail();
            for (int i = 0; i < split.length; i++) {
                String[] split1 = parseStatsTotalCommand(split[i].split(","));
                if (i == 0) {
                    if (split1.length < 5) {
                        continue;
                    }
                    oggProcessDetail.setFrom(split1[2]);
                    oggProcessDetail.setTo(split1[4]);
                } else if (i == 1) {
                    oggProcessDetail.setSinceTime(Convert.toLocalDateTime(split1[4] + " " + split1[5]));
                } else {
                    String type = split1[1];
                    if ("Total".equals(type)) {
                        type = split1[2];
                    }
                    int total = Convert.toInt(split1[3], 0);
                    switch (type) {
                        case "inserts":
                            oggProcessDetail.setInsertTotal(total);
                            break;
                        case "updates":
                            oggProcessDetail.setUpdatesTotal(total);
                            break;
                        case "deletes":
                            oggProcessDetail.setDeletesTotal(total);
                            break;
                        case "discards":
                            oggProcessDetail.setDiscardsTotal(total);
                            break;
                        case "operations":
                            oggProcessDetail.setOperationsTotal(total);
                            if (oggProcessDetail.getOperationsTotal() > 0) {
                                list.add(oggProcessDetail);
                            }
                            break;
                        default:
                    }
                }

            }
        }
        oggProcess.setDetails(list);
        return oggProcess;
    }

    private String[] parseStatsTotalCommand(String[] input) {
        String[] result = new String[6];
        if ("从".equals(input[0])) {
            result[0] = "Extracting";
            result[2] = input[1];
            result[4] = input[3];
        } else if ("自".equals(input[1])) {
            result[4] = input[2];
            result[5] = input[3];
        } else if (input[1].endsWith("总数")) {
            result[3] = input[2];
            if ("操作总数".equals(input[1])) {
                result[1] = "operations";
            } else {
                result[1] = input[1];
            }
        } else {
            result = input;
        }
        return result;
    }

    /**
     * 获取进程的参数信息
     *
     * @param serverId    服务器id
     * @param processName 进程名称
     * @return 参数详情
     */
    @Override
    public OggProcess getProcessParams(Integer serverId, String processName) {
        OggProcess oggProcess = new OggProcess();
        MonitorServerInfo serverInfo = serverInfoService.getById(serverId);
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 6);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo).putOnce("processName", processName.toLowerCase()));
        oggProcess.setParams(commandExecuteService.executeCommand(serverInfo, command));
        return oggProcess;
    }

    /**
     * 获取进程的定义文件参数信息
     *
     * @param oggDefFileRequest
     * @return 定义文件参数详情
     */
    @Override
    public OggDefFileResp getProcessDefParams(OggDefFileBaseRequest oggDefFileRequest) {
        String defParamName = oggDefFileRequest.getProcessName().toLowerCase().replaceFirst("ext", "def");
        MonitorServerInfo serverInfo = serverInfoService.getById(oggDefFileRequest.getServerId());
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 18);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo).putOnce("defParamName", defParamName.toLowerCase()));
        String defParamContent = commandExecuteService.executeCommand(serverInfo, command);
        OggDefFileResp oggDefFileResp = OggDefFileResp.builder()
                .serverId(oggDefFileRequest.getServerId())
                .processName(oggDefFileRequest.getProcessName())
                .defFileName(defParamName)
                .defParamContent(defParamContent)
                .build();
        return oggDefFileResp;
    }

    /**
     * 保存定义文件参数信息
     *
     * @param oggDefFileSaveRequest
     * @return
     */
    @Override
    public Boolean saveProcessDefParams(OggDefFileSaveRequest oggDefFileSaveRequest) {
        MonitorServerInfo serverInfo = serverInfoService.getById(oggDefFileSaveRequest.getServerId());
        String defParamName = oggDefFileSaveRequest.getProcessName().toLowerCase().replaceFirst("ext", "def");
        String splitChar = "/";
        String path = serverInfo.getOggPath() + splitChar + "dirprm" + splitChar + defParamName + ".prm";
        path = path.replaceAll("\\\\", "/");
        return commandExecuteService.uploadFile(serverInfo, path, DesUtil.decrypt(oggDefFileSaveRequest.getParamContent()));
    }

    /**
     * 生成进程的定义文件
     *
     * @param oggDefFileRequest 服务器id
     * @return 生成结果
     */
    @Override
    public OggDefFileResp defgenDefsFile(OggDefFileBaseRequest oggDefFileRequest) {
        MonitorServerInfo serverInfo = serverInfoService.getById(oggDefFileRequest.getServerId());
        String defParamName = oggDefFileRequest.getProcessName().toLowerCase().replaceFirst("ext", "def");
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 19);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo).putOnce("defParamName", defParamName.toLowerCase()));
        String defgenResult = commandExecuteService.executeCommand(serverInfo, command);
        OggDefFileResp oggDefFileResp = OggDefFileResp.builder()
                .serverId(oggDefFileRequest.getServerId())
                .processName(oggDefFileRequest.getProcessName())
                .defFileName(defParamName)
                .defgenResult(defgenResult)
                .build();
        return oggDefFileResp;
    }

    /**
     * 获取进程的定义文件内容
     *
     * @param oggDefFileRequest 查询对象
     * @return 生成结果
     */
    @Override
    public OggDefFileResp getDefsFileContent(OggDefFileBaseRequest oggDefFileRequest) {
        MonitorServerInfo serverInfo = serverInfoService.getById(oggDefFileRequest.getServerId());
        String defParamName = oggDefFileRequest.getProcessName().toLowerCase().replaceFirst("ext", "def");
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 20);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo).putOnce("defParamName", defParamName.toLowerCase()));
        String fileContent = commandExecuteService.executeCommand(serverInfo, command);
        OggDefFileResp oggDefFileResp = OggDefFileResp.builder()
                .serverId(oggDefFileRequest.getServerId())
                .processName(oggDefFileRequest.getProcessName())
                .defFileName(defParamName)
                .fileContent(fileContent)
                .build();
        return oggDefFileResp;
    }

    /**
     * 上传定义文件到目标端
     *
     * @param oggDefFileUploadRequest 请求体
     * @return
     */
    @Override
    public Boolean uploadDefsFile(OggDefFileUploadRequest oggDefFileUploadRequest) {
        MonitorServerInfo serverInfo = serverInfoService.getById(oggDefFileUploadRequest.getServerId());
        MonitorServerInfo targetServerInfo = serverInfoService.getById(oggDefFileUploadRequest.getTargetServerId());
        String defParamName = oggDefFileUploadRequest.getProcessName().toLowerCase().replaceFirst("ext", "def");
        String splitChar = "/";
        String path = targetServerInfo.getOggPath() + splitChar + "dirdef" + splitChar + defParamName + ".def";
        path = path.replaceAll("\\\\", "/");
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 20);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo).putOnce("defParamName", defParamName.toLowerCase()));
        String fileContent = commandExecuteService.executeCommand(serverInfo, command);
        return commandExecuteService.uploadFile(targetServerInfo, path, fileContent);
    }

    /**
     * 查看进程的日志
     *
     * @param serverId    服务器id
     * @param processName 进程名称
     * @return 进程日志内容
     */
    @Override
    public OggProcess getProcessReport(Integer serverId, String processName) {
        OggProcess oggProcess = new OggProcess();
        MonitorServerInfo serverInfo = serverInfoService.getById(serverId);
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 8);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo).putOnce("processName", processName));
        oggProcess.setReport(commandExecuteService.executeCommand(serverInfo, command));
        return oggProcess;
    }

    /**
     * 启动或关闭进程
     *
     * @param serverId    服务器id
     * @param processName 进程名称
     * @param isStart     是启动
     * @return 参数详情
     */
    public Boolean startOrStopProcess(Integer serverId, String processName, Boolean isStart) {
        MonitorServerInfo serverInfo = serverInfoService.getById(serverId);
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), isStart ? 4 : 5);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo).putOnce("processName", processName));
        commandExecuteService.executeCommand(serverInfo, command);
        return true;
    }

    /**
     * 删除进程
     *
     * @param serverId    服务器id
     * @param processName 进程名称
     * @return
     */
    @Override
    public String deleteProcess(Integer serverId, String processName) {
        MonitorServerInfo serverInfo = serverInfoService.getById(serverId);
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 7);
        command = AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo).putOnce("processName", processName));
        String result = commandExecuteService.executeCommand(serverInfo, command);
        return result;
    }

    /**
     * 根据配置类，生成进程参数
     *
     * @param oggGenerateConfig 生成配置
     * @return 生成的参数
     */
    @Override
    public OggConfig generateOggConfig(OggGenerateConfig oggGenerateConfig) {
        OggConfig oggConfig = new OggConfig();
        oggConfig.setSourceServerId(oggGenerateConfig.getSourceServer().getId());
        oggConfig.setTargetServerId(oggGenerateConfig.getTargetServer().getId());

        MonitorServerInfo serverInfo = oggGenerateConfig.getSourceServer();

        //源端命令
        String sourceCommand = serverCommandService.getCommand(serverInfo.getServerOs(), 10);
        sourceCommand = AviatorUtils.compileSql(sourceCommand, JSONUtil.parseObj(oggGenerateConfig));
        oggConfig.setSourceCommand(sourceCommand);

        //目标端命令
        String targetCommand = serverCommandService.getCommand(serverInfo.getServerOs(), 11);
        targetCommand = AviatorUtils.compileSql(targetCommand, JSONUtil.parseObj(oggGenerateConfig));
        oggConfig.setTargetCommand(targetCommand);

        //抓取进程参数
        String extParams = serverCommandService.getCommand(serverInfo.getServerOs(), 12);
        extParams = AviatorUtils.compileSql(extParams, JSONUtil.parseObj(oggGenerateConfig));
        oggConfig.setExtParams(extParams);

        //投递进程参数
        String pumParams = serverCommandService.getCommand(serverInfo.getServerOs(), 13);
        pumParams = AviatorUtils.compileSql(pumParams, JSONUtil.parseObj(oggGenerateConfig));
        oggConfig.setPumParams(pumParams);

        //复制进程参数
        String mapSql = serverCommandService.getCommand(serverInfo.getServerOs(), 15);
        String repParams = serverCommandService.getCommand(serverInfo.getServerOs(), 14);
        List<LinkedHashMap<String, Object>> keyParams = getKeys(oggGenerateConfig.getSourceRepo().getId(), oggGenerateConfig);
        mapSql = AviatorUtils.compileSql(mapSql, JSONUtil.parseObj(oggGenerateConfig).putOnce("keyParams", keyParams));
        List<LinkedHashMap<String, Object>> mapParams = getParams(oggGenerateConfig.getTargetRepo().getId(), mapSql);
        repParams = AviatorUtils.compileSql(repParams, JSONUtil.parseObj(oggGenerateConfig).putOnce("mapParams", mapParams));
        oggConfig.setRepParams(repParams);

        //初始化
        if (oggGenerateConfig.getIsInit()) {
            //源端初始化参数
            String einParams = serverCommandService.getCommand(serverInfo.getServerOs(), 16);
            einParams = AviatorUtils.compileSql(einParams, JSONUtil.parseObj(oggGenerateConfig));
            oggConfig.setEinParams(einParams);

            //目标端初始化参数
            String rinParams = serverCommandService.getCommand(serverInfo.getServerOs(), 17);

            if (oggGenerateConfig.getIsRange()) {
                Integer threadCount = oggGenerateConfig.getRinRange();
                List<String> rinParamsList = new ArrayList<>(threadCount);
                for (int i = 0; i < threadCount; i++) {
                    oggGenerateConfig.setCurrThread(i);
                    rinParamsList.add(AviatorUtils.compileSql(rinParams, JSONUtil.parseObj(oggGenerateConfig)));
                }
                oggConfig.setRinParams(rinParamsList);
            } else {
                oggGenerateConfig.setCurrThread(-1);
                String s = AviatorUtils.compileSql(rinParams, JSONUtil.parseObj(oggGenerateConfig));
                List<String> rinParamsList = new ArrayList<String>();
                rinParamsList.add(s);
                oggConfig.setRinParams(rinParamsList);
            }

        }
        return oggConfig;
    }

    public List<LinkedHashMap<String, Object>> getParams(Serializable key, String sql) {
        try {
            repositorySourceService.changeDataSource(Convert.toStr(key));
            return repositorySourceService.execQuery(sql);
        } finally {
            DynamicDataSourceContextHolder.poll();
            DynamicDataSourceContextHolder.clear();
        }
    }

    public List<LinkedHashMap<String, Object>> getKeys(Serializable key, OggGenerateConfig oggGenerateConfig) {
        List<String> tableList = oggGenerateConfig.getTableList();
        String sourceDbSchema = oggGenerateConfig.getSourceDbSchema();
        List<LinkedHashMap<String, Object>> hashMaps = new ArrayList<>();
        for (String s : tableList) {
//            List<LinkedHashMap<String, Object>> primaryKey = repositorySourceService.getPrimaryKey(Convert.toStr(key), sourceDbSchema, s);
//            hashMaps.addAll(primaryKey);
        }
        return hashMaps;

    }

    @PreDestroy
    public void cleanup() {
        // 在应用程序退出前执行清理或处理操作
        // 如关闭数据库连接、销毁线程池等
        JschUtil.closeSessions();
    }

    /**
     * 使用shell执行多个命令
     *
     * @param oggConfig ogg设置
     * @return 结果
     */
    @Override
    public String executeMultipleCommands(OggConfig oggConfig) {
        Assert.isTrue(oggConfig.getSourceOrTarget().equals(1) || oggConfig.getSourceOrTarget().equals(2), "非法操作");
        MonitorServerInfo serverInfo = null;
        List<String> commands = null;
        if (oggConfig.getSourceOrTarget().equals(1)) {
            serverInfo = serverInfoService.getById(oggConfig.getSourceServerId());
            commands = Arrays.asList(oggConfig.getSourceCommand().split("\\r?\\n"));
        } else {
            serverInfo = serverInfoService.getById(oggConfig.getTargetServerId());
            commands = Arrays.asList(oggConfig.getTargetCommand().split("\\r?\\n"));
        }
        Assert.isTrue(serverInfo != null && !commands.isEmpty(), "未获取到服务器或命令");
        return JschUtil.executeCommands(serverInfo, commands);
    }

    /**
     * 预览命令
     *
     * @param request 请求体
     * @return
     */
    @Override
    public String commandPreview(OggCommandRequest request) {
        MonitorServerInfo serverInfo = serverInfoService.getById(request.getServerId());
        String command = request.getCommand();
        return JschUtil.execCommand(serverInfo, command);
    }

    @Override
    public String commandByOgg(OggCommandListRequest request) {
        MonitorServerInfo serverInfo = serverInfoService.getById(request.getServerId());
        String endChar = serverInfo.getServerOs().equals("01") ? "\r" : "\n";
        String command = serverCommandService.getCommand(serverInfo.getServerOs(), 23);
        SshSessionHolder shellSession = commandExecuteService.createShellSession(serverInfo);
        final String[] out = {""};
        shellSession.init(new CommandOutputCallback() {
            @Override
            public void onOutput(String output) {
                out[0] = out[0] + output;
            }

            @Override
            public void onError(String error) {
                out[0] = out[0] + error;
            }

            @Override
            public void onComplete(int exitCode) {
                out[0] = out[0] + "执行结束，返回：" + exitCode;
            }
        });
        shellSession.write(AviatorUtils.compileSql(command, JSONUtil.parseObj(serverInfo)) + endChar);

        try {
            while (StrUtil.isBlank(out[0])) {
                Thread.sleep(200);
            }
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        request.getCommandList().forEach(t -> {
            int length = out[0].length();
            shellSession.write(t + endChar);
            try {
                while (out[0].length() <= length){
                    Thread.sleep(200);
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        shellSession.close();
        out[0] = out[0].replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "");
        return out[0];
    }
}




