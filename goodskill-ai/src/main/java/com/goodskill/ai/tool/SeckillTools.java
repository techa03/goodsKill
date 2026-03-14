package com.goodskill.ai.tool;

import com.goodskill.ai.service.feign.SeckillMockFeignClient;
import com.goodskill.core.info.Result;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * 开启秒杀活动函数
 *
 * @author techa03
 *
 */
@Component
public class SeckillTools implements Function<SeckillTools.StartSeckillRequest, Long> {

	private static final Logger logger = LoggerFactory.getLogger(SeckillTools.class);

	@Autowired
	private SeckillMockFeignClient seckillMockFeignClient;

	@Override
	public Long apply(StartSeckillRequest request) {
		SeckillWebMockRequestDTO dto = new SeckillWebMockRequestDTO();
		dto.setSeckillId(request.seckillId);
		dto.setSeckillCount(request.seckillCount);
		dto.setRequestCount(request.requestCount);
		CompletableFuture<Result<Long>> resultCompletableFuture = CompletableFuture.supplyAsync(() -> seckillMockFeignClient.doWithProcedure(dto));
		if (resultCompletableFuture.isCompletedExceptionally()) {
			return -1L;
		} else {
			try {
				return resultCompletableFuture.get().getData();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public record StartSeckillRequest(Long seckillId, Integer seckillCount, Integer requestCount) {
	}



//	@Bean
//	@Description("获取任务耗时统计信息")
//	public Function<StartSeckillRequest, String> getTaskTimeInfo() {
//		return request -> {
//			CompletableFuture<Result<String>> resultCompletableFuture = CompletableFuture.supplyAsync(() -> seckillMockFeignClient.getTaskTimeInfo(request.seckillId));
//			if (resultCompletableFuture.isCompletedExceptionally()) {
//				return "";
//			} else {
//				try {
//					return resultCompletableFuture.get().getData();
//				} catch (InterruptedException | ExecutionException e) {
//					throw new RuntimeException(e);
//				}
//			}
//		};
//	}


}
