package com.goodskill.es.feign;

import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.entity.SuccessKilledDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;

/**
 * mongo feign客户端，支持服务降级
 * @since 2021/1/24
 * @author techa03
 */
@FeignClient(value = "mongo-service-provider", fallback = MongoFeignClient.MongoFeignClientFallback.class,
        configuration = MongoFeignClient.FeignConfiguration.class)
public interface MongoFeignClient extends SuccessKilledMongoService {

    class FeignConfiguration {
        @Bean
        public MongoFeignClientFallback echoServiceFallback() {
            return new MongoFeignClientFallback();
        }
    }

    @Slf4j
    class MongoFeignClientFallback implements MongoFeignClient {
        @Override
        public long deleteRecord(long sekcillId) {
            log.warn("echo fallback");
            return -1;
        }

        @Override
        public void saveRecord(SuccessKilledDto successKilledDto) {
            log.warn("echo fallback");
        }

        @Override
        public long count(SuccessKilledDto successKilledDto) {
            log.warn("echo fallback");
            return -1;
        }
    }
}
