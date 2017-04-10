/**
 * 
 */
package org.seckill.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author heng
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
@Transactional
public class SuccessKilledDaoTest {
	@Autowired
	private SuccessKilledDao successKilledDao;
	/**
	 * Test method for {@link org.seckill.dao.SuccessKilledDao#insertSuccessKilled(long, long)}.
	 */
	@Test
	public void testInsertSuccessKilled() {
		Assert.assertEquals(1,successKilledDao.insertSuccessKilled(1000L, 1373483412L));
	}

	/**
	 * Test method for {@link org.seckill.dao.SuccessKilledDao#queryByIdWithSeckill(long)}.
	 */
	@Test
	public void testQueryByIdWithSeckill() {
		successKilledDao.insertSuccessKilled(1000L, 1373483414L);
		Assert.assertEquals(1373483414L,successKilledDao.queryByIdWithSeckill(1000,1373483414L).getUserPhone());
	}

}
