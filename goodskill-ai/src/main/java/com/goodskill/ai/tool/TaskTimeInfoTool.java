package com.goodskill.ai.tool;

import com.goodskill.ai.service.feign.SeckillMockFeignClient;
import com.goodskill.core.info.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Component
public class TaskTimeInfoTool implements Function<TaskTimeInfoTool.GetTaskTimeInfoRequest, String> {
    @Autowired
    private SeckillMockFeignClient seckillMockFeignClient;
    @Override
    public String apply(TaskTimeInfoTool.GetTaskTimeInfoRequest request) {
        CompletableFuture<Result<Map<String, Object>>> resultCompletableFuture = CompletableFuture.supplyAsync(() -> seckillMockFeignClient.getTaskDetails(request.taskId));
        if (resultCompletableFuture.isCompletedExceptionally()) {
            return "";
        } else {
            try {
                return resultCompletableFuture.get().getData().toString();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public record GetTaskTimeInfoRequest(String taskId, Long seckillId) {
    }
}
