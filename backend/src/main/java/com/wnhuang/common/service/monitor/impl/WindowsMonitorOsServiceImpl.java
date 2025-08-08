package com.wnhuang.common.service.monitor.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.domain.entity.monitor.Cpu;
import com.wnhuang.common.domain.entity.monitor.Mem;
import com.wnhuang.common.domain.entity.monitor.Sys;
import com.wnhuang.common.domain.entity.monitor.SysFile;
import com.wnhuang.common.service.CommandExecuteService;
import com.wnhuang.common.service.monitor.MonitorOsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WindowsMonitorOsServiceImpl implements MonitorOsService {

    @Autowired
    CommandExecuteService commandExecuteService;

    /**
     * 操作系统类型
     *
     * @return 操作系统类型
     */
    @Override
    public String getOsType() {
        return "01";
    }

    private MonitorServerInfo monitorServerInfo;

    private String execCommand(String command) {
        return commandExecuteService.executeCommand(monitorServerInfo, command);
    }

    /**
     * @param server 服务器对象
     */
    @Override
    public void setServer(MonitorServerInfo server) {
        this.monitorServerInfo = server;
    }

    /**
     * 获取cpu使用情况
     *
     * @return cpu使用情况
     */
    @Override
    public Cpu getCpu() {
        String commandCpu = "typeperf \"\\Processor(_Total)\\% Processor Time\" -sc 1";
        String cpuInfo = execCommand(commandCpu);
        Cpu cpu = new Cpu();
        String[] lines = cpuInfo.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.contains("Processor Time")) {
                String[] split = lines[i + 1].split(",");
                cpu.setUsed(Double.parseDouble(split[1].replaceAll("\"", "")) / 100);
                break;
            }
        }
        return cpu;
    }

    /**
     * 获取缓存使用情况
     *
     * @return 缓存使用情况
     */
    @Override
    public Mem getMem() {
        Mem mem = new Mem();
        String totalMemoryCommand = "wmic OS get TotalVisibleMemorySize /Value";
        String freeMemoryCommand = "wmic OS get FreePhysicalMemory /Value";
        String totalMemory = execCommand(totalMemoryCommand);
        String[] split = totalMemory.split("=");
        mem.setTotal(Long.parseLong(split[1].trim()) * 1024L);
        String freeMemory = execCommand(freeMemoryCommand);
        split = freeMemory.split("=");
        mem.setFree(Long.parseLong(split[1].trim()) * 1024L);
        mem.setUsed(mem.getTotal() - mem.getFree());
        return mem;
    }

    /**
     * 获取磁盘信息
     *
     * @return 磁盘使用情况
     */
    @Override
    public List<SysFile> getSysFile() {
        String commandDisk = "wmic logicaldisk get DeviceID, FreeSpace, Size";
        String diskInfo = execCommand(commandDisk);
        String[] lines = diskInfo.split("\n");
        List<SysFile> sysFiles = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String[] split = lines[i].split("\\s+");
            if (split.length > 1){
                SysFile sysFile = new SysFile();
                sysFile.setDirName(split[0]);
                if (split.length > 2) {
                    sysFile.setFree(split[1].trim());
                    sysFile.setTotal(split[2].trim());
                    sysFile.setUsage((Long.parseLong(split[2].trim()) - Long.parseLong(split[1].trim())) * 1.0 / Long.parseLong(split[2].trim()) * 100);
                }
                sysFiles.add(sysFile);
            }
        }
        return sysFiles;
    }

    /**
     * 获取服务器信息
     *
     * @return 服务器信息
     */
    @Override
    public Sys getSys() {
        String commandSys = "wmic os get Caption, CSName, LastBootUpTime /Value";
        String sysInfo = execCommand(commandSys);
        String[] lines = sysInfo.split("\n");
        Sys sys = new Sys();
        for (String line : lines) {
            String[] split = line.split("=");
            if (split.length > 1) {
                if ("Caption".equals(split[0].trim())) {
                    sys.setOsName(split[1].trim());
                }
                if ("CSName".equals(split[0].trim())) {
                    sys.setComputerName(split[1].trim());
                }
                if ("LastBootUpTime".equals(split[0].trim())) {
                    DateTime lastBootUpTime = DateUtil.parse(split[1].substring(0, 14), "yyyyMMddHHmmss");
                    sys.setLastBootUpTime(lastBootUpTime);
                }
            }
        }
        return sys;
    }
}
