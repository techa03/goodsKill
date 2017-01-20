package org.seckill.web;

import org.seckill.entity.Goods;
import org.seckill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.transaction.Transactional;
import java.util.Date;

/**
 *  Created by heng on 17/1/18.
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/addGoods")
    public String addGoods(){
        return "addGoods";
    }

    @Transactional
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String add(Goods goods, @RequestParam("file") CommonsMultipartFile file){
        goods.setCreateTime(new Date());
        goodsService.addGoods(goods,file);
        return "redirect:/seckill/list";
    }
}
