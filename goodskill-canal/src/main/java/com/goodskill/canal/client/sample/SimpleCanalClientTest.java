package com.goodskill.canal.client.sample;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.goodskill.api.dto.SeckillMockCanalResponseDTO;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 单机模式的测试例子
 * 
 * @author jianghang 2013-4-15 下午04:19:20
 * @version 1.0.4
 */
public class SimpleCanalClientTest extends AbstractCanalClientTest {
    /**
     * 用于存放秒杀结果通知
     */
    public final static List<SeckillMockCanalResponseDTO> CANAL_RESPONSE_LIST = new ArrayList<>();

    public SimpleCanalClientTest(String destination){
        super(destination);
    }

    public void run() {
        // 根据ip，直接创建链接，无HA的功能
        String destination = "example";
        String ip = AddressUtils.getHostIp();
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(ip, 11111),
            destination,
            "canal",
            "canal");
        this.setConnector(connector);
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("## stop the canal client");
                this.stop();
            } catch (Throwable e) {
                logger.warn("##something goes wrong when stopping canal:", e);
            } finally {
                logger.info("## canal client is down.");
            }
        }));
    }

}
