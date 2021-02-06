package com.goodskill.mongo.api;

import com.goodskill.mongo.entity.SuccessKilledDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    void deleteRecord(@RequestParam("seckillId") long seckillId);

    /**
     * @param successKilledDto
     * @return
     */
    @PostMapping("/saveRecord")
    void saveRecord(@RequestBody SuccessKilledDto successKilledDto);

    /**
     *
     * @param seckillId@return
     */
    @PostMapping("/count")
    long count(@RequestParam("seckillId") long seckillId);
}
