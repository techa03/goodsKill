package org.seckill.service.impl;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import org.seckill.dao.GoodsDao;
import org.seckill.entity.Goods;
import org.seckill.exception.HengException;
import org.seckill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by heng on 2017/1/7.
 */
@SuppressWarnings("ALL")
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsDao goodsDao;
    private Logger logger = LoggerFactory.logger(this.getClass());

    @Override
    public void uploadGoodsPhoto(CommonsMultipartFile file, long goodsId) throws IOException {
        String path = uploadGoodsPhoto(file);
        goodsDao.updatePhotoUrl(goodsId, path);
    }

    @Override
    public String getPhotoUrl(int goodsId) {
        Goods good = goodsDao.selectById(goodsId);
        return good.getPhotoUrl();
    }


    private String uploadGoodsPhoto(CommonsMultipartFile file) throws IOException {
        final String s = "/Users/heng/java学习/";
        String path = s + file.getOriginalFilename();
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            String filePath = s;
            File file_tmp = new File(filePath);
            if (!file_tmp.exists() && !file_tmp.mkdirs()) {
                throw new HengException("dir create error!");
            }
            fos = new FileOutputStream(path);
            is = file.getInputStream();
            int b;
            while ((b = is.read()) != -1) {
                fos.write(b);
            }
            fos.flush();
        } catch (IOException e) {
            logger.error("error message is:", e);
            throw new HengException("上传文件异常");
        } finally {
            if (fos!=null){
                fos.close();
            }
            if (is!=null){
                is.close();
            }

        }
        return path;
    }

    @Override
    public void addGoods(Goods goods, CommonsMultipartFile file) {
        String path = null;
        try {
            path = uploadGoodsPhoto(file);
        } catch (IOException e) {
            logger.info(e.getMessage(),e);
            throw new HengException("上传文件方法出现错误");
        }
        goods.setPhotoUrl(path);
        goodsDao.insert(goods);
    }
}
