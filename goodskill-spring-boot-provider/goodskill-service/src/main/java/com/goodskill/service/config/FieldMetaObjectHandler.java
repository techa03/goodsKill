package com.goodskill.service.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatis-plus数据填充处理器
 *
 */
@Component
@Slf4j
public class FieldMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATED_TIME = "createTime";
    private static final String UPDATED_TIME = "updateTime";


    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        this.strictInsertFill(metaObject, CREATED_TIME, Date.class, now);
        this.strictInsertFill(metaObject, UPDATED_TIME, Date.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Date now = new Date();
        this.strictUpdateFill(metaObject, UPDATED_TIME, Date.class, now);
    }
}
