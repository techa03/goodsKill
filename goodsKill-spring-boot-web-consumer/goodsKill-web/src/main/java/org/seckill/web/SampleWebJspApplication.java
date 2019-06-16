package org.seckill.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@ImportResource(value = {"classpath*:META-INF/spring/spring-web.xml",
        "classpath*:META-INF/spring/spring-web-mq.xml","classpath*:META-INF/spring/spring-shiro-web.xml"})
public class SampleWebJspApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SampleWebJspApplication.class);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(SampleWebJspApplication.class)
                .registerShutdownHook(true)
                .run(args);
    }

}
