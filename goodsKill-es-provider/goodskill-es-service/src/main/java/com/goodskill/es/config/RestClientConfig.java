package com.goodskill.es.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;


/**
 * es RestClient配置，默认端口9200
 * @author heng
 */
@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {
    @Value("${elasticsearch.hostAndPort}")
    private String hostAndPort;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(hostAndPort)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}