package com.wnhuang.common.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 缓存操作处理
 *
 * @author
 */
@Service
public class CacheService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Map<String, Object> getInfo() {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::info);
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::dbSize);

        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        commandStats.stringPropertyNames().forEach(key -> {
            Map<String, String> data = new HashMap<>(2);
            String property = commandStats.getProperty(key);
            data.put("name", StringUtils.removeStart(key, "cmdstat_"));
            data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
            pieList.add(data);
        });
        result.put("commandStats", pieList);
        return result;
    }


    /**
     * 获取所有缓存名称
     *
     * @return 缓存列表
     */
    public Set<String> getCacheNames(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        return keys;
    }

    /**
     * 根据缓存键内容值
     *
     * @param key 缓存key
     * @return 键值
     */
    public Map<String,Object> getValue(String key) {
        DataType dataType = redisTemplate.type(key);
        Map<String,Object> map = new HashMap<>();
        if (dataType != null) {
            map.put("type", dataType.code());
            map.put("expire",redisTemplate.getExpire(key));
            switch (dataType) {
                case STRING:
                    map.put("value", redisTemplate.opsForValue().get(key));
                    break;
                case HASH:
                    map.put("value", redisTemplate.opsForHash().entries(key));
                    break;
                case LIST:
                    map.put("value", redisTemplate.opsForList().range(key, 0, -1));
                    break;
                case SET:
                    map.put("value", redisTemplate.opsForSet().members(key));
                    break;
                case ZSET:
                    map.put("value", redisTemplate.opsForZSet().rangeWithScores(key, 0, -1));
                    break;
                default:
                    return null; // Unknown data type or key not found
            }
        }
        return map;
    }



    /**
     * 根据名称删除缓存信息
     */
    public Long clearCache(String pattern) {
        Set<String> cacheNames = redisTemplate.keys(pattern);
        for (String cacheName : cacheNames) {
            redisTemplate.delete(cacheName);
        }
        return Long.valueOf(cacheNames.size());
    }


}
