package org.seckill.web.controller;

import org.apache.shiro.SecurityUtils;
import org.seckill.api.dto.ChatMessageDto;
import org.seckill.entity.User;
import org.seckill.web.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author heng
 */
@Controller
@RequestMapping("/chatroom")
public class ChatController {
    @Autowired
    private ChatClient chatClient;

    @PostMapping(value = "/send")
    public String send(ChatMessageDto chatMessageDto) {
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        chatMessageDto.setUser(user);
        chatClient.getInstance(user).sendMessage(chatMessageDto);
        return "seckill/chatroom";
    }
}
