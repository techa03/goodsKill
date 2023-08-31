package com.goodskill.web.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.StopWatch;

import java.util.Map;

import static org.mockito.Mockito.*;

public class TaskTimeCaculateUtilTest {

    @Mock
    private StopWatch stopWatch;

    @Mock
    private Map<String, StopWatch> taskWatchMap;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testStartTask() {
        TaskTimeCaculateUtil.startTask("Task 1", "1");
        verify(stopWatch, times(1)).start("Task 1");
        verify(taskWatchMap, times(1)).put("1", stopWatch);
    }

    @Test
    public void testStop() {
        when(taskWatchMap.get("1")).thenReturn(stopWatch);

        TaskTimeCaculateUtil.stop("1");

        verify(stopWatch, times(1)).stop();
    }

    @Test
    public void testPrettyPrint() {
        when(taskWatchMap.get("1")).thenReturn(stopWatch);
        when(stopWatch.prettyPrint()).thenReturn("Task 1: 500ms");

        String result = TaskTimeCaculateUtil.prettyPrint("1");

        Assertions.assertEquals("Task 1: 500ms", result);
    }
}
