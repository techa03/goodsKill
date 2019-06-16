package org.seckill.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.goodskill.es.api.GoodsEsService;
import com.goodskill.es.dto.GoodsDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.service.GoodsService;
import org.seckill.dao.GoodsMapper;
import org.seckill.entity.Goods;
import org.seckill.entity.GoodsExample;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by heng on 2017/1/7.
 */
@Service(
        version = "${demo.service.version}",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
@Slf4j
public class GoodsServiceImpl extends AbstractServiceImpl<GoodsMapper, GoodsExample, Goods> implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Reference(version = "1.0.0", check = false)
    private GoodsEsService goodsEsService;

    public void setGoodsMapper(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    public void setGoodsEsService(GoodsEsService goodsEsService) {
        this.goodsEsService = goodsEsService;
    }

    @Override
    public void uploadGoodsPhoto(long goodsId, byte[] bytes) {
        Goods goods = new Goods();
        goods.setGoodsId((int) goodsId);
        goods.setPhotoImage(bytes);
        log.info(goods.toString());
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    @Override
    public byte[] getPhotoImage(int goodsId) {
        Goods good = goodsMapper.selectByPrimaryKey(goodsId);
        return good.getPhotoImage();
    }


    @Override
    public void addGoods(Goods goods, byte[] bytes) {
        goods.setPhotoImage(bytes);
        goodsMapper.insert(goods);
        GoodsDto goodsDto = new GoodsDto();
        goodsDto.setId(IdWorker.getId());
        BeanUtils.copyProperties(goods, goodsDto);
        goodsEsService.save(goodsDto);
    }

    @Override
    public List<Goods> queryAll() {
        return goodsMapper.selectByExample(null);
    }

    @Override
    public Goods queryByGoodsId(long goodsId) {
        return goodsMapper.selectByPrimaryKey((int) goodsId);
    }

}
