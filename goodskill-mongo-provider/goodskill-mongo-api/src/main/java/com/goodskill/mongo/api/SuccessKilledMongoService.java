package com.goodskill.mongo.api;

import com.goodskill.mongo.entity.SuccessKilledDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author techa03
 * @date 2019/4/6
 */
@FeignClient("mongo-service-provider")
public interface SuccessKilledMongoService {

    /**
     * @param seckillId
     */
    @DeleteMapping("/deleteRecord")
    Boolean deleteRecord(@RequestParam("seckillId") long seckillId);

    /**
     * @param successKilledDto
     * @return
     */
    @PostMapping("/saveRecord")
    Boolean saveRecord(@RequestBody SuccessKilledDto successKilledDto);

    /**
     *
     * @param seckillId@return
     */
    @GetMapping("/count")
    long count(@RequestParam("seckillId") long seckillId);
}
