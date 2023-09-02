package com.goodskill.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.auth.entity.Role;
import com.goodskill.auth.mapper.RoleMapper;
import com.goodskill.auth.service.RoleService;
import org.springframework.stereotype.Service;

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
}
