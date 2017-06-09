package org.seckill.dao;

import com.github.pagehelper.PageHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
@Transactional
public class GoodsMapperTest {
    @Autowired
    private GoodsMapper goodsMapper;
    @Test
    public void testSelectByPrimaryKey() throws Exception {
        PageHelper.startPage(1,2);
        Goods goods=goodsMapper.selectByPrimaryKey(1);
        byte[] b=goods.getPhotoImage();
        FileOutputStream os=new FileOutputStream(new File("F:\\heng\\a.jpg"));
        os.write(b);
    }

    @Test
    public void testInsertSelective(){

    }
}