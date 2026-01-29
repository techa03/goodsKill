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
    private static final Map<String, StopWatch> taskWatchMap = new ConcurrentHashMap<>();


    /**
     * 开启一个计时任务，最大允许同时开启5个任务
     *
     * @param taskName 任务名
     * @param taskId   任务id
     */
    public synchronized static void startTask(String taskName, String taskId) {
        if (stopWatch.getTaskCount() > 5 || stopWatch.isRunning()) {
            stopWatch = new StopWatch();
        }
        try {
            stopWatch.start(taskName + "，任务id:" + taskId);
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

    /**
     * 获取任务详细信息
     *
     * @param taskId 任务id
     * @return 任务详细信息，包含任务名称和耗时；如果任务不存在，返回null
     */
    public static Map<String, Object> getTaskDetails(String taskId) {
        StopWatch stopWatch = taskWatchMap.get(taskId);
        if (stopWatch == null) {
            return null;
        }

        // 不等待任务结束，立即返回当前状态
        // 这样可以避免网关超时问题，让前端通过轮询获取最终状态
        boolean isRunning = false;
        try {
            isRunning = stopWatch.isRunning();
        } catch (Exception e) {
            log.warn("检查任务状态时发生错误: {}", e.getMessage());
        }

        Map<String, Object> details = new ConcurrentHashMap<>();
        details.put("totalTimeMillis", stopWatch.getTotalTimeMillis());
        details.put("totalTimeSeconds", stopWatch.getTotalTimeSeconds());
        details.put("taskCount", stopWatch.getTaskCount());
        details.put("isRunning", isRunning); // 添加任务运行状态
        details.put("taskId", taskId); // 添加任务ID

        // 获取每个任务的详细信息
        Map<String, Long> taskTimes = new ConcurrentHashMap<>();
        for (int i = 0; i < stopWatch.getTaskCount(); i++) {
            StopWatch.TaskInfo taskInfo = stopWatch.getTaskInfo()[i];
            String taskName = taskInfo.getTaskName();
            taskTimes.put(taskName, taskInfo.getTimeMillis());
        }
        details.put("taskTimeMap", taskTimes);
        return details;
    }

    /**
     * 获取所有任务的详细信息
     *
     * @return 所有任务的详细信息，键为任务ID，值为任务详细信息
     */
    public static Map<String, Map<String, Object>> getAllTaskDetails() {
        Map<String, Map<String, Object>> allTasksDetails = new ConcurrentHashMap<>();
        for (String taskId : taskWatchMap.keySet()) {
            Map<String, Object> taskDetails = getTaskDetails(taskId);
            if (taskDetails != null) {
                allTasksDetails.put(taskId, taskDetails);
            }
        }
        return allTasksDetails;
    }

}
