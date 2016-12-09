/**
 * 
 */
package org.seckill.dao;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author heng
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
	@Autowired
	private SuccessKilledDao successKilledDao;
	/**
	 * Test method for {@link org.seckill.dao.SuccessKilledDao#insertSuccessKilled(long, long)}.
	 */
	@Test
	public void testInsertSuccessKilled() {
		successKilledDao.insertSuccessKilled(1L, 1373483423L);
	}

	/**
	 * Test method for {@link org.seckill.dao.SuccessKilledDao#queryByIdWithSeckill(long)}.
	 */
	@Test
	public void testQueryByIdWithSeckill() {
		fail("Not yet implemented");
	}

}
