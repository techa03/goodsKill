package com.goodskill.service.controller;

import com.goodskill.api.service.GoodsService;
import com.goodskill.api.vo.GoodsVO;
import com.goodskill.service.util.UploadFileUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 *  Created by heng on 17/1/18.
 */
@Tag(name = "商品管理")
@Controller
@RequestMapping("/seckill/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;
    @Resource
    private UploadFileUtil uploadFileUtil;

    @GetMapping("/new")
    public String addGoodsPage(){
        return "addGoods";
    }

    @Transactional
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public String add(GoodsVO goods, @RequestParam("file") MultipartFile file){
        goods.setCreateTime(new Date());
        String url = uploadFileUtil.uploadFile(file);
        goods.setPhotoUrl(url);
        goodsService.addGoods(goods);
        return url;
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET,produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public List<GoodsVO> list(){
        return goodsService.findMany();
    }

    @RequestMapping(value = "/{goodsId}", method = RequestMethod.GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public GoodsVO getGoodsById(@PathVariable(value = "goodsId") long goodsId) {
        return goodsService.findById(goodsId);
    }
}
