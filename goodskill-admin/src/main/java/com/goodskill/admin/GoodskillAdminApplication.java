package com.goodskill.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class GoodskillAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodskillAdminApplication.class, args);
    }

}
