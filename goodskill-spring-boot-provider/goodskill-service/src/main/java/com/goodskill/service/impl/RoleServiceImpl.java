package com.goodskill.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.api.service.RoleService;
import com.goodskill.entity.Role;
import com.goodskill.mp.dao.mapper.RoleMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@DubboService
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {


    @Override
    public IPage<Role> page(Page<Role> page) {
        return super.page(page);
    }

    @Override
    public boolean remove(Role entity) {
        return this.removeById(entity.getRoleId());
    }

    @Override
    public boolean save(Role entity) {
        return super.save(entity);
    }
}
