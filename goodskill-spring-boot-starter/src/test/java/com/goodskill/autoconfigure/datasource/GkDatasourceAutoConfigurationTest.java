package com.goodskill.autoconfigure.datasource;


import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class GkDatasourceAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GkDatasourceAutoConfiguration.class));

    @Test
    public void ds0() {
        this.contextRunner.withUserConfiguration(GkDatasourceAutoConfiguration.class).run(context -> {
            assertThat(context).doesNotHaveBean("ds0");
        });
    }

    @Test
    public void ds1() {
        this.contextRunner.withUserConfiguration(GkDatasourceAutoConfiguration.class).run(context -> {
            assertThat(context).doesNotHaveBean("ds1");
        });
    }
}