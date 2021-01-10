package org.seckill.web.controller;

import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.Reference;
import org.seckill.api.service.GoodsService;
import org.seckill.entity.Goods;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Date;
import java.util.List;

/**
 *  Created by heng on 17/1/18.
 */
@Api(tags = "商品管理")
@Controller
@RequestMapping("/seckill/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;

    @GetMapping("/new")
    public String addGoodsPage(){
        return "addGoods";
    }

    @Transactional
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public String add(Goods goods, @RequestParam("file") CommonsMultipartFile file){
        goods.setCreateTime(new Date());
        goodsService.addGoods(goods,file.getBytes());
        return "redirect:/seckill/list";
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET,produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public List<Goods> list(){
        return goodsService.list();
    }

    @RequestMapping(value = "/{goodsId}", method = RequestMethod.GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public Goods getGoodsById(@PathVariable(value = "goodsId") long goodsId) {
        return goodsService.getById(goodsId);
    }
}
