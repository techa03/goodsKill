package com.goodskill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.api.service.UserRoleService;
import com.goodskill.entity.UserRole;
import com.goodskill.mp.dao.mapper.UserRoleMapper;
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
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public boolean remove(UserRole entity) {
        return super.remove(new QueryWrapper<>(entity));
    }

    @Override
    public boolean save(UserRole entity) {
        return super.save(entity);
    }

    @Override
    public List<UserRole> list() {
        return super.list();
    }

    @Override
    public List<UserRole> list(Integer id) {
        UserRole query = new UserRole();
        query.setUserId(id);
        return super.list(new QueryWrapper<>(query));
    }
}
