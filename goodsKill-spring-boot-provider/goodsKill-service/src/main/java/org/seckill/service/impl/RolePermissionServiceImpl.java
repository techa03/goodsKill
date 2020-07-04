package org.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.service.RolePermissionService;
import org.seckill.entity.RolePermission;
import org.seckill.mp.dao.mapper.RolePermissionMapper;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

    @Override
    public boolean remove(int roleId) {
        RolePermission query = new RolePermission();
        query.setRoleId(roleId);
        return super.remove(new QueryWrapper<>(query));
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
