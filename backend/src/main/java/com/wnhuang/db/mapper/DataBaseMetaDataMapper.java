package com.wnhuang.db.mapper;

import com.wnhuang.db.domain.entity.SchemaInfo;
import com.wnhuang.db.domain.entity.TableColumnInfo;
import com.wnhuang.db.domain.entity.TableIndex;
import com.wnhuang.db.domain.entity.TableInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author wnhuang
 * @date 2024/11/17 16:20
 */
@Mapper
public interface DataBaseMetaDataMapper {

    @Select("${querySql}")
    List<SchemaInfo> getSchemaList(String querySql);

    @Select("${querySql}")
    List<TableInfo> getTableList(String querySql);

    @Select("${querySql}")
    List<TableColumnInfo> getTableColumnList(String querySql);

    @Select("${querySql}")
    List<TableIndex> getTableIndexList(String querySql);
}
