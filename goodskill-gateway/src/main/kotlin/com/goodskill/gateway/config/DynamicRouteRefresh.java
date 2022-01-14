package com.goodskill.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 1）实现一个Spring提供的事件推送接口ApplicationEventPublisherAware
 * 2）提供动态路由的基础方法，可通过获取bean操作该类的方法。该类提供新增路由、更新路由、删除路由，然后实现发布的功能。
 *
 * @author heng
 * @since  2022/1/6 1:47 下午
 */
@Slf4j
@Service
public class DynamicRouteRefresh implements ApplicationEventPublisherAware {
    @Autowired
    private InMemoryRouteDefinitionRepository memoryRouteDefinitionRepository;

    /**
     * 发布事件
     */
    @Autowired
    private ApplicationEventPublisher publisher;


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 删除路由
     *
     * @param id
     * @return
     */
    public String delete(String id) {
        try {
            log.info("gateway delete route id {}", id);
            memoryRouteDefinitionRepository.delete(Mono.just(id)).doOnError(error -> log.error("删除失败,id:{}", id))
                    .doAfterTerminate(() -> this.publisher.publishEvent(new RefreshRoutesEvent(this))).subscribe();
            return "delete success";
        } catch (Exception e) {
            return "delete fail";
        }
    }

    /**
     * 更新路由
     *
     * @param definitions
     * @return
     */
    public String updateList(List<RouteDefinition> definitions) {
        log.info("gateway update route {}", definitions);
        // 删除缓存routerDefinition
        memoryRouteDefinitionRepository.getRouteDefinitions()
                .filter(it -> !it.getId().startsWith("ReactiveCompositeDiscoveryClient_"))
                .doOnNext(routeDefinition -> {
                    log.info("delete routeDefinition:{}", routeDefinition);
                    delete(routeDefinition.getId());
                }).doAfterTerminate(() -> definitions.forEach(this::save)).subscribe();
        return "success";
    }

    /**
     * 更新路由
     *
     * @param definition
     * @return
     */
    public String save(RouteDefinition definition) {
        log.info("gateway add route {}", definition);
        try {
            memoryRouteDefinitionRepository.save(Mono.just(definition))
                    .doAfterTerminate(() -> this.publisher.publishEvent(new RefreshRoutesEvent(this))).subscribe();
            return "success";
        } catch (Exception e) {
            return "update route fail";
        }
    }


}
