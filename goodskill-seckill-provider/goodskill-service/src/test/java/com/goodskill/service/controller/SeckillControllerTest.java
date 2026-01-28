package com.goodskill.service.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.goodskill.api.dto.ExposerDTO;
import com.goodskill.api.dto.SeckillInfoDTO;
import com.goodskill.api.service.GoodsEsService;
import com.goodskill.api.service.GoodsService;
import com.goodskill.api.service.SeckillService;
import com.goodskill.api.vo.SeckillVO;
import com.goodskill.core.info.Result;
import com.goodskill.service.pojo.dto.ResponseDTO;
import com.goodskill.service.util.UploadFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeckillControllerTest {
    @Mock
    private SeckillService seckillService;
    @Mock
    private GoodsService goodsService;
    @Mock
    private GoodsEsService goodsEsService;
    @Mock
    private UploadFileUtil uploadFileUtil;
    @Mock
    private Model model;

    @InjectMocks
    private SeckillController seckillController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testList() {
        PageDTO<SeckillVO> pageInfo = new PageDTO<>();
        pageInfo.setRecords(new ArrayList<>());
        pageInfo.setTotal(0L);
        pageInfo.setPages(0);

        when(seckillService.getSeckillList(0, 4, null)).thenReturn(pageInfo);

        String result = seckillController.list(model, 0, 4, null);

        assertEquals("list", result);
        verify(seckillService, times(1)).getSeckillList(0, 4, null);
    }

    @Test
    void testListWithGoodsName() {
        PageDTO<SeckillVO> pageInfo = new PageDTO<>();
        List<SeckillVO> records = new ArrayList<>();
        SeckillVO seckillVO = new SeckillVO();
        seckillVO.setSeckillId(1000L);
        records.add(seckillVO);
        pageInfo.setRecords(records);
        pageInfo.setTotal(1L);
        pageInfo.setPages(1);

        when(seckillService.getSeckillList(0, 4, "iphone")).thenReturn(pageInfo);

        String result = seckillController.list(model, 0, 4, "iphone");

        assertEquals("list", result);
        verify(seckillService, times(1)).getSeckillList(0, 4, "iphone");
    }

    @Test
    void testDetail() {
        Long seckillId = 1000L;
        SeckillVO seckillVO = new SeckillVO();
        seckillVO.setSeckillId(seckillId);
        seckillVO.setName("test");

        when(seckillService.findById(seckillId)).thenReturn(seckillVO);

        String result = seckillController.detail(seckillId, model);

        assertEquals("detail", result);
        verify(seckillService, times(1)).findById(seckillId);
    }

    @Test
    void testDetailWithNullId() {
        String result = seckillController.detail(null, model);

        assertEquals("redirect:/seckill/list", result);
        verify(seckillService, never()).findById(any());
    }

    @Test
    void testDetailWithNonExistentId() {
        Long seckillId = 9999L;
        when(seckillService.findById(seckillId)).thenReturn(null);

        String result = seckillController.detail(seckillId, model);

        assertEquals("forward:/seckill/list", result);
        verify(seckillService, times(1)).findById(seckillId);
    }

    @Test
    void testExposerSuccess() {
        Long seckillId = 1000L;
        ExposerDTO exposerDTO = new ExposerDTO(true, "md5", seckillId);

        when(seckillService.exportSeckillUrl(seckillId)).thenReturn(exposerDTO);

        Result<ExposerDTO> result = seckillController.exposer(seckillId);

        assertEquals(Result.SUCCESS, result.getCode());
        assertNotNull(result.getData());
        assertEquals(seckillId, result.getData().getSeckillId());
        verify(seckillService, times(1)).exportSeckillUrl(seckillId);
    }

    @Test
    void testExposerFailure() {
        Long seckillId = 1000L;
        when(seckillService.exportSeckillUrl(seckillId)).thenThrow(new RuntimeException("test error"));

        Result<ExposerDTO> result = seckillController.exposer(seckillId);

        assertEquals(Result.FAIL, result.getCode());
        assertEquals("test error", result.getMsg());
        verify(seckillService, times(1)).exportSeckillUrl(seckillId);
    }

    @Test
    void testAddSeckill() {
        SeckillVO seckill = new SeckillVO();
        seckill.setName("test");

        String result = seckillController.addSeckill(seckill);

        assertNull(result);
        verify(seckillService, times(1)).save(seckill);
    }

    @Test
    void testToAddSeckillPage() {
        String result = seckillController.toAddSeckillPage();

        assertEquals("seckill/addSeckill", result);
    }

    @Test
    void testDelete() {
        Long seckillId = 1000L;

        String result = seckillController.delete(seckillId);

        assertNull(result);
        verify(seckillService, times(1)).removeBySeckillId(seckillId);
    }

    @Test
    void testEdit() {
        Long seckillId = 1000L;
        SeckillInfoDTO seckillInfoDTO = new SeckillInfoDTO();
        seckillInfoDTO.setSeckillId(seckillId);

        when(seckillService.getInfoById(seckillId)).thenReturn(seckillInfoDTO);

        String result = seckillController.edit(model, seckillId);

        assertEquals("seckill/edit", result);
        verify(seckillService, times(1)).getInfoById(seckillId);
    }

    @Test
    void testUpdate() {
        SeckillVO seckill = new SeckillVO();
        seckill.setSeckillId(1000L);

        String result = seckillController.update(seckill);

        assertNull(result);
        verify(seckillService, times(1)).saveOrUpdateSeckill(seckill);
    }

    @Test
    void testPayTransaction() throws IOException {
        String QRfilePath = "test";

        String result = seckillController.payTransaction(QRfilePath, model);

        assertEquals("seckill/payByQrcode", result);
        verify(model, times(1)).addAttribute(eq("QRfilePath"), eq(QRfilePath));
    }

    @Test
    void testUploadPhoto() throws IOException {
        Long seckillId = 1000L;
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());
        String fileUrl = "http://test.com/image.jpg";

        SeckillVO seckill = new SeckillVO();
        seckill.setSeckillId(seckillId);
        seckill.setGoodsId(1);

        when(seckillService.findById(seckillId)).thenReturn(seckill);
        when(uploadFileUtil.uploadFile(file)).thenReturn(fileUrl);

        String result = seckillController.uploadPhoto(file, seckillId);

        assertEquals(fileUrl, result);
        verify(seckillService, times(1)).findById(seckillId);
        verify(uploadFileUtil, times(1)).uploadFile(file);
        verify(goodsService, times(1)).uploadGoodsPhoto(1L, fileUrl);
    }

    @Test
    void testSearchGoods() {
        String goodsName = "iphone";
        List goodsList = new ArrayList();
        goodsList.add("item1");

        when(goodsEsService.searchWithNameByPage(goodsName)).thenReturn(goodsList);

        ResponseDTO result = seckillController.searchGoods(goodsName);

        assertNotNull(result);
        assertEquals("0000", result.getCode());
        assertEquals(goodsList.size(), ((Object[]) result.getData()).length);
        verify(goodsEsService, times(1)).searchWithNameByPage(goodsName);
    }
}
