package org.seckill.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.service.PermissionService;
import org.seckill.entity.Permission;
import org.seckill.mp.dao.mapper.PermissionMapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public IPage<Permission> page(IPage<Permission> page) {
        return super.page(page);
    }

    @Override
    public boolean removeById(int permissionId) {
        return super.removeById(permissionId);
    }

}
