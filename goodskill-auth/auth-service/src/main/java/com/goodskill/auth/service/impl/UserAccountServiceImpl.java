package com.goodskill.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.auth.entity.User;
import com.goodskill.auth.mapper.UserMapper;
import com.goodskill.auth.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author heng
 */
@Service
@Slf4j
public class UserAccountServiceImpl extends ServiceImpl<UserMapper, User> implements UserAccountService {


}
