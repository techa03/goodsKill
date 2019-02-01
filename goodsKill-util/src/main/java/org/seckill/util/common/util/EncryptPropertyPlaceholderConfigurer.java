package org.seckill.util.common.util;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 支持加密配置文件插件
 *
 * @author ZhangShuzheng
 * @date 2017/2/4
 */
public class EncryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private String[] propertyNames = {"master.password","slave.password"};

    /**
     * 解密指定propertyName的加密属性值
     * @param propertyName
     * @param propertyValue
     * @return
     */
    @Override
    protected String convertProperty(String propertyName, String propertyValue) {
        for (String p : propertyNames) {
            if (p.equalsIgnoreCase(propertyName)) {
                return AesUtil.aesDecode(propertyValue);
            }
        }
        return super.convertProperty(propertyName, propertyValue);
    }

}
