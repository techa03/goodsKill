package org.seckill.service.impl.mock;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.seckill.dao.UserMapper;
import org.seckill.entity.User;
import org.seckill.service.impl.UserAccountServiceImpl;
import org.springframework.util.DigestUtils;

/**
 * Created by heng on 2017/6/28.
 */
public class UserAccountServiceImplTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private UserAccountServiceImpl userAccountService = new UserAccountServiceImpl();
    private UserMapper userMapper;

    @Before
    public void setUp() {
        userMapper = context.mock(UserMapper.class);
        userAccountService.setUserMapper(userMapper);
    }

    @Test
    public void register() throws Exception {
        User user = new User();
        user.setPassword("123");
        context.checking(new Expectations() {{
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            oneOf(userMapper).insert(user);
            will(returnValue(1));
        }});
        userAccountService.register(user);
    }

    @Test
    public void login() throws Exception {
        // TODO exmaple mock失败！
//        User user=new User();
//        user.setAccount("1");
//        user.setPassword("1");
//        context.checking(new Expectations(){{
//            UserExample example = new UserExample();
//            example.createCriteria().andAccountEqualTo(user.getAccount()).andPasswordEqualTo(user.getPassword());
//            oneOf(userMapper).selectByExample(example);
//        }});
//        userAccountService.login(user);
    }

}