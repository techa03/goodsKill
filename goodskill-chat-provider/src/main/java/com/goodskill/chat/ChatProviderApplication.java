package com.goodskill.chat;

import com.goodskill.chat.server.ChatServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/**
 * 聊天服务提供者
 *
 * @author techa03
 * @date 2020/12/7
 */
@SpringBootApplication
public class ChatProviderApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ChatProviderApplication.class).web(WebApplicationType.NONE).build().run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> new ChatServer(8080).start();
    }

}
