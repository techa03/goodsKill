package org.seckill.service.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.seckill.dao.common.DataSourceEnum;
import org.seckill.dao.common.DynamicDataSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 动态数据源切面
 *
 * @author heng
 * @note 需要注意下该切面必须要在事务注解@Transactional之前，由于在开始事务之前就需要确定数据源，所以设置@Order(Ordered.LOWEST_PRECEDENCE-1)，@Transactional的order是最小值
 * @date 2018/09/28
 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Slf4j
public class DataSourceEntryAspect {
    @Pointcut("execution(* org.seckill.service.impl..*(..))")
    public void servicePointcut() {
    }

    @Pointcut("execution(* insert*(..))")
    public void serviceInsertPointcut() {
    }

    @Pointcut("execution(* update*(..))")
    public void serviceUpdatePointcut() {
    }

    @Pointcut("execution(* delete*(..))")
    public void serviceDeletePointcut() {
    }

    @Pointcut("execution(* select*(..))")
    public void serviceSelectPointcut() {
    }

    @Pointcut("execution(* query*(..))")
    public void serviceQueryPointcut() {
    }

    @Pointcut("execution(* count*(..))")
    public void serviceCountPointcut() {
    }

    @Pointcut("execution(* get*(..))")
    public void serviceGetPointcut() {
    }

    @Before("servicePointcut() && (serviceDeletePointcut() || serviceInsertPointcut() || serviceUpdatePointcut())")
    public void setWriteDataSource() {
        DynamicDataSource.setDatasourceContext(DataSourceEnum.MASTER.getName());
        if (log.isDebugEnabled()) {
            log.debug("切换数据源成功！当前数据源{}", DynamicDataSource.getDatasourceContext().get());
        }
    }

    @After("servicePointcut()")
    public void removeDataSource() {
        DynamicDataSource.remove();
        if (log.isDebugEnabled()) {
            log.debug("数据源还原成功！当前数据源{}", DynamicDataSource.getDatasourceContext().get());
        }
    }

    @Before("servicePointcut() && (serviceSelectPointcut() || serviceCountPointcut() || serviceGetPointcut() || serviceQueryPointcut())")
    public void setReadDataSource() {
        DynamicDataSource.setDatasourceContext(DataSourceEnum.SLAVE.getName());
        if (log.isDebugEnabled()) {
            log.debug("切换数据源成功！当前数据源{}", DynamicDataSource.getDatasourceContext().get());
        }
    }
}
