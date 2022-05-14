package com.goodskill.canal.client.sample;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.goodskill.api.dto.SeckillMockCanalResponseDTO;
import com.goodskill.common.constant.SeckillStatusConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class SimpleCanalClientExample {

    /**
     * 用于存放秒杀结果通知
     */
    public final static List<SeckillMockCanalResponseDTO> CANAL_RESPONSE_LIST = new ArrayList<>();

    private static void printEntry(List<Entry> entrys) {
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            EventType eventType = rowChage.getEventType();
            System.out.printf("================&gt; binlog[%s:%s] , name[%s,%s] , eventType : %s%n",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType);

            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                } else {
                    log.debug("-------&gt; before");
                    printColumn(rowData.getBeforeColumnsList());
                    log.debug("-------&gt; after");
                    List<Column> afterColumnsList = rowData.getAfterColumnsList();
                    printColumn(afterColumnsList);
                    boolean present = afterColumnsList.stream().anyMatch(it -> "status".equals(it.getName())
                            && SeckillStatusConstant.END.equals(it.getValue()));
                    if (present) {
                        Column column = afterColumnsList.stream().filter(it -> "seckill_id".equals(it.getName())).findFirst().get();
                        CANAL_RESPONSE_LIST.add(SeckillMockCanalResponseDTO.builder().seckillId(Long.parseLong(column.getValue())).status(true).build());
                        log.warn("秒杀结束！！！！！！！！");
                    }
                }
            }
        }
    }

    private static void printColumn(List<Column> columns) {
        for (Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    @PostConstruct
    public void run() {
        new Thread(() -> {
            CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(),
                    11111), "example", "canal", "canal");
            int batchSize = 1000;
            int emptyCount = 0;
            try {
                connector.connect();
                connector.subscribe("seckill\\.seckill");
                connector.rollback();
                int totalEmptyCount = 1200;
                while (emptyCount < totalEmptyCount) {
                    Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {
                        emptyCount++;
                        log.debug("empty count : " + emptyCount);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    } else {
                        emptyCount = 0;
                        // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                        printEntry(message.getEntries());
                    }

                    connector.ack(batchId); // 提交确认
                    // connector.rollback(batchId); // 处理失败, 回滚数据
                }
                log.info("empty too many times, exit");
            } finally {
                connector.disconnect();
            }
        }).start();
    }
}