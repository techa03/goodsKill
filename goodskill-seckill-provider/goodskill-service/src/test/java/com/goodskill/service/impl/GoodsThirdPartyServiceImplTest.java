package com.goodskill.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.api.dto.GoodsDTO;
import com.goodskill.api.service.GoodsEsService;
import com.goodskill.api.vo.GoodsVO;
import com.goodskill.service.entity.Goods;
import com.goodskill.service.impl.dubbo.GoodsThirdPartyServiceImpl;
import com.goodskill.service.mapper.GoodsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoodsThirdPartyServiceImplTest {
    @Mock
    private GoodsMapper baseMapper;
    @Mock
    private GoodsEsService goodsEsService;

    @InjectMocks
    private GoodsThirdPartyServiceImpl goodsService;

    private GoodsVO goodsVO;
    private Goods goods;

    @BeforeEach
    void setUp() throws Exception {
        goodsVO = new GoodsVO();
        goodsVO.setGoodsId(1);
        goodsVO.setName("test goods");
        goodsVO.setPrice("100");
        goodsVO.setPhotoUrl("http://test.com/image.jpg");

        goods = new Goods();
        goods.setGoodsId(1);
        goods.setName("test goods");
        goods.setPrice("100");
        goods.setPhotoUrl("http://test.com/image.jpg");

        Field baseMapperField = ServiceImpl.class.getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(goodsService, baseMapper);
    }

    @Test
    public void testUploadGoodsPhoto() {
        Long goodsId = 1L;
        String fileUrl = "http://localhost/goodskill/1.png";

        goodsService.uploadGoodsPhoto(goodsId, fileUrl);

        verify(baseMapper, times(1)).updateById(any(Goods.class));
    }

    @Test
    public void testUploadGoodsPhotoWithDifferentUrl() {
        Long goodsId = 2L;
        String fileUrl = "http://localhost/goodskill/2.png";

        goodsService.uploadGoodsPhoto(goodsId, fileUrl);

        verify(baseMapper, times(1)).updateById(any(Goods.class));
    }

    @Test
    public void testAddGoods() {
        GoodsVO goods = new GoodsVO();
        goods.setName("new goods");
        goods.setPrice("200");

        try (MockedStatic<IdWorker> idWorkerMock = mockStatic(IdWorker.class)) {
            idWorkerMock.when(IdWorker::getId).thenReturn(12345L);

            goodsService.addGoods(goods);

            verify(baseMapper, times(1)).insert(any(Goods.class));
            verify(goodsEsService, times(1)).save(any(GoodsDTO.class));
        }
    }

    @Test
    public void testAddGoodsWithEsException() {
        GoodsVO goods = new GoodsVO();
        goods.setName("new goods");
        goods.setPrice("200");

        try (MockedStatic<IdWorker> idWorkerMock = mockStatic(IdWorker.class)) {
            idWorkerMock.when(IdWorker::getId).thenReturn(12345L);

            doThrow(new RuntimeException("ES service unavailable"))
                .when(goodsEsService).save(any(GoodsDTO.class));

            assertDoesNotThrow(() -> goodsService.addGoods(goods));

            verify(baseMapper, times(1)).insert(any(Goods.class));
            verify(goodsEsService, times(1)).save(any(GoodsDTO.class));
        }
    }

    @Test
    public void testFindById() {
        Integer goodsId = 1;
        Goods goods = new Goods();
        goods.setGoodsId(1);
        goods.setName("test goods");
        goods.setPrice("100");

        when(baseMapper.selectById(goodsId)).thenReturn(goods);

        GoodsVO result = goodsService.findById(goodsId);

        assertNotNull(result);
        assertEquals(goodsId, result.getGoodsId());
        assertEquals("test goods", result.getName());
        assertEquals("100", result.getPrice());
        verify(baseMapper, times(1)).selectById(goodsId);
    }

    @Test
    public void testFindByIdWithNullResult() {
        Integer goodsId = 999;

        when(baseMapper.selectById(goodsId)).thenReturn(null);

        GoodsVO result = goodsService.findById(goodsId);

        assertNull(result);
        verify(baseMapper, times(1)).selectById(goodsId);
    }

    @Test
    public void testFindMany() {
        List<Goods> goodsList = new ArrayList<>();
        Goods goods1 = new Goods();
        goods1.setGoodsId(1);
        goods1.setName("goods1");
        goods1.setPrice("100");

        Goods goods2 = new Goods();
        goods2.setGoodsId(2);
        goods2.setName("goods2");
        goods2.setPrice("200");

        goodsList.add(goods1);
        goodsList.add(goods2);

        when(baseMapper.selectList(any())).thenReturn(goodsList);

        List<GoodsVO> result = goodsService.findMany();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getGoodsId());
        assertEquals("goods1", result.get(0).getName());
        assertEquals(2, result.get(1).getGoodsId());
        assertEquals("goods2", result.get(1).getName());
        verify(baseMapper, times(1)).selectList(any());
    }

    @Test
    public void testFindManyEmpty() {
        when(baseMapper.selectList(any())).thenReturn(new ArrayList<>());

        List<GoodsVO> result = goodsService.findMany();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(baseMapper, times(1)).selectList(any());
    }

    @Test
    public void testFindManySingleItem() {
        List<Goods> goodsList = new ArrayList<>();
        Goods goods1 = new Goods();
        goods1.setGoodsId(1);
        goods1.setName("goods1");
        goods1.setPrice("100");

        goodsList.add(goods1);

        when(baseMapper.selectList(any())).thenReturn(goodsList);

        List<GoodsVO> result = goodsService.findMany();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getGoodsId());
        assertEquals("goods1", result.get(0).getName());
        verify(baseMapper, times(1)).selectList(any());
    }
}
