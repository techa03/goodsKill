package com.goodskill.service.handler;

import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@Setter
public class PreRequestPipeline {
    @Resource
    private List<AbstractPreRequestHandler> prePreRequestHandlers;

    public void handle(SeckillWebMockRequestDTO request) {
        prePreRequestHandlers.stream().sorted(Comparator.comparing(Ordered::getOrder))
                .forEach(it -> {
                    try {
                        it.handle(request);
                    } catch (Exception e) {
                        log.warn("pre request handler error", e);
                    }
                });
    }

}
