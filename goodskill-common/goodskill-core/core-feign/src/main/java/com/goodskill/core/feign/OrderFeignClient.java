package com.goodskill.core.feign;

import com.goodskill.core.pojo.dto.OrderDTO;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @author techa03
 * @date 2019/4/6
 */
@FeignClient(value = "goodskill-order", fallback = OrderFeignClientFallback.class)
public interface OrderFeignClient {

    /**
     * @param seckillId
     */
    @DeleteMapping("/deleteRecord")
    Boolean deleteRecord(@RequestParam("seckillId") long seckillId);

    /**
     * @param orderDTO
     * @return
     */
    @PostMapping("/saveRecord")
    String saveRecord(@RequestBody OrderDTO orderDTO);

    /**
     *
     * @param seckillId@return
     */
    @GetMapping("/count")
    long count(@RequestParam("seckillId") long seckillId);


}


@Component
class OrderServiceFallbackFactory implements FallbackFactory<OrderFeignClientFallback> {

    @Override
    public OrderFeignClientFallback create(Throwable cause) {
        cause.printStackTrace();
        return new OrderFeignClientFallback();
    }
}

@Component
class OrderFeignClientFallback implements OrderFeignClient {
    public Boolean deleteRecord(long seckillId) {
        return false;
    }

    public String saveRecord(OrderDTO orderDTO) {
        return "";
    }

    public long count(long seckillId) {
        throw new NoFallbackAvailableException("Boom!", new RuntimeException());
    }

}





