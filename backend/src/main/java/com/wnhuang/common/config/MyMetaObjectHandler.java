package com.wnhuang.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import com.wnhuang.common.utils.UserContentUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author wnhuang
 * @Package com.base.config
 * @date 2022/3/20 12:13
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date::new, Date.class); // 起始版本 3.3.3(推荐)
        if(null != UserContentUtils.getUser()){
            this.strictInsertFill(metaObject,"createUser", Integer.class,UserContentUtils.getUser().getId());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date::new, Date.class); // 起始版本 3.3.3(推荐)
        if(null != UserContentUtils.getUser()){
            this.strictInsertFill(metaObject,"createUser", Integer.class,UserContentUtils.getUser().getId());
        }
    }

    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        if (metaObject.getValue(fieldName) == null || fieldName.equals("updateTime")) {
            Object obj = fieldVal.get();
            if (Objects.nonNull(obj)) {
                metaObject.setValue(fieldName, obj);
            }
        }
        return this;
    }
}
