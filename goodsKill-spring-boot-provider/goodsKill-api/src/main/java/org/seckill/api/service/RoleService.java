package org.seckill.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.seckill.entity.Role;

/**
 * @author heng
 */
public interface RoleService {
    IPage<Role> page(IPage<Role> page);

    boolean save(Role role);

    boolean remove(Role entity);
}
