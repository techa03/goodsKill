package com.goodskill.common.core.orm.mybatis.config;

import cn.dev33.satoken.stp.StpUtil;
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

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "createUser", () -> StpUtil.getLoginId(""), String.class);
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateUser", () -> StpUtil.getLoginId(""), String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updateUser", () -> StpUtil.getLoginId(""), String.class);
    }
}
