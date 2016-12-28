package org.seckill.dao;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-dao.xml" })
public class UserDaoTest {
	@Autowired
	private UserDao userDao;

	@Test
	public void testAddUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectUserByAccountAndPsw() {
		User user = new User();
		user.setAccount("techa04");
		user.setPassword("daa857d46518b530c9800e491a928d3f");
		Assert.assertEquals(userDao.selectUserByAccountAndPsw(user), 1);
	}

}
