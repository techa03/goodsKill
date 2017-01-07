package org.seckill.dao;

import static org.junit.Assert.fail;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class GoodsDaoTest {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private GoodsDao goodsDao;

	@Transactional
	@Test
	public void testUpdatePhotoUrl() {
		goodsDao.updatePhotoUrl(1L, "1");
		logger.info(goodsDao.selectById(1L).toString());
	}
	
	@Test
	public void testSelectById() {
		logger.info(goodsDao.selectById(1L).toString());
	}

}
