package com.goodskill.api.feign;

import com.goodskill.api.dto.GoodsDTO;
import com.goodskill.api.service.GoodsEsService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "goodskill-seckill", contextId = "goods")
public interface GoodsEsFeignClient extends GoodsEsService {

    @Override
    @PutMapping("/es/save")
    void save(@RequestBody GoodsDTO goodsDto);

    @Override
    @PutMapping("/es/saveBatch")
    void saveBatch(@RequestBody List<GoodsDTO> list);

    @Override
    @DeleteMapping("/es/delete")
    void delete(@RequestBody GoodsDTO goodsDto);

    @Override
    @GetMapping("/es/searchWithNameByPage")
    List<GoodsDTO> searchWithNameByPage(@RequestParam("input") String input);
}
