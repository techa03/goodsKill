package com.goodskill.autoconfigure.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.shardingsphere.datasource")
public class GkDatasourceProperties {

    private Ds0 ds0 = new Ds0();

    private Ds1 ds1 = new Ds1();

    public Ds0 getDs0() {
        return ds0;
    }

    public void setDs0(Ds0 ds0) {
        this.ds0 = ds0;
    }

    public Ds1 getDs1() {
        return ds1;
    }

    public void setDs1(Ds1 ds1) {
        this.ds1 = ds1;
    }

    public static class Ds0 {
        private String driverClassName;

        private String jdbcUrl;

        private String username;

        private String password;

        private String type;

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Ds1 {
        private String driverClassName;

        private String jdbcUrl;

        private String username;

        private String password;

        private String type;

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
