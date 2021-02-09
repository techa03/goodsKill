package org.seckill.web.controller;

import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.seckill.api.service.GoodsService;
import org.seckill.api.service.SeckillService;
import org.seckill.entity.Goods;
import org.seckill.entity.Seckill;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.List;

/**
 * Created by heng on 17/1/18.
 */
@Api(tags = "商品管理")
@Controller
@RequestMapping("/seckill/goods")
public class GoodsController {
    @DubboReference
    private GoodsService goodsService;
    @DubboReference
    private SeckillService seckillService;

    @GetMapping("/new")
    public String addGoodsPage() {
        return "addGoods";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @GlobalTransactional(rollbackFor = Exception.class)
    public String add(Goods goods, @RequestParam("file") CommonsMultipartFile file) {
        Seckill seckill = new Seckill();
        seckill.setSeckillId(1234L);
        seckill.setName("1");
        seckill.setNumber(1);
        seckillService.save(seckill);
        seckillService.save(seckill);
        if (true) {
            throw new RuntimeException();
        }
        return "redirect:/seckill/list";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public List<Goods> list() {
        return goodsService.list();
    }

    @RequestMapping(value = "/{goodsId}", method = RequestMethod.GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public Goods getGoodsById(@PathVariable(value = "goodsId") long goodsId) {
        return goodsService.getById(goodsId);
    }
}
