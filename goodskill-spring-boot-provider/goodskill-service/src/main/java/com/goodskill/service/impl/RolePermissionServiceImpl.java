package com.goodskill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.api.service.RolePermissionService;
import com.goodskill.entity.RolePermission;
import com.goodskill.mp.dao.mapper.RolePermissionMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@DubboService
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService<RolePermission> {

    @Override
    public boolean remove(int id) {
        RolePermission query = new RolePermission();
        query.setRoleId(id);
        return super.remove(new QueryWrapper<>(query));
    }

    @Override
    public boolean save(RolePermission entity) {
        return super.save(entity);
    }

    @Override
    public List<RolePermission> list() {
        return super.list();
    }

    @Override
    public List<RolePermission> list(Integer roleId) {
        RolePermission query = new RolePermission();
        query.setRoleId(roleId);
        return super.list(new QueryWrapper<>(query));
    }
}
