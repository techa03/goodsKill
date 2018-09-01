package org.seckill.service.mq;

public class MqTask {
    public static volatile boolean taskCompleteFlag = false;
    public static volatile int count = 0;
}
