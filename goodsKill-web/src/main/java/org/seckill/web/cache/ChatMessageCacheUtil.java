package org.seckill.web.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.seckill.api.dto.ChatMessageDto;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author heng
 */
@Component
@Data
@Slf4j
public class ChatMessageCacheUtil {
    public static Cache<String, ChatMessageDto> userCache;

    @PostConstruct
    public void init() {
        userCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
        log.info(userCache.toString());
    }

}
