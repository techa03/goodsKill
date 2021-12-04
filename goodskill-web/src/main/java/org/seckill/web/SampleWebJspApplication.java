package org.seckill.web;

import com.alibaba.cloud.seata.feign.SeataFeignClientAutoConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import springfox.documentation.oas.annotations.EnableOpenApi;


/**
 * 启动类
 *
 * @author heng
 */
@SpringBootApplication(exclude = {SeataFeignClientAutoConfiguration.class})
@ImportResource(value = {"classpath*:META-INF/spring/spring-web.xml", "classpath*:META-INF/spring/spring-shiro-web.xml"})
@EnableDiscoveryClient
@EnableFeignClients(value = {"com.goodskill.*.api", "org.seckill.api"})
@EnableRedisHttpSession
@EnableOpenApi
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
