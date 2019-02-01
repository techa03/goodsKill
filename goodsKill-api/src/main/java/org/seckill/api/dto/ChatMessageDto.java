package org.seckill.api.dto;

import lombok.Data;
import org.seckill.entity.User;

import java.io.Serializable;

/**
 * @author heng
 */
@Data
public class ChatMessageDto implements Serializable {
    private User user;
    private String message;

}
