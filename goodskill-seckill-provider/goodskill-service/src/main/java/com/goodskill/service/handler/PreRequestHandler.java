package com.goodskill.service.handler;

import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;

/**
 * 请求处理类
 */
public interface PreRequestHandler {
    /**
     * @param request
     */
    void handle(SeckillWebMockRequestDTO request);
}
