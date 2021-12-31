package com.goodskill.es.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * 配置elasticsearch http连接客户端
 * @author heng
 */
@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {
    @Value("${elasticsearch.rest.client.address}")
    private String restClientAddress;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
            .connectedTo(restClientAddress)
            .build();
        return RestClients.create(clientConfiguration).rest();
    }
}