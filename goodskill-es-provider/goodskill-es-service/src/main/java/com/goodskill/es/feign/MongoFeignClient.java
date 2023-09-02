package com.goodskill.es.feign;

import com.goodskill.order.api.SuccessKilledMongoService;
import com.goodskill.order.entity.SuccessKilledDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * order feign客户端，支持服务降级
 * @since 2021/1/24
 * @author techa03
 */
@FeignClient(value = "mongo-service-provider", fallbackFactory = MongoFeignClient.MongoFeignClientFallbackFactory.class)
public interface MongoFeignClient extends SuccessKilledMongoService {

    @Slf4j
    @Component
    class MongoFeignClientFallbackFactory implements FallbackFactory<MongoFeignClientFallback> {
        @Override
        public MongoFeignClientFallback create(Throwable cause) {
            log.error(cause.getMessage(), cause);
            return new MongoFeignClientFallback();
        }
    }

    @Slf4j
    class MongoFeignClientFallback implements SuccessKilledMongoService {

        @Override
        public Boolean deleteRecord(long seckillId) {
            log.warn("echo fallback");
            return false;
        }

        @Override
        public Boolean saveRecord(SuccessKilledDto successKilledDto) {
            log.warn("echo fallback");
            return false;
        }

        @Override
        public long count(long seckillId) {
            log.warn("echo fallback");
            return -1;
        }
    }
}
