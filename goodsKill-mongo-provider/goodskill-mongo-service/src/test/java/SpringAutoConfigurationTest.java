import com.goodskill.mongo.service.UserService;
import com.goodskill.mongo.service.config.UserServiceAutoConfiguration;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author techa03
 * @date 2019/4/7
 */
public class SpringAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(UserServiceAutoConfiguration.class));

    @Test
    public void defaultServiceBacksOff() {
        ConditionEvaluationReportLoggingListener initializer = new ConditionEvaluationReportLoggingListener(
                LogLevel.INFO);
        this.contextRunner.withUserConfiguration(UserConfiguration.class)
                .withInitializer(initializer)
                .run((context) -> {
                    assertThat(context).hasSingleBean(UserService.class);
                    assertThat(context.getBean(UserService.class)).isSameAs(
                            context.getBean(UserConfiguration.class).myUserService());
                });
    }

    @Configuration
    static class UserConfiguration {

        @Bean
        public UserService myUserService() {
            return new UserService("mine");
        }

    }


}
