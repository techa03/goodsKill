package org.seckill.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Goods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
@Transactional
public class GoodsDaoTest {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private GoodsDao goodsDao;


	@Test
	public void testUpdatePhotoUrl() {
		goodsDao.updatePhotoUrl(1L, "1");
		Assert.assertNotNull(goodsDao.selectById(1L).toString());
	}
	
	@Test
	public void testSelectById() {
		Assert.assertNotNull(goodsDao.selectById(1L).toString());
	}

	@Test
	public void testInsert(){
		Assert.assertEquals(1, goodsDao.insert(new Goods()));
	}

	@Test
	public void testDelete(){
		Assert.assertEquals(1, goodsDao.delete(10L));
	}


}
