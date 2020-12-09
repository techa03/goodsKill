package com.goodskill.chat.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * @author heng
 */
@Data
public class ChatMessageDto implements Serializable {

    /**
     * 待发送的消息
     */
    private String message;

    /**
     * 请求token
     */
    private String token;

}
