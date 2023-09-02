package com.goodskill.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.auth.entity.Permission;
import com.goodskill.auth.mapper.PermissionMapper;
import com.goodskill.auth.service.PermissionService;
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
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}
