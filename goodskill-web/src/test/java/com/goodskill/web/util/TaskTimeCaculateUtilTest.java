package com.goodskill.web.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTimeCaculateUtilTest {

    @Test
    void testPrettyPrint() {
        TaskTimeCaculateUtil.startTask("Task 1", "taskId1");
        // Perform some task-related operations
        // ...
        TaskTimeCaculateUtil.stop("taskId1");
        String output = TaskTimeCaculateUtil.prettyPrint("taskId1");
        Assertions.assertNotNull(output);
        System.out.println(output);
    }
}
