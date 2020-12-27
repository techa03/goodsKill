package org.seckill.web;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.ImportResource;


/**
 * 启动类
 *
 * @author heng
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, ActiveMQAutoConfiguration.class})
@ImportResource(value = {"classpath*:META-INF/spring/spring-web.xml", "classpath*:META-INF/spring/spring-shiro-web.xml"})
@EnableBinding(value = {Source.class})
@EnableDiscoveryClient
@EnableFeignClients(value = {"com.goodskill.*.api", "org.seckill.api"})
public class SampleWebJspApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SampleWebJspApplication.class);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(SampleWebJspApplication.class)
                .web(WebApplicationType.SERVLET)
                .registerShutdownHook(true)
                .run(args);
    }

}
