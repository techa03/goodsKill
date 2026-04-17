package com.goodskill.service.impl;

import com.goodskill.core.pojo.dto.GoodsDTO;
import com.goodskill.service.es.model.Goods;
import com.goodskill.service.es.repository.GoodsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.data.elasticsearch.core.query.Query;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SeckillGoodsServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SeckillGoodsServiceImplTest {

    @InjectMocks
    private SeckillGoodsServiceImpl seckillGoodsService;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private GoodsRepository goodsRepository;

    @Test
    void shouldSaveGoodsToElasticsearch() {
        // Given
        GoodsDTO goodsDto = new GoodsDTO();
        goodsDto.setGoodsId(1);
        goodsDto.setName("Test Product");

        // When
        seckillGoodsService.save(goodsDto);

        // Then
        verify(goodsRepository, times(1)).save(any(Goods.class));
    }

    @Test
    void shouldSaveBatchGoodsToElasticsearch() {
        // Given
        List<GoodsDTO> dtoList = new ArrayList<>();
        GoodsDTO dto1 = new GoodsDTO();
        dto1.setGoodsId(1);
        dto1.setName("Product 1");
        GoodsDTO dto2 = new GoodsDTO();
        dto2.setGoodsId(2);
        dto2.setName("Product 2");
        dtoList.add(dto1);
        dtoList.add(dto2);

        // When
        seckillGoodsService.saveBatch(dtoList);

        // Then
        verify(goodsRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldSaveBatchEmptyList() {
        // Given
        List<GoodsDTO> emptyList = new ArrayList<>();

        // When
        seckillGoodsService.saveBatch(emptyList);

        // Then
        verify(goodsRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldDeleteGoodsByGoodsId() {
        // Given
        GoodsDTO goodsDto = new GoodsDTO();
        goodsDto.setGoodsId(1);

        // When
        seckillGoodsService.delete(goodsDto);

        // Then
        verify(goodsRepository, times(1)).deleteByGoodsId(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSearchWithNameByPage() {
        // Given
        String input = "iPhone";

        Goods goods = new Goods();
        goods.setName("iPhone 15 Pro");

        SearchHit<Goods> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(goods);
        when(searchHit.getHighlightField("name")).thenReturn(List.of("<font color='red'>iPhone</font> 15 Pro"));

        SearchHits<Goods> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of(searchHit));

        when(elasticsearchOperations.search(any(Query.class), eq(Goods.class))).thenReturn(searchHits);

        // When
        List<GoodsDTO> result = seckillGoodsService.searchWithNameByPage(input);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("<font color='red'>iPhone</font> 15 Pro", result.get(0).getName());
        assertEquals("iPhone 15 Pro", result.get(0).getRawName());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnEmptyListWhenNoSearchResults() {
        // Given
        String input = "NonExistent";

        SearchHits<Goods> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of());

        when(elasticsearchOperations.search(any(Query.class), eq(Goods.class))).thenReturn(searchHits);

        // When
        List<GoodsDTO> result = seckillGoodsService.searchWithNameByPage(input);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
