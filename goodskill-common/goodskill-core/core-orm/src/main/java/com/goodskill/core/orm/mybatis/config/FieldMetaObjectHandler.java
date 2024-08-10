package com.goodskill.core.orm.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.goodskill.core.util.UserInfoUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatis-plus数据填充处理器
 *
 */
@Component
public class FieldMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "createUser", UserInfoUtil::getUserId, String.class);
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateUser", UserInfoUtil::getUserId, String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updateUser", UserInfoUtil::getUserId, String.class);
    }
}
