package com.wnhuang.common.service.monitor.impl;

import cn.hutool.core.date.DateUtil;

import com.wnhuang.common.domain.entity.MonitorServerInfo;

import com.wnhuang.common.domain.entity.monitor.Cpu;
import com.wnhuang.common.domain.entity.monitor.Mem;
import com.wnhuang.common.domain.entity.monitor.Sys;
import com.wnhuang.common.domain.entity.monitor.SysFile;
import com.wnhuang.common.service.CommandExecuteService;
import com.wnhuang.common.service.monitor.MonitorOsService;
import com.wnhuang.common.utils.Arith;
import com.wnhuang.common.utils.JschUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class LinuxMonitorOsServiceImpl implements MonitorOsService {

    private MonitorServerInfo monitorServerInfo;

    @Autowired
    CommandExecuteService commandExecuteService;


    /**
     * 操作系统类型
     *
     * @return 操作系统类型
     */
    @Override
    public String getOsType() {
        return "02";
    }

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
        String commandCpu = "top -bn1 | grep load | awk '{printf \"%.2f%%\\n\", $(NF-2)}'";
        String cpuInfo = execCommand(commandCpu);
        Cpu cpu = new Cpu();
        String[] lines = cpuInfo.split("\n");
        cpu.setUsed(Arith.div(Double.parseDouble(lines[0].replaceAll("%", "")), 100) );
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
        String memoryCommand = "free -b | grep Mem  | awk '{printf \"%d,%d\\n\", $2,$7}'";
        String memory = execCommand(memoryCommand);
        String[] split = memory.split(",");
        mem.setTotal(Long.parseLong(split[0].trim()));
        mem.setFree(Long.parseLong(split[1].trim()));
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
        String commandDisk = "df -P | grep -vE '^Filesystem|tmpfs|cdrom|overlay' | awk '{ print $2\",\"$3\",\"$4\",\"$5\",\"$6 }'";
        String diskInfo = execCommand(commandDisk);
        String[] lines = diskInfo.split("\n");
        List<SysFile> sysFiles = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String[] split = lines[i].split(",");
            SysFile sysFile = new SysFile();
            if (split.length > 4) {
                sysFile.setDirName(split[4]);
                sysFile.setFree(new BigDecimal(split[2].trim()).multiply(new BigDecimal(1024)).toString());
                sysFile.setTotal(new BigDecimal(split[0].trim()).multiply(new BigDecimal(1024)).toString());
                sysFile.setUsage(Double.parseDouble(split[3].trim().replaceAll("%","")));
            }
            sysFiles.add(sysFile);
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
        String commandHostName = "hostname";
        String commandSysOs = "cat /etc/system-release";
        Sys sys = new Sys();
        String hostname = execCommand(commandHostName);
        sys.setComputerName(hostname);
        String osInfo  = execCommand(commandSysOs);
        sys.setOsName(osInfo);
        String commandBootTime = "cat /proc/uptime";
        String output = execCommand(commandBootTime);
        String bootTime = output.split(" ")[0].trim();
        Date lastBootUpTime = null;
        BigDecimal bd = new BigDecimal(bootTime);
        bd = bd.setScale(0, RoundingMode.DOWN);
        lastBootUpTime = DateUtil.offsetSecond(new Date(), -1 * bd.intValue());
        sys.setLastBootUpTime(lastBootUpTime);
        return sys;
    }
}
