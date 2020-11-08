package org.seckill.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.es.api.GoodsEsService;
import com.goodskill.es.dto.GoodsDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.service.GoodsService;
import org.seckill.entity.Goods;
import org.seckill.mp.dao.mapper.GoodsMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Service
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
    @Resource
    private GoodsEsService goodsEsService;

    @Override
    @Scheduled
    public void uploadGoodsPhoto(long goodsId, byte[] bytes) {
        Goods goods = new Goods();
        goods.setGoodsId((int) goodsId);
        goods.setPhotoImage(bytes);
        log.info(goods.toString());
        this.updateById(goods);
    }

    @Override
    public void addGoods(Goods goods, byte[] bytes) {
        goods.setPhotoImage(bytes);
        this.save(goods);
        GoodsDto goodsDto = new GoodsDto();
        goodsDto.setId(IdWorker.getId());
        BeanUtils.copyProperties(goods, goodsDto);
        try {
            goodsEsService.save(goodsDto);
        } catch (Exception e) {
            log.error("es服务不可用，请检查！", e);
        }
    }

    @Override
    public List<Goods> list() {
        return super.list();
    }

}
