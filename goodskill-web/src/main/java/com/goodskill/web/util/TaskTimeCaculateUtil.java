package com.goodskill.web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

/**
 * 任务计时器，封装了spring的stopWatch工具类
 *
 * @since 2021/4/14
 * @author techa03
 */
@Slf4j
public class TaskTimeCaculateUtil {

    /**
     * 计时器
     */
    private static StopWatch stopWatch = new StopWatch();


    /**
     * 开启一个计时任务，最大允许同时开启十个任务
     *
     * @param taskName 任务名
     */
    public synchronized static void startTask(String taskName) {
        if (stopWatch.getTaskCount() > 10) {
            stopWatch = new StopWatch();
        }
        try {
            stopWatch.start(taskName);
        } catch (IllegalStateException e) {
            log.warn("Last task have not finish yet!", e);
        }
    }

    /**
     * 停止上一个计时任务
     *
     */
    public synchronized static void stop() {
        try {
            stopWatch.stop();
        } catch (IllegalStateException e) {
            log.warn("Oops, stop error occurs!", e);
        }
    }

    /**
     * 输出历史任务的耗时情况
     *
     * @return 任务的耗时情况
     */
    public static String prettyPrint() {
        return stopWatch.prettyPrint();
    }
}
