package org.seckill.service.common;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * 启动报错友好提示
 *
 * @author techa03
 * @date 2019/3/24
 */
public class ApplicationStartFailureAnalyzer extends AbstractFailureAnalyzer {
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, Throwable cause) {
        return new FailureAnalysis(cause.getMessage(), "亲，请检查配置！mongo/zookeeper/redis/kafka/zookeeper/mysql8.0/activemq都装好了吗？如有问题请参考README文档！", cause);
    }

}
