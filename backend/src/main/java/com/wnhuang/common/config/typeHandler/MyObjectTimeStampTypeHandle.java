package com.wnhuang.common.config.typeHandler;

import oracle.sql.DATE;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
/**
 * @author wnhuang
 * @Package com.base.config.typeHandler
 * @date 2022/3/21 20:04
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(value = {JdbcType.TIMESTAMP})
@Component
public class MyObjectTimeStampTypeHandle extends BaseTypeHandler<Object> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object result = rs.getObject(columnName);
        return rs.wasNull() ? null : dealResult(result);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object result = rs.getObject(columnIndex);
        return rs.wasNull() ? null : dealResult(result);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object result = cs.getObject(columnIndex);
        return cs.wasNull() ? null : dealResult(result);
    }

    /**
     * 为了解决错误：
     * 26-Sep-2018 14:21:06.634 WARNING [http-apr-8080-exec-6] org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver.handleHttpMessageNotWritable Failed to write HTTP message: org.springframework.http.converter.HttpMessageNotWritableException:
     * Could not write JSON: No serializer found for class java.io.ByteArrayInputStream and no properties discovered to create BeanSerializer
     * (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS);
     * nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class java.io.ByteArrayInputStream and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)
     * (through reference chain: java.util.HashMap["pageData"]->java.util.ArrayList[0]->java.util.HashMap["UPDATE_TIME"]->oracle.sql.TIMESTAMP["stream"])
     * @param result
     * @return
     * @throws SQLException
     */
    private Object dealResult(Object result) throws SQLException {
        if (result instanceof TIMESTAMP) {
            return new Date(((TIMESTAMP) result).dateValue().getTime());
        } else if (result instanceof DATE) {
            return new Date(((DATE) result).dateValue().getTime());
        } else if (result instanceof TIMESTAMPLTZ) {
            return new Date(((TIMESTAMPLTZ) result).dateValue().getTime());
        } else if (result instanceof TIMESTAMPTZ) {
            return new Date(((TIMESTAMPTZ) result).dateValue().getTime());
        } else{
            return result;
        }

    }
}
