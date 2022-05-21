package com.goodskill.web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务计时器，封装了spring的stopWatch工具类
 *
 * @author techa03
 * @since 2021/4/14
 */
@Slf4j
public class TaskTimeCaculateUtil {

    /**
     * 计时器
     */
    private static StopWatch stopWatch = new StopWatch();

    /**
     * 任务计时器映射, taskId:stopWatch
     */
    private static Map<String, StopWatch> taskWatchMap = new ConcurrentHashMap<>();


    /**
     * 开启一个计时任务，最大允许同时开启十个任务
     *
     * @param taskName 任务名
     * @param taskId   任务id
     */
    public synchronized static void startTask(String taskName, String taskId) {
        if (stopWatch.getTaskCount() > 10 || stopWatch.isRunning()) {
            stopWatch = new StopWatch();
        }
        try {
            stopWatch.start(taskName);
            taskWatchMap.put(taskId, stopWatch);
        } catch (IllegalStateException e) {
            log.warn("Last task have not finish yet!", e);
        }
    }

    /**
     * 停止上一个计时任务
     *
     * @param taskId 任务id
     */
    public synchronized static void stop(String taskId) {
        try {
            taskWatchMap.get(taskId).stop();
        } catch (IllegalStateException e) {
            log.warn("Oops, stop error occurs!", e);
        }
    }

    /**
     * 输出历史任务的耗时情况
     *
     * @param taskId 任务id
     * @return 任务的耗时情况
     */
    public static String prettyPrint(String taskId) {
        return taskWatchMap.get(taskId).prettyPrint();
    }
}
