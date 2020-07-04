package org.seckill.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.service.RoleService;
import org.seckill.entity.Role;
import org.seckill.mp.dao.mapper.RoleMapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {


    @Override
    public IPage<Role> page(IPage<Role> page) {
        return super.page(page);
    }

    @Override
    public boolean remove(Role entity) {
        return this.removeById(entity.getRoleId());
    }
}
