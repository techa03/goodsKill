package org.seckill.web.mqlistener;

import lombok.extern.slf4j.Slf4j;
import org.seckill.api.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Date;

/**
 * 秒杀活动结束通知监听器
 * Created by heng on 18/09/02.
 */
@Slf4j
public class SeckillTopicListener implements MessageListener {
    @Autowired
    private SeckillService seckillService;

    @Override
    public void onMessage(Message message) {
        long seckillId = 0;
        boolean status = false;
        String note = null;
        try {
            seckillId = message.getLongProperty("seckillId");
            status = message.getBooleanProperty("status");
            note = message.getStringProperty("note");
        } catch (JMSException e) {
            log.error(e.getMessage(), e);
        }
        if (status) {
            log.info("最终成功交易笔数：{}", seckillService.getSuccessKillCount(seckillId));
            log.info("秒杀活动结束，{}时间：{},秒杀id：{}", new Object[]{note, new Date(), seckillId});
        }
    }
}
