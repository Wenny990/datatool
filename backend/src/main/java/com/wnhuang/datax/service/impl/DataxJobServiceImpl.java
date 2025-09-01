package com.wnhuang.datax.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wnhuang.common.domain.entity.MonitorServerInfo;
import com.wnhuang.common.exception.BusinessException;
import com.wnhuang.common.service.CommandExecuteService;
import com.wnhuang.common.service.MonitorServerInfoService;
import com.wnhuang.common.service.impl.MonitorServerInfoServiceImpl;
import com.wnhuang.common.utils.DesUtil;
import com.wnhuang.common.utils.RegexUtil;
import com.wnhuang.common.utils.SplitUtil;
import com.wnhuang.datax.domain.entity.DataxJob;
import com.wnhuang.datax.domain.entity.DataxJobRunLog;
import com.wnhuang.datax.domain.enums.JobRunStatusEnum;
import com.wnhuang.datax.mapper.DataxJobMapper;
import com.wnhuang.datax.service.DataxJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【datax_job(datax任务)】的数据库操作Service实现
 * @createDate 2023-06-04 17:00:35
 */
@Service
@Slf4j
public class DataxJobServiceImpl extends ServiceImpl<DataxJobMapper, DataxJob>
        implements DataxJobService {

    @Autowired
    MonitorServerInfoService serverInfoService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    CommandExecuteService commandExecuteService;


    @Autowired
    @Qualifier("dataxExecutorService")
    private ExecutorService dataxExecutorService;

    private final Map<String, DataxJobRunLog> jobRunLogMap = new HashMap<>();
    private final Map<String, StringBuilder> jobRunLogContentMap = new HashMap<>();
    private final static String CACHE_NAME = "datax:jobRunLog:";
    private final static String CACHE_MAP_NAME = "datax:jobRunLog:map";
    private final static String CACHE_CONTENT_NAME = "datax:jobRunLogContent:";
    @Autowired
    private MonitorServerInfoServiceImpl monitorServerInfoServiceImpl;

    /**
     * 上传job到datax服务器
     *
     * @param id dataxJobId
     * @return 上传后的路径
     */
    @Override
    public DataxJob uploadJson(Integer id) {
        DataxJob dataxJob = this.getById(id);
        Assert.notNull(dataxJob, "根据传入的jobId{" + id + "}，未获取到对应的job，请检查！");
        return uploadDataxJob(dataxJob);
    }

    /**
     * 上传job到datax服务器
     *
     * @param dataxJob dataxJob对象
     * @return
     */
    @Override
    public DataxJob uploadDataxJob(DataxJob dataxJob) {
        Integer serverId = dataxJob.getServerId();
        MonitorServerInfo serverInfo = serverInfoService.getById(serverId);
        if (!serverInfo.getIsUploaded()) {
            log.info("检测到服务器未上传datax脚本，开始上传...");
            String resourceFilePath = "/datax-json.py";
            InputStream inputStream = this.getClass().getResourceAsStream(resourceFilePath);
            if (inputStream == null) {
                throw new BusinessException("Resource file not found: " + resourceFilePath);
            }
            commandExecuteService.uploadFile(serverInfo, serverInfo.getOggPath() + "/bin" + resourceFilePath, inputStream);
            log.info("服务器{} 上传datax脚本成功，{}", serverInfo.getServerName(), serverInfo.getOggPath() + "/bin/" + resourceFilePath);
            serverInfo.setIsUploaded(true);
            serverInfoService.updateIsUploaded(serverInfo);
        }
//        String path = serverInfo.getOggPath() + "/myJob/" + dataxJob.getJobName() + ".json";
//        dataxJob.setJsonPath(path);
//        commandExecuteService.uploadFile(serverInfo, path, dataxJob.getJobParams());
        return dataxJob;
    }

    @Override
    public boolean saveOrUpdate(DataxJob entity) {
        entity.setJobParams(DesUtil.decrypt(entity.getJobParams()));
        entity.setSetting(DesUtil.decrypt(entity.getSetting()));
        entity.setReader(DesUtil.decrypt(entity.getReader()));
        entity.setWriter(DesUtil.decrypt(entity.getWriter()));
        entity.setSplitParams(DesUtil.decrypt(entity.getSplitParams()));
        entity.setTransformer(DesUtil.decrypt(entity.getTransformer()));
        return super.saveOrUpdate(entity);
    }

    /**
     * 获取分组下的所有任务
     *
     * @param groupId 分组ID
     * @return 任务列表
     */
    @Override
    public List<DataxJob> listByGroupId(Integer groupId) {
        return this.lambdaQuery().
                eq(DataxJob::getGroupId, groupId).
                select(DataxJob::getId, DataxJob::getJobName).
                list();
    }

    /**
     * 后台执行job任务
     *
     * @param id job主键
     * @return 返回任务执行标识uuid
     */
    @Override
    public String executeDataxJob(Integer id) {
        DataxJob dataxJob = uploadJson(id);
        ArrayList<DataxJob> list = new ArrayList<>();
        list.add(dataxJob);
        String uuid = executeDataxJobList(list, 0);
        return uuid;
    }

    /**
     * 切分执行任务，串行执行，一个任务根据时间或者值进行切分，返回第一个任务的uuid
     *
     * @param id 任务id
     * @return 第一个任务标识
     */
    @Override
    public String executeDataxJobByRange(Integer id) {
        DataxJob dataxJob = this.getById(id);
        Assert.notNull(dataxJob, "根据传入的jobId{" + id + "}，未获取到对应的job，请检查！");
        List<DataxJob> dataxJobs = setSplit(dataxJob);
        return executeDataxJobList(dataxJobs, 0);
    }

    /**
     * 切分执行任务，同步执行，一个任务根据时间或者值进行切分
     *
     * @param id 任务id
     * @return 第一个任务标识
     */
    @Override
    public List<String> executeDataxJobByRangeAsync(Integer id) {
        DataxJob dataxJob = this.getById(id);
        Assert.notNull(dataxJob, "根据传入的jobId{" + id + "}，未获取到对应的job，请检查！");
        List<DataxJob> dataxJobs = setSplit(dataxJob);
        return executeDataxJobList(dataxJobs);
    }


    /**
     * 后台并行执行job任务
     *
     * @param list  job列表
     * @return 返回所有任务执行标识uuid
     */
    private List<String> executeDataxJobList(List<DataxJob> list) {
        Assert.notEmpty(list, "传入的job列表不能为空！");
        List<String> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            DataxJob dataxJob = uploadDataxJob(list.get(i));
            DataxJobRunLog dataxJobRunLog = executeDataxJob(dataxJob, (resultCode) -> {

            });
            dataxJobRunLog.setCurrWhereStr(dataxJob.getCurrWhereStr());
            dataxJobRunLog.setTotal(list.size());
            dataxJobRunLog.setCurrent(i + 1);
            result.add(dataxJobRunLog.getUuid());
        }
        ListUtil.reverse(result);
        return result;
    }

    /**
     * 后台串行执行job任务
     *
     * @param list  job列表
     * @param index 当前任务序号
     * @return 返回任务执行标识uuid
     */
    private String executeDataxJobList(List<DataxJob> list, Integer index) {
        Assert.notEmpty(list, "传入的job列表不能为空！");
        DataxJob dataxJob = uploadDataxJob(list.get(index));

        // 使用数组来持有引用，解决Lambda表达式中的变量引用问题
        DataxJobRunLog[] jobRunLogArray = new DataxJobRunLog[1];

        Consumer<Integer> completeFunc = (resultCode) -> {
            //如果执行成功并且还有剩余任务，则继续执行
            if (resultCode.equals(0)) {
                if (index < list.size() - 1) {
                    jobRunLogArray[0].setHasNext(true);
                    jobRunLogArray[0].setNextUuid(executeDataxJobList(list, index + 1));
                } else {
                    jobRunLogArray[0].setHasNext(false);
                }
            }
        };

        DataxJobRunLog dataxJobRunLog = executeDataxJob(dataxJob, completeFunc);
        dataxJobRunLog.setTotal(list.size());
        dataxJobRunLog.setCurrent(index + 1);
        dataxJobRunLog.setCurrWhereStr(dataxJob.getCurrWhereStr());
        // 将引用保存到数组中，以便在回调中使用
        jobRunLogArray[0] = dataxJobRunLog;
        return dataxJobRunLog.getUuid();
    }

    private DataxJobRunLog executeDataxJob(DataxJob dataxJob, Consumer<Integer> completeFunc) {
        String uuid = UUID.randomUUID().toString();
        dataxJob.setUuid(uuid);
        Integer serverId = dataxJob.getServerId();
        MonitorServerInfo serverInfo = serverInfoService.getById(serverId);
        String shellCommand = buildShellCommand(serverInfo, dataxJob);
        DataxJobRunLog jobRunLog = DataxJobRunLog.builder()
                .uuid(uuid)
                .jobId(dataxJob.getId())
                .jobName(dataxJob.getJobName())
                .jobPath(dataxJob.getJsonPath())
                .shellCommand(shellCommand)
                .serverName(serverInfo.getServerName())
                .serverIp(serverInfo.getServerIp())
                .logPath(dataxJob.getLogPath())
                .serverId(serverId)
                .status(JobRunStatusEnum.NOT_START.getStatus())
                .startTime(new Date())
                .build();

        StringBuilder logContent = new StringBuilder();
        jobRunLogMap.put(uuid, jobRunLog);
        jobRunLogContentMap.put(uuid, logContent);

        dataxExecutorService.execute(() -> {
            commandExecuteService.executeCommand(serverInfo, shellCommand, (lineContent) -> {
                if (StrUtil.isBlank(lineContent)) {
                    return;
                }
                if (jobRunLog.getStatus() < JobRunStatusEnum.RUNNING.getStatus()) {
                    jobRunLog.setStatus(JobRunStatusEnum.RUNNING.getStatus());
                    jobRunLog.setStartTime(new Date());
                }
                logContent.append(lineContent).append("\r\n");
//            jobRunLog.setLogContent(logContent.toString());
//            entries.set("log", log);
                //匹配进度百分比
                String percentageString = RegexUtil.match("Percentage (\\d+\\.\\d+)%", lineContent);
                if (null != percentageString) {
                    Double percentage = Convert.toDouble(percentageString);
                    jobRunLog.setPercentage(percentage);
                }
                //匹配记录数
                String totalRecordsString = RegexUtil.match("Total (\\d+) records", lineContent);
                if (null != totalRecordsString) {
                    Long totalRecords = Convert.toLong(totalRecordsString);
                    jobRunLog.setTotalRecords(totalRecords);
                }
                //匹配速度
                String speedString = RegexUtil.match("(\\d+) records/s", lineContent);
                if (null != speedString) {
                    Long speed = Convert.toLong(speedString);
                    jobRunLog.setSpeed(speed);
                }

            }, (result) -> {
                //result 0表示成功，其它都是失败
                jobRunLog.setStatus(result == 0 ? JobRunStatusEnum.SUCCESS.getStatus() : JobRunStatusEnum.FAIL.getStatus());
                jobRunLog.setResult(result);
                jobRunLog.setEndTime(new Date());


                completeFunc.accept(result);
                redisTemplate.opsForHash().put(CACHE_MAP_NAME, uuid, JSONUtil.toJsonStr(jobRunLog));
                redisTemplate.opsForValue().set(CACHE_CONTENT_NAME + uuid, logContent.toString());
                jobRunLogMap.remove(uuid);
                jobRunLogContentMap.remove(uuid);

            });
        });
        return jobRunLog;
    }

    /**
     * 设置分段执行
     *
     * @param dataxJob 任务对象
     */
    private List<DataxJob> setSplit(DataxJob dataxJob) {
        if (!JSONUtil.isTypeJSON(dataxJob.getSplitParams())) {
            throw new BusinessException("splitParams 参数格式错误");
        }
        List<DataxJob> result = new ArrayList<>();
        JSONObject splitParams = JSONUtil.parseObj(dataxJob.getSplitParams());
        String type = splitParams.getStr("type");
        String replaceStr = splitParams.getStr("replaceStr");

        List<List<String>> lists = null;
        switch (type) {
            case "range":
                Long start = splitParams.getLong("startValue");
                Long end = splitParams.getLong("endValue");
                Long step = splitParams.getLong("step");
                lists = SplitUtil.splitRange(start, end, step);
                break;
            case "date":
                Date startDate = splitParams.getDate("startDate");
                Date endDate = splitParams.getDate("endDate");
                Integer stepMonth = splitParams.getInt("step");
                lists = SplitUtil.splitDate(startDate, endDate, stepMonth);
                break;
            default:
                throw new BusinessException("不支持的分段类型");
        }

        for (List<String> list : lists) {
            result.add(copyAndSetJobReaderWhere(dataxJob, replaceStr, list));
        }
        return result;
    }


    /**
     * 复制并设置where条件
     *
     * @param dataxJob   原始job
     * @param replaceStr 替换的字符串
     * @param params     替换的参数
     * @return 返回一个新的job任务对象
     */
    private DataxJob copyAndSetJobReaderWhere(DataxJob dataxJob, String replaceStr, List<String> params) {
        DataxJob curr = new DataxJob();
        BeanUtils.copyProperties(dataxJob, curr);
        JSONObject obj = JSONUtil.parseObj(curr.getJobParams());
        JSONObject parameter = obj.getJSONObject("job").
                getJSONArray("content").
                getJSONObject(0).
                getJSONObject("reader").
                getJSONObject("parameter");
        String where;
        if (parameter.containsKey("where") && null != parameter.getStr("where")) {
            where = parameter.getStr("where");
        } else {
            where = "";
        }
        if (StrUtil.isNotBlank(where)) {
            where += " and ";
        }
        replaceStr = replaceStr.replace("#{0}", params.get(0));
        replaceStr = replaceStr.replace("#{1}", params.get(1));
        where = where + replaceStr;
        parameter.set("where", where);
        curr.setJobParams(JSONUtil.toJsonStr(obj));
        curr.setCurrWhereStr(where);
        return curr;
    }

    /**
     * 结束任务
     *
     * @param uuid 任务标识
     * @return 结束成功标识
     */
    @Override
    public Boolean killDataxJob(String uuid) {
        DataxJobRunLog execLog = getJobRunLog(uuid);
        String jobPath = execLog.getJobPath();
        Integer serverId = execLog.getServerId();
        String command = "pkill -f -9  \"" + execLog.getUuid() + "\"";
        commandExecuteService.executeCommand(monitorServerInfoServiceImpl.getById(serverId), command);
        return true;
    }

    /**
     * 获取单个任务执行情况
     *
     * @param uuid 任务标识
     * @return
     */
    @Override
    public DataxJobRunLog getJobRunLog(String uuid) {
        DataxJobRunLog jobRunLog;
        if (jobRunLogMap.containsKey(uuid)) {
            jobRunLog = jobRunLogMap.get(uuid);
            return jobRunLog;
        }
        String s = (String) redisTemplate.opsForHash().get(CACHE_MAP_NAME, uuid);
        jobRunLog = JSONUtil.toBean(s, DataxJobRunLog.class);
        return jobRunLog;
    }

    /**
     * 获取任务执行日志内容情况
     *
     * @param uuid 任务标识
     * @return
     */
    @Override
    public DataxJobRunLog getJobRunLogContent(String uuid) {
        DataxJobRunLog jobRunLog;
        if (jobRunLogMap.containsKey(uuid)) {
            jobRunLog = jobRunLogMap.get(uuid);
            jobRunLog.setLogContent(jobRunLogContentMap.get(uuid).toString());
            return jobRunLog;
        }
        String s = (String) redisTemplate.opsForHash().get(CACHE_MAP_NAME, uuid);
        jobRunLog = JSONUtil.toBean(s, DataxJobRunLog.class);
        if (jobRunLog != null) {
            String content = (String) redisTemplate.opsForValue().get(CACHE_CONTENT_NAME + uuid);
            jobRunLog.setLogContent(content);
        }
        return jobRunLog;
    }

    @Override
    public List<DataxJobRunLog> getAllDataxJobRunLog() {
        List<DataxJobRunLog> result = new ArrayList<>();

        // 先添加正在运行的任务（内存中的数据）
        result.addAll(jobRunLogMap.values());

        // 再添加已完成的任务（Redis中的数据）
        List<Object> values = redisTemplate.opsForHash().values(CACHE_MAP_NAME);
        List<DataxJobRunLog> redisLogs = values.stream()
                .map(t -> JSONUtil.toBean((String) t, DataxJobRunLog.class))
                .sorted(Comparator.comparing(DataxJobRunLog::getStartTime).reversed())
                .collect(Collectors.toList());

        result.addAll(redisLogs);


        // 对所有任务按开始时间，精确到秒倒序排序，相同时间的再按照current降序
        result.sort(
                Comparator.comparing((DataxJobRunLog log) -> {
                            // 秒
                            return DateUtil.format(log.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                        })
                        .thenComparingInt(DataxJobRunLog::getCurrent  ).reversed()
        );
        return result;
    }

    @Override
    public Long removeExecKeys(List<String> keys) {
        List<String> contentCollect = keys.stream().map(t -> {
            return CACHE_NAME + t;
        }).collect(Collectors.toList());
        redisTemplate.delete(contentCollect);
        return redisTemplate.opsForHash().delete(CACHE_MAP_NAME, keys.stream().toArray());
    }

    /**
     * 构建datax执行命令
     *
     * @param serverInfo datax服务器
     * @param dataxJob   dataxJob对象
     * @return 返回命令语句
     */
    @Override
    public String buildShellCommand(MonitorServerInfo serverInfo, DataxJob dataxJob) {
        StringBuilder sb = new StringBuilder();
        sb.append("python ");
        sb.append(serverInfo.getOggPath());
        sb.append("/bin/datax-json.py");
        String uuid = dataxJob.getUuid();
        //指定日志文件名称
        sb.append(" --uid ").append(uuid);
        dataxJob.setLogPath(serverInfo.getOggPath() + "/log/" + DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN) + "/" + uuid + ".log");
        //设置jvm参数
        if (StrUtil.isNotBlank(dataxJob.getJvmParams())) {
            sb.append(" --jvm=\"");
            sb.append(dataxJob.getJvmParams());
            sb.append("\"");
        }

        sb.append(" --json ").append("'")
                .append(
                        dataxJob.getJobParams()
                )
                .append("'");
        return sb.toString();
    }
}




