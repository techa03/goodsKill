package com.goodskill.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.auth.entity.UserRole;
import com.goodskill.auth.mapper.UserRoleMapper;
import com.goodskill.auth.service.UserRoleService;
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
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
