package com.goodskill.gateway.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 动态路由配置初始化
 *
 * @author heng
 * @since  2022/1/6 1:47 下午
 */
@Slf4j
@Component
public class DynamicRouteConfig implements CommandLineRunner {

    @Autowired
    private DynamicRouteRefresh dynamicRouteService;

    private ConfigService configService;
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String NACOS_SERVER_ADDR;

    @Value("${spring.cloud.nacos.discovery.namespace:}")
    private String NACOS_NAMESPACE;

    @Value("${spring.cloud.nacos.discovery.group:DEFAULT_GROUP}")
    private String NACOS_ROUTE_GROUP;

    @Value("${spring.cloud.nacos.username}")
    private String NACOS_USERNAME;

    @Value("${spring.cloud.nacos.password}")
    private String NACOS_PASSWORD;

    @Value("${nacos.router.data.id}")
    private String NACOS_ROUTE_DATA_ID;

    @PostConstruct
    public void init() {
        log.info("gateway route init...");
        try {
            configService = initConfigService();
            if (configService == null) {
                log.warn("initConfigService fail");
                return;
            }
        } catch (Exception e) {
            log.error("初始化网关路由时发生错误", e);
        }
        dynamicRouteByNacosListener(NACOS_ROUTE_DATA_ID, NACOS_ROUTE_GROUP);
    }

    /**
     * 监听Nacos下发的动态路由配置
     *
     * @param dataId
     * @param group
     */
    public void dynamicRouteByNacosListener(String dataId, String group) {
        try {
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("进行网关更新:\n\r{}", configInfo);
                    List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
                    log.info("update route : {}", definitionList.toString());
                    dynamicRouteService.updateList(definitionList);
                }

                @Override
                public Executor getExecutor() {
                    log.info("getExecutor\n\r");
                    return null;
                }
            });
        } catch (NacosException e) {
            log.error("从nacos接收动态路由配置出错!!!", e);
        }
    }

    /**
     * 初始化网关路由 nacos config
     *
     * @return
     */
    private ConfigService initConfigService() {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", NACOS_SERVER_ADDR);
            properties.setProperty("username", NACOS_USERNAME);
            properties.setProperty("password", NACOS_PASSWORD);
            properties.setProperty("namespace", NACOS_NAMESPACE);
            return configService = NacosFactory.createConfigService(properties);
        } catch (Exception e) {
            log.error("初始化网关路由时发生错误", e);
            return null;
        }
    }

    @Override
    public void run(String... args) throws Exception {
        String configInfo = configService.getConfig(NACOS_ROUTE_DATA_ID, NACOS_ROUTE_GROUP, 3000);
        log.info("获取网关当前配置:\r\n{}", configInfo);
        List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
        if (!CollectionUtils.isEmpty(definitionList)) {
            definitionList.forEach(definition -> {
                log.info("add route : {}", definition.toString());
                dynamicRouteService.save(definition);
            });
        }
    }
}
