package org.seckill.service.mp.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.es.api.GoodsEsService;
import com.goodskill.es.dto.GoodsDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.service.GoodsService;
import org.seckill.entity.Goods;
import org.seckill.mp.dao.mapper.GoodsMapper;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Service(
        version = "${demo.service.version}",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Reference(version = "1.0.0", check = false)
    private GoodsEsService goodsEsService;

    @Override
    public void uploadGoodsPhoto(long goodsId, byte[] bytes) {
        Goods goods = new Goods();
        goods.setGoodsId((int) goodsId);
        goods.setPhotoImage(bytes);
        log.info(goods.toString());
        baseMapper.updateById(goods);
    }

    @Override
    public byte[] getPhotoImage(int goodsId) {
        Goods good = baseMapper.selectById(goodsId);
        return good.getPhotoImage();
    }


    @Override
    public void addGoods(Goods goods, byte[] bytes) {
        goods.setPhotoImage(bytes);
        baseMapper.insert(goods);
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
    public List<Goods> queryAll() {
        return baseMapper.selectList(null);
    }

    @Override
    public Goods queryByGoodsId(long goodsId) {
        return baseMapper.selectById((int) goodsId);
    }
}
