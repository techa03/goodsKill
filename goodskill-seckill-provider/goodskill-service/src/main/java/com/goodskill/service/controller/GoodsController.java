package com.goodskill.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.core.info.Result;
import com.goodskill.order.api.OrderService;
import com.goodskill.service.common.GoodsService;
import com.goodskill.service.entity.Goods;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by heng on 17/1/18.
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private OrderService orderService;
    @Resource
    private GoodsService goodsService;

    @GetMapping("/orders/count")
    public Long countOrders(@RequestParam("seckillId") long seckillId) {
        return orderService.count(seckillId);
    }

    @GetMapping("/list")
    @Operation(summary = "分页获取商品列表")
    public Result<IPage<Goods>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Goods> pageParam = new Page<>(page, size);
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword);
        }
        queryWrapper.orderByDesc("create_time");
        return Result.ok(goodsService.page(pageParam, queryWrapper));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情")
    public Result<Goods> getGoodsById(@PathVariable Integer id) {
        Goods goods = goodsService.getById(id);
        return goods != null ? Result.ok(goods) : Result.fail("商品不存在");
    }

    @PostMapping
    @Operation(summary = "创建商品")
    public Result<String> createGoods(@RequestBody Goods goods) {
        boolean success = goodsService.save(goods);
        return success ? Result.ok("创建成功") : Result.fail("创建失败");
    }

    @PutMapping
    @Operation(summary = "更新商品")
    public Result<String> updateGoods(@RequestBody Goods goods) {
        boolean success = goodsService.updateById(goods);
        return success ? Result.ok("更新成功") : Result.fail("更新失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    public Result<String> deleteGoods(@PathVariable Integer id) {
        boolean success = goodsService.removeById(id);
        return success ? Result.ok("删除成功") : Result.fail("删除失败");
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除商品")
    public Result<String> batchDeleteGoods(@RequestBody List<Integer> ids) {
        boolean success = goodsService.removeByIds(ids);
        return success ? Result.ok("批量删除成功") : Result.fail("批量删除失败");
    }

}
