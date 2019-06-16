package com.goodskill.es;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * elasticsearch服务启动类
 * @author heng
 * @date 2019/06/15
 */
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.goodskill.es.repository")
public class EsApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(EsApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
