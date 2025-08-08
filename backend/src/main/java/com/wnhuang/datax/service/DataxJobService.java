package com.wnhuang.datax.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.datax.domain.entity.DataxJob;
import com.wnhuang.datax.domain.entity.DataxJobRunLog;

import java.util.List;

/**
* @author Administrator
* @description 针对表【datax_job(datax任务)】的数据库操作Service
* @createDate 2023-06-04 17:00:35
*/
public interface DataxJobService extends IService<DataxJob> {

    /**
     * 上传job到datax服务器
     * @param id dataxJobId
     * @return 上传后的任务路径
     */
    DataxJob uploadJson(Integer id);

    DataxJob uploadDataxJob(DataxJob dataxJob);

    /**
     * 获取分组下的所有任务
     * @param groupId 分组ID
     * @return 任务列表
     */
    List<DataxJob> listByGroupId(Integer groupId);

    /**
     * 后台执行job任务
     * @param id job主键
     * @return 返回任务标识
     */
    String executeDataxJob(Integer id);


    String executeDataxJobByRange(Integer id);

    /**
     * 结束任务
     * @param uuid 任务标识
     * @return
     */
    Boolean killDataxJob(String uuid);

    /**
     * 获取单个任务执行情况
     * @param uuid 任务标识
     * @return
     */
    DataxJobRunLog getJobRunLog(String uuid);

    /**
     * 获取单个任务执行日志
     * @param uuid
     * @return
     */
    DataxJobRunLog getJobRunLogContent(String uuid);

    /**
     * 获取全部任务运行日志
     * @return
     */
    List<DataxJobRunLog> getAllDataxJobRunLog();

    /**
     * 删除指定的任务日志
     * @param keys 任务标识项
     * @return
     */
    Long removeExecKeys(List<String> keys);

    String buildShellCommand(MonitorServerInfo serverInfo, DataxJob dataxJob);
}
