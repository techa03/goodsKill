package com.goodskill.order.api;

import com.goodskill.order.entity.OrderDTO;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @author techa03
 * @date 2019/4/6
 */
@FeignClient(value = "goodskill-order", fallback = OrderServiceFallback.class)
public interface OrderService {

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
    Boolean saveRecord(@RequestBody OrderDTO orderDTO);

    /**
     *
     * @param seckillId@return
     */
    @GetMapping("/count")
    long count(@RequestParam("seckillId") long seckillId);


}


//@Component
class OrderServiceFallbackFactory implements FallbackFactory<OrderServiceFallback> {

    @Override
    public OrderServiceFallback create(Throwable cause) {
        cause.printStackTrace();
        return new OrderServiceFallback();
    }
}

@Component
class OrderServiceFallback implements OrderService {
    public Boolean deleteRecord(long seckillId) {
        return false;
    }

    public Boolean saveRecord(OrderDTO orderDTO) {
        return false;
    }

    public long count(long seckillId) {
        throw new NoFallbackAvailableException("Boom!", new RuntimeException());
    }

}





