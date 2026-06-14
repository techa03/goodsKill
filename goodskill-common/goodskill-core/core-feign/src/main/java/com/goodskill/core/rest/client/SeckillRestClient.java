package com.goodskill.core.rest.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.goodskill.core.pojo.dto.ExposerDTO;
import com.goodskill.core.pojo.dto.SeckillInfoDTO;
import com.goodskill.core.pojo.dto.SeckillMockRequestDTO;
import com.goodskill.core.pojo.dto.SeckillResponseDTO;
import com.goodskill.core.pojo.vo.SeckillVO;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.Serializable;

@Service
public class SeckillRestClient {

    private final RestClient restClient;

    public SeckillRestClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://goodskill-seckill")
                .build();
    }

    public PageDTO<SeckillVO> getSeckillList(int pageNum, int pageSize, String goodsName) {
        return restClient.get()
                .uri("/getSeckillList?pageNum={pageNum}&pageSize={pageSize}&goodsName={goodsName}", pageNum, pageSize, goodsName)
                .retrieve()
                .body(PageDTO.class);
    }

    public ExposerDTO exportSeckillUrl(long seckillId) {
        return restClient.post()
                .uri("/exportSeckillUrl?seckillId={seckillId}", seckillId)
                .retrieve()
                .body(ExposerDTO.class);
    }

    public void deleteSuccessKillRecord(long seckillId) {
        restClient.delete()
                .uri("/deleteSuccessKillRecord?seckillId={seckillId}", seckillId)
                .retrieve()
                .toBodilessEntity();
    }

    public void execute(SeckillMockRequestDTO requestDto, int strategyNumber) {
        restClient.post()
                .uri("/execute?strategyNumber={strategyNumber}", strategyNumber)
                .body(requestDto)
                .retrieve()
                .toBodilessEntity();
    }

    public long getSuccessKillCount(Long seckillId) {
        return restClient.get()
                .uri("/getSuccessKillCount?seckillId={seckillId}", seckillId)
                .retrieve()
                .body(Long.class);
    }

    public void prepareSeckill(Long seckillId, int seckillCount, String taskId) {
        restClient.post()
                .uri("/prepareSeckill?seckillId={seckillId}&seckillCount={seckillCount}&taskId={taskId}", seckillId, seckillCount, taskId)
                .retrieve()
                .toBodilessEntity();
    }

    public SeckillVO findById(Serializable seckillId) {
        return restClient.get()
                .uri("/getById?seckillId={seckillId}", seckillId)
                .retrieve()
                .body(SeckillVO.class);
    }

    public boolean saveOrUpdateSeckill(SeckillVO seckill) {
        return restClient.post()
                .uri("/saveOrUpdate")
                .body(seckill)
                .retrieve()
                .body(Boolean.class);
    }

    public boolean removeBySeckillId(Serializable seckillId) {
        return restClient.delete()
                .uri("/removeById?seckillId={seckillId}", seckillId)
                .retrieve()
                .body(Boolean.class);
    }

    public boolean save(SeckillVO seckill) {
        return restClient.post()
                .uri("/save")
                .body(seckill)
                .retrieve()
                .body(Boolean.class);
    }

    public int reduceNumber(com.goodskill.core.pojo.dto.SuccessKilledDTO successKilled) {
        return restClient.post()
                .uri("/reduceNumber")
                .body(successKilled)
                .retrieve()
                .body(Integer.class);
    }

    public int reduceNumberInner(com.goodskill.core.pojo.dto.SuccessKilledDTO successKilled) {
        return restClient.post()
                .uri("/reduceNumberInner")
                .body(successKilled)
                .retrieve()
                .body(Integer.class);
    }

    public SeckillResponseDTO getQrcode(String fileName) {
        return restClient.get()
                .uri("/getQrcode?fileName={fileName}", fileName)
                .retrieve()
                .body(SeckillResponseDTO.class);
    }

    public SeckillInfoDTO getInfoById(Serializable seckillId) {
        return restClient.get()
                .uri("/getInfoById?seckillId={seckillId}", seckillId)
                .retrieve()
                .body(SeckillInfoDTO.class);
    }

    public boolean endSeckill(Long seckillId) {
        return restClient.post()
                .uri("/endSeckill?seckillId={seckillId}", seckillId)
                .retrieve()
                .body(Boolean.class);
    }
}