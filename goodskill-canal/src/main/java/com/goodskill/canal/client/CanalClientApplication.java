package com.goodskill.canal.client;

import com.goodskill.api.dto.SeckillMockCanalResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.goodskill.canal.client.sample.SimpleCanalClientExample.CANAL_RESPONSE_LIST;

/**
 * binlog读取服务
 */
@SpringBootApplication
@Slf4j
public class CanalClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(CanalClientApplication.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public Supplier<Flux<SeckillMockCanalResponseDTO>> seckillCanalResult() {
        return () -> Flux.fromStream(Stream.generate(() -> {
            try {
                // 每隔1秒拉取一次
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                log.warn(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
            log.debug("开始拉取秒杀结果通知。。");
            if (!CollectionUtils.isEmpty(CANAL_RESPONSE_LIST)) {
                SeckillMockCanalResponseDTO seckillMockCanalResponseDTO = CANAL_RESPONSE_LIST.get(0);
                if (seckillMockCanalResponseDTO != null) {
                    CANAL_RESPONSE_LIST.remove(seckillMockCanalResponseDTO);
                }
                return seckillMockCanalResponseDTO;
            }
            return null;
        }).filter(Objects::nonNull)).subscribeOn(Schedulers.boundedElastic()).share();
    }
}
