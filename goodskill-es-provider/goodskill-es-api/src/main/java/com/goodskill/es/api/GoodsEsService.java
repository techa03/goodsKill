package com.goodskill.es.api;

import com.goodskill.es.dto.GoodsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author heng
 */
@FeignClient("es-service-provider")
public interface GoodsEsService {

    /**
     * 保存
     * @param goodsDto
     * @return
     */
    @PutMapping("/save")
    void save(@RequestBody GoodsDTO goodsDto);

    /**
     * 保存
     * @param list
     * @return
     */
    @PutMapping("/saveBatch")
    void saveBatch(@RequestBody List<GoodsDTO> list);

    /**
     * 删除商品
     */
    @DeleteMapping("/delete")
    void delete(@RequestBody GoodsDTO goodsDto);

    /**
     * 根据商品名称检索商品
     * @param input 用户输入的商品关键词
     * @return 分页结果
     */
    @GetMapping("/searchWithNameByPage")
    List<GoodsDTO> searchWithNameByPage(@RequestParam("input") String input);
}
