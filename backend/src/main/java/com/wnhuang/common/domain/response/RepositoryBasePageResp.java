package com.wnhuang.common.domain.response;

import lombok.Data;

/**
 * 数据库查询对象
 * @author wnhuang
 * @date 2024/3/28 16:15
 */
@Data
public class RepositoryBasePageResp {

    /**
     * 数据源id
     */
    private Integer repoId;

    /**
     * sql脚本
     */
    private String sqlText;

    /**
     * 单页数据大小
     */
    private Integer pageSize;

    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 查询全部数据量
     */
    private Boolean searchTotal = true;

    /**
     * 是否分页
     */
    private Boolean isPage = true;

}
