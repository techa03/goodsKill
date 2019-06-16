package com.goodskill.es.service;


import com.goodskill.es.api.GoodsEsService;
import com.goodskill.es.dto.GoodsDto;
import com.goodskill.es.model.Goods;
import com.goodskill.es.repository.GoodsRepository;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;

/**
 * @author techa03
 * @date 2019/6/15
 */
@Service(version = "${es.service.version}")
public class GoodsEsServiceImpl implements GoodsEsService {
    @Autowired
    GoodsRepository goodsRepository;


    @Override
    public Goods save(GoodsDto goodsDto) {
        BeanCopier beanCopier = BeanCopier.create(GoodsDto.class, Goods.class, false);
        Goods goods = new Goods();
        beanCopier.copy(goodsDto, goods, null);
        return goodsRepository.save(goods);
    }
}
