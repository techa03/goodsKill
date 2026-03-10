package com.goodskill.service.controller;

import com.goodskill.api.dto.GoodsDTO;
import com.goodskill.api.service.GoodsEsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/es")
public class GoodsEsFeignController {

    @Resource
    private GoodsEsService goodsEsService;

    @PutMapping("/save")
    public void save(@RequestBody GoodsDTO goodsDto) {
        goodsEsService.save(goodsDto);
    }

    @PutMapping("/saveBatch")
    public void saveBatch(@RequestBody List<GoodsDTO> list) {
        goodsEsService.saveBatch(list);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestBody GoodsDTO goodsDto) {
        goodsEsService.delete(goodsDto);
    }

    @GetMapping("/searchWithNameByPage")
    public List<GoodsDTO> searchWithNameByPage(@RequestParam("input") String input) {
        return goodsEsService.searchWithNameByPage(input);
    }
}
