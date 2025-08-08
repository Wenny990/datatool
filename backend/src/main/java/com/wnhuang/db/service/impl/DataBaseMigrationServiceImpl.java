package com.wnhuang.db.service.impl;

import cn.hutool.core.convert.Convert;
import com.wnhuang.common.aviator.AviatorUtils;
import com.wnhuang.common.domain.entity.RepositoryConfig;
import com.wnhuang.common.domain.entity.RepositoryMapping;
import com.wnhuang.common.service.RepositoryMappingService;
import com.wnhuang.common.service.RepositorySourceService;
import com.wnhuang.db.domain.entity.ConversionConfig;
import com.wnhuang.db.domain.entity.TableColumnInfo;
import com.wnhuang.db.domain.entity.TableIndex;
import com.wnhuang.db.domain.entity.TableInfo;
import com.wnhuang.db.service.DataBaseMigrationService;

import cn.hutool.core.util.StrUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wnhuang
 * @date 2025/6/16 00:42
 */
@Service
@Slf4j
public class DataBaseMigrationServiceImpl implements DataBaseMigrationService {

    @Autowired
    private RepositoryMappingService repositoryMappingService;

    @Autowired
    private RepositorySourceService repositorySourceService;

    // 获取迁移SQL
    @Override
    public String getMigrationSql(TableInfo tableInfo, List<TableColumnInfo> columnInfoList, TableIndex tableIndex, ConversionConfig conversionConfig) {
        List<RepositoryConfig> repositoryConfigList = repositorySourceService.getRepositoryConfigList();
        Optional<RepositoryConfig> first = repositoryConfigList.stream().filter(t -> t.getId().equals(conversionConfig.getTargetDbType())).findFirst();
        String compileSql = "";
        if (first.isPresent()) {
            Map<String, Object> map = new HashMap<>();
            map.put("columns", columnInfoList);
            map.put("index", tableIndex);
            compileSql = AviatorUtils.compileSql(first.get().getDdlTemplate(), map);
        }
        return compileSql;
    }

    @Override
    public List<TableColumnInfo> getConversionColumn(List<TableColumnInfo> columnInfoList, ConversionConfig conversionConfig) {
        // 获取所有字段类型映射关系
        List<RepositoryMapping> mappings = repositoryMappingService.list();
        mappings = mappings.stream()
                .filter(t -> t.getSourceType().equals(conversionConfig.getSourceDbType()) && t.getTargetType().equals(conversionConfig.getTargetDbType()))
                .collect(Collectors.toList());
        List<TableColumnInfo> list = new ArrayList<>();
        for (TableColumnInfo columnInfo : columnInfoList) {
            list.add(mapColumnType(columnInfo, conversionConfig, mappings));
        }
        return list;
    }

    private TableColumnInfo mapColumnType(TableColumnInfo columnInfo, ConversionConfig conversionConfig, List<RepositoryMapping> mappings) {
        RepositoryMapping mapping = findMapping(columnInfo, mappings);
        // 创建新的TableColumnInfo对象，避免修改原对象
        TableColumnInfo newColumnInfo = new TableColumnInfo();
        newColumnInfo.setColumnName(columnInfo.getColumnName());
        newColumnInfo.setDataType(mapping.getTargetType());
        if (StrUtil.isNotBlank(mapping.getPrecisionMapping())) {
            if ("SOURCE".equals(mapping.getLengthMapping().toUpperCase())) {
                newColumnInfo.setDataLength(columnInfo.getDataLength());
            } else {
                newColumnInfo.setDataLength(mapping.getLengthMapping());
            }
        }
        if (StrUtil.isNotBlank(mapping.getPrecisionMapping())) {
            if ("SOURCE".equals(mapping.getPrecisionMapping().toUpperCase())) {
                newColumnInfo.setDataPrecision(columnInfo.getDataPrecision());
            } else {
                newColumnInfo.setDataPrecision(Convert.toInt(mapping.getPrecisionMapping()));
            }
        }
        if (StrUtil.isNotBlank(mapping.getScaleMapping())) {
            if ("SOURCE".equals(mapping.getScaleMapping().toUpperCase())) {
                newColumnInfo.setDataScale(columnInfo.getDataScale());
            } else {
                newColumnInfo.setDataScale(Convert.toInt(mapping.getScaleMapping()));
            }
        }

        newColumnInfo.setNullable(columnInfo.getNullable());

        newColumnInfo.setRemarks(columnInfo.getRemarks());

        return newColumnInfo;
    }

    private RepositoryMapping findMapping(TableColumnInfo columnInfo, List<RepositoryMapping> mappings) {
        // 按优先级排序
        mappings.sort(Comparator.comparing(RepositoryMapping::getPriorityNo));

        // 查找第一个满足条件的映射规则
        for (RepositoryMapping mapping : mappings) {
            if (mapping.getSourceType().equals(columnInfo.getDataType())) {
                // 如果没有条件表达式，作为默认规则
                if (mapping.getConditionExpr() == null) {
                    return mapping;
                }

                // 评估条件表达式
                if (evaluateCondition(mapping.getConditionExpr(), columnInfo)) {
                    return mapping;
                }
            }
        }

        log.warn("没有找到匹配的字段类型映射规则，数据库类型: {}, 字段类型: {}", columnInfo.getColumnName(), columnInfo.getDataType());
        // 如果没有找到匹配的规则，返回原类型
        return null;
    }

    private boolean evaluateCondition(String conditionExpr, TableColumnInfo columnInfo) {
        if (conditionExpr == null || conditionExpr.trim().isEmpty()) {
            return true; // 没有条件时默认匹配
        }

        // 构建条件参数
        Map<String, Object> params = new HashMap<>();
        params.put("precision", columnInfo.getDataPrecision());
        params.put("scale", columnInfo.getDataScale());
        params.put("length", columnInfo.getDataLength());
        params.put("nullable", columnInfo.getNullable());
        params.put("isPrimaryKey", columnInfo.getIsPrimaryKey());

        // 使用 Aviator 执行条件表达式
        try {
            Object result = AviatorUtils.getInstance().execute(conditionExpr, params);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("条件表达式执行失败: {}", conditionExpr, e);
            return false;
        }
    }
}
