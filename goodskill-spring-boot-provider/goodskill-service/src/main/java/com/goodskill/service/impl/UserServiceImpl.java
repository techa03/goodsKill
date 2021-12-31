package com.goodskill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.entity.User;
import com.goodskill.mp.dao.mapper.UserMapper;
import com.goodskill.service.common.UserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
