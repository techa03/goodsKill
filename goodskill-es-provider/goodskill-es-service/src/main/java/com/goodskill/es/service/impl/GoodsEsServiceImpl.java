package com.goodskill.es.service.impl;


import com.goodskill.es.api.GoodsEsService;
import com.goodskill.es.dto.GoodsDTO;
import com.goodskill.es.model.Goods;
import com.goodskill.es.repository.GoodsRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private BeanCopier beanCopier = BeanCopier.create(GoodsDTO.class, Goods.class, false);

    private BeanCopier beanCopierReverse = BeanCopier.create(Goods.class, GoodsDTO.class, false);


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
        goodsRepository.deleteById(goodsDto.getGoodsId());
    }

    @Override
    public List<GoodsDTO> searchWithNameByPage(String input) {
        NativeSearchQuery searchQuery;
        if (!StringUtils.hasText(input)) {
            searchQuery = new NativeSearchQueryBuilder()
                    .build();
        } else {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchQuery("name", input))
                    .build();
        }
        Pageable pageble = PageRequest.of(0, 3);
        searchQuery.setPageable(pageble);
        HighlightFieldParameters parameters = HighlightFieldParameters.builder()
                .withPostTags(new String[]{"</font>"})
                .withPreTags(new String[]{"<font color='red'>"})
                .build();
        HighlightField highlightField = new HighlightField("name", parameters);
        Highlight highlight = new Highlight(HighlightParameters.builder().build(), List.of(highlightField));
        HighlightQuery highlightQuery = new HighlightQuery(highlight, null);
        searchQuery.setHighlightQuery(highlightQuery);
        return elasticsearchOperations.search(searchQuery, Goods.class)
                .getSearchHits().stream().map(s -> {
                    Goods goods = s.getContent();
                    GoodsDTO goodsDto = new GoodsDTO();
                    goodsDto.setName(s.getHighlightField("name").get(0));
                    goodsDto.setRawName(goods.getName());
                    return goodsDto;
                }).collect(Collectors.toList());
    }
}
