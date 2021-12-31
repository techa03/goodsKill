package com.goodskill.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.entity.Role;

/**
 * @author heng
 */
public interface RoleService {
    IPage<Role> page(Page<Role> page);

    boolean save(Role role);

    boolean remove(Role entity);
}
