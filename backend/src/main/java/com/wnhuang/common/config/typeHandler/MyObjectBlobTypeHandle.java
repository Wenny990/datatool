package com.wnhuang.common.config.typeHandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.sql.*;

/**
 * @author wnhuang
 * @Package com.base.config
 * @date 2022/3/21 19:39
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(value = {JdbcType.BLOB})
@Component
public class MyObjectBlobTypeHandle extends BaseTypeHandler<Object> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        byte[] parameterByteArray = (byte[]) parameter;
        String parameterStr = new String(parameterByteArray);
        StringReader reader = new StringReader(parameterStr);
        ps.setCharacterStream(i, reader, parameterStr.length());
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Blob blob = rs.getBlob(columnName);
        byte[] returnValue = null;
        if (null != blob) {
            returnValue = blob.getBytes(1L, (int) blob.length());
        }
        return returnValue == null ? "" : Base64.encodeBase64String(returnValue);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Blob blob = rs.getBlob(columnIndex);
        byte[] returnValue = null;
        if (null != blob) {
            returnValue = blob.getBytes(1L, (int) blob.length());
        }
        return returnValue == null ? "" : new String(returnValue);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Blob blob = cs.getBlob(columnIndex);
        byte[] returnValue = null;
        if (null != blob) {
            returnValue = blob.getBytes(1L, (int) blob.length());
        }
        return returnValue == null ? "" : new String(returnValue);
    }
}
