package com.goodskill.autoconfigure.mysql;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author techa03
 * @date 2019/4/7
 */
@ConfigurationProperties(prefix = "goodskill")
public class MysqlDruidProperties {
    private DruidMaster druidMaster = new DruidMaster();
    private DruidSlave druidSlave = new DruidSlave();

    private class DruidMaster {
        private String driver;
        private String url;
        private String username;
        private String passwordEncrpt;
    }

    private class DruidSlave {
        private String driver;
        private String url;
        private String username;
        private String passwordEncrpt;
    }
}
