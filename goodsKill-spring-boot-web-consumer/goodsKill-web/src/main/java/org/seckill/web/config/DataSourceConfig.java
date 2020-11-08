package org.seckill.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

import javax.validation.ValidatorFactory;

/**
 * @author techa03
 * @date 2019/3/23
 */
@Configuration
@EnableRedisHttpSession
public class DataSourceConfig {

    @Bean
    @Primary
    public ValidatorFactory getValidatorFactory() {
        return new OptionalValidatorFactoryBean();
    }

}
