package org.seckill.service.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态切换数据源类
 * @author heng
 */
@Slf4j
@Deprecated
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static ThreadLocal<String> DATASOURCE_CONTEXT = new ThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        String key = getDatasourceContext().get();
        if (key == null) {
            setDatasourceContext(DataSourceEnum.MASTER.getName());
        } else {
            log.debug("thread:{},determine,dataSource:{}", Thread.currentThread().getName(), key);
            return key;
        }
        log.debug("thread:{},determine,dataSource:{}", Thread.currentThread().getName(), DataSourceEnum.MASTER.getName());
        return DataSourceEnum.MASTER.getName();
    }

    public static ThreadLocal<String> getDatasourceContext() {
        return DATASOURCE_CONTEXT;
    }

    public static void setDatasourceContext(String dataSourceKey) {
        DATASOURCE_CONTEXT.set(dataSourceKey);
    }

    public static void remove() {
        DATASOURCE_CONTEXT.remove();
    }
}
