package com.goodskill.service.impl;


import com.goodskill.api.dto.GoodsDTO;
import com.goodskill.api.service.GoodsEsService;
import com.goodskill.service.es.model.Goods;
import com.goodskill.service.es.repository.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品信息es库操作类
 *
 * @author techa03
 * @date 2019/6/15
 */
@RestController
@Service
public class GoodsEsServiceImpl implements GoodsEsService {
    private final BeanCopier beanCopier = BeanCopier.create(GoodsDTO.class, Goods.class, false);
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private GoodsRepository goodsRepository;

    @Override
    public void save(GoodsDTO goodsDto) {
        Goods goods = new Goods();
        beanCopier.copy(goodsDto, goods, null);
        goodsRepository.save(goods);
    }

    @Override
    public void saveBatch(List<GoodsDTO> list) {
        List<Goods> collect = list.stream().map(dto -> {
            Goods goods = new Goods();
            beanCopier.copy(dto, goods, null);
            return goods;
        }).collect(Collectors.toList());
        goodsRepository.saveAll(collect);
    }

    @Override
    public void delete(GoodsDTO goodsDto) {
        goodsRepository.deleteByGoodsId(goodsDto.getGoodsId());
    }

    @Override
    public List<GoodsDTO> searchWithNameByPage(String input) {
        Criteria criteria = new Criteria("name").matches(input);
        Query query = new CriteriaQuery(criteria);

        HighlightFieldParameters parameters = HighlightFieldParameters.builder()
                .withPostTags(new String[]{"</font>"})
                .withPreTags(new String[]{"<font color='red'>"})
                .build();
        HighlightField highlightField = new HighlightField("name", parameters);
        Highlight highlight = new Highlight(HighlightParameters.builder().build(), List.of(highlightField));
        query.setHighlightQuery(new HighlightQuery(highlight, null));
        query.setPageable(PageRequest.of(0, 3));
        return elasticsearchOperations.search(query, Goods.class)
                .getSearchHits().stream().map(s -> {
                    Goods goods = s.getContent();
                    GoodsDTO goodsDto = new GoodsDTO();
                    goodsDto.setName(s.getHighlightField("name").getFirst());
                    goodsDto.setRawName(goods.getName());
                    return goodsDto;
                }).collect(Collectors.toList());
    }
}
