package com.goodskill.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.api.service.PermissionService;
import com.goodskill.entity.Permission;
import com.goodskill.mp.dao.mapper.PermissionMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.io.Serializable;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@DubboService
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public IPage<Permission> page(Page<Permission> page) {
        return super.page(page);
    }

    @Override
    public boolean removeById(int permissionId) {
        return super.removeById(permissionId);
    }

    @Override
    public boolean save(Permission entity) {
        return super.save(entity);
    }

    @Override
    public Permission getById(Serializable id) {
        return super.getById(id);
    }
}
