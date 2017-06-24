package org.seckill.dao;

import com.github.pagehelper.PageHelper;
import org.junit.Assert;
import org.junit.Test;
import org.seckill.base.BaseMapperTestConfig;
import org.seckill.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by heng on 2017/6/24.
 */
public class GoodsMapperTest extends BaseMapperTestConfig {
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
    public void deleteByPrimaryKey() throws Exception {
        Goods goods=new Goods();
        goods.setGoodsId(1833);
        goodsMapper.insert(goods);
        Assert.assertEquals(goodsMapper.deleteByPrimaryKey(1),1);
    }

    @Test
    public void insert() throws Exception {
    }

    @Test
    public void insertSelective() throws Exception {
    }

    @Test
    public void selectByExampleWithBLOBs() throws Exception {
    }

    @Test
    public void selectByExample() throws Exception {
    }

    @Test
    public void selectByPrimaryKey() throws Exception {
    }

    @Test
    public void updateByPrimaryKeySelective() throws Exception {
    }

    @Test
    public void updateByPrimaryKeyWithBLOBs() throws Exception {
    }

    @Test
    public void updateByPrimaryKey() throws Exception {
    }

}