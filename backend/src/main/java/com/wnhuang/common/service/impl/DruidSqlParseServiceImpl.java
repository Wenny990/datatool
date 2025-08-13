package com.wnhuang.common.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.wnhuang.common.domain.entity.RepositorySource;
import com.wnhuang.common.domain.entity.SelectSqlObject;
import com.wnhuang.common.domain.entity.sql.JoinInfo;
import com.wnhuang.common.domain.entity.sql.SelectItem;
import com.wnhuang.common.domain.request.SqlParseBaseRequest;
import com.wnhuang.common.service.RepositorySourceService;
import com.wnhuang.common.service.SqlParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wnhuang
 * @date 2025/8/9 22:34
 */
@Service
@Slf4j
public class DruidSqlParseServiceImpl implements SqlParseService {

    @Autowired
    RepositorySourceService repositorySourceService;

    @Override
    public SelectSqlObject parseSelectSql(SqlParseBaseRequest sqlParseBaseRequest) {
        SelectSqlObject selectSqlObject = new SelectSqlObject();
        RepositorySource repositorySource = repositorySourceService.getById(sqlParseBaseRequest.getRepoId());
        Assert.notNull(repositorySource, "数据源不存在");
        DbType dbType = getDbType(repositorySource.getDataSourceName());
        Assert.notNull(dbType, "不支持的数据源类型：" + repositorySource.getDataSourceName());
        selectSqlObject.setDbType(dbType.name());
        selectSqlObject.setSql(sqlParseBaseRequest.getSql());
        try {
            SQLStatementParser parser = new SQLStatementParser(sqlParseBaseRequest.getSql(), dbType);
            SQLStatement statement = parser.parseStatement();
            if (!(statement instanceof SQLSelectStatement)) {
                throw new RuntimeException("解析SQL发生错误：传入不是查询语句！");
            }
            SQLSelectStatement selectStatement = (SQLSelectStatement) statement;
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) selectStatement.getSelect().getQuery();

            selectSqlObject.setSelectList(parseSelectItem(queryBlock));

            selectSqlObject.setJoinList(parseFromJoins(queryBlock));

            selectSqlObject.setWhere(queryBlock.getWhere() != null ? SQLUtils.toSQLString(queryBlock.getWhere()) : "");

            return selectSqlObject;
        } catch (Exception e) {
            log.error("解析SQL发生错误：{}", e.getMessage());
            throw new RuntimeException("解析SQL发生错误：" + e.getMessage());
        }

    }

    private List<SelectItem> parseSelectItem(SQLSelectQueryBlock queryBlock) {
        //解析select部分
        List<SQLSelectItem> selectList = queryBlock.getSelectList();
        return selectList.stream().map(sqlSelectItem -> {
            SelectItem selectItem = new SelectItem();
            selectItem.setStr(SQLUtils.toSQLString(sqlSelectItem));
            selectItem.setExpression(SQLUtils.toSQLString(sqlSelectItem.getExpr()));
            selectItem.setAlias(sqlSelectItem.getAlias());
            return selectItem;
        }).collect(Collectors.toList());
    }

    /**
     * 获取所有FROM 和 JOIN表的信息
     *
     * @param queryBlock 查询块
     * @return List<JoinInfo>
     */
    private List<JoinInfo> parseFromJoins(SQLSelectQueryBlock queryBlock) {
        List<JoinInfo> joinList = new ArrayList<>();
        SQLTableSource tableSource = queryBlock.getFrom();
        deepParseJoins(tableSource, joinList);
        ListUtil.reverse(joinList);
        return joinList;
    }

    public DbType getDbType(String dbTypeName) {
        DbType dbType = DbType.of(dbTypeName.toLowerCase());
        if (dbType == null)
            dbType = DbType.of("postgresql");
        return dbType;
    }


    // 递归处理JOIN信息
    private void deepParseJoins(SQLTableSource tableSource, List<JoinInfo> joinList) {
        JoinInfo joinInfo = new JoinInfo();
        SQLTableSource exprTableSource;
        SQLJoinTableSource joinTableSource = null;
        if (tableSource instanceof SQLJoinTableSource) {
            joinTableSource = (SQLJoinTableSource) tableSource;
            exprTableSource = joinTableSource.getRight();
            joinInfo.setJoinType(joinTableSource.getJoinType().name);
            joinInfo.setJoinCondition(SQLUtils.toSQLString(joinTableSource.getCondition()));
        } else {
            exprTableSource = tableSource;
            joinInfo.setJoinType("FROM");
        }
        joinInfo.setTableAlias(exprTableSource.getAlias());

        if (exprTableSource instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) exprTableSource).getExpr();
            if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                joinInfo.setOwner(propertyExpr.getOwnerName());
                joinInfo.setTableName(propertyExpr.getName());
            } else if (expr instanceof SQLIdentifierExpr) {
                joinInfo.setTableName(((SQLIdentifierExpr) expr).getName());
            } else {
                joinInfo.setTableName(expr.toString());
            }
        } else if (exprTableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) exprTableSource;
            joinInfo.setTableAlias(subqueryTableSource.getAlias());
            joinInfo.setTableName(" ( " + subqueryTableSource.getSelect().toString() + " ) ");
        }

        joinList.add(joinInfo);
        if (!"FROM".equals(joinInfo.getJoinType()) && joinTableSource != null) {
            // 递归处理左表和右表
            deepParseJoins(joinTableSource.getLeft(), joinList);
        }
    }
}
