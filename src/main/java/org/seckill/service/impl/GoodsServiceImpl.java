package org.seckill.service.impl;

import org.seckill.dao.GoodsDao;
import org.seckill.entity.Goods;
import org.seckill.exception.HengException;
import org.seckill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by heng on 2017/1/7.
 */
@Service
public class GoodsServiceImpl implements GoodsService{
    @Autowired
    private GoodsDao goodsDao;

    @Override
    public void uploadGoodsPhoto(CommonsMultipartFile file, long goodsId) {
        String path="I:/java学习/" + file.getOriginalFilename();
        try {
            FileOutputStream fos = new FileOutputStream("I:/java学习/" + file.getOriginalFilename());
            InputStream is = file.getInputStream();
            int b = 0;
            while ((b = is.read()) != -1) {
                fos.write(b);
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (IOException e) {
            throw new HengException("上传文件异常");
        }
        goodsDao.updatePhotoUrl(goodsId, path);
    }

    @Override
    public String getPhotoUrl(int goodsId) {
        Goods good=goodsDao.selectById(goodsId);
        return good.getPhotoUrl();
    }
}
