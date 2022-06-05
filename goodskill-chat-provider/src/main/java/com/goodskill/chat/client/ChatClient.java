package com.goodskill.chat.client;

import com.goodskill.chat.dto.ChatMessageDto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author heng
 */
@Slf4j
@Component
public abstract class ChatClient {

    /**
     * 发送聊天消息
     *
     * @param dto
     * @return
     */
    protected abstract Channel sendMsg(ChatMessageDto dto);

}