package org.seckill.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

import javax.validation.Validator;

/**
 * @author techa03
 * @since 2019/3/23
 */
@Configuration
public class DataSourceConfig {

    /**
     * 可以解决启动报错问题
     * Parameter 1 of method messageHandlerMethodFactory in org.springframework.cloud.stream.config.BinderFactoryAutoConfiguration
     * required a single bean, but 2 were found:
     */
    @Bean
    @Primary
    @DependsOn("mvcValidator")
    public Validator validator() {
        return new OptionalValidatorFactoryBean();
    }

}
