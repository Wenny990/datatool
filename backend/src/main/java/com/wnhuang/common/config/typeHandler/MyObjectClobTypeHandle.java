package com.wnhuang.common.config.typeHandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.sql.*;
/**
 * @author wnhuang
 * @Package com.base.config.typeHandler
 * @date 2022/3/21 19:59
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(value = {JdbcType.CLOB})
@Component
public class MyObjectClobTypeHandle extends BaseTypeHandler<Object>{
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        String parameterStr = (String) parameter;
        StringReader reader = new StringReader(parameterStr);
        ps.setCharacterStream(i, reader, parameterStr.length());
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = "";
        Clob clob = rs.getClob(columnName);
        if(clob != null) {
            int size = (int)clob.length();
            value = clob.getSubString(1L, size);
        }
        return value;
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = "";
        Clob clob = rs.getClob(columnIndex);
        if(clob != null) {
            int size = (int)clob.length();
            value = clob.getSubString(1L, size);
        }
        return value;
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = "";
        Clob clob = cs.getClob(columnIndex);
        if(clob != null) {
            int size = (int)clob.length();
            value = clob.getSubString(1L, size);
        }
        return value;
    }
}
