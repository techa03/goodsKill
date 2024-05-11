package com.goodskill.service.handler;


import com.goodskill.common.core.pojo.dto.SeckillWebMockRequestDTO;
import org.springframework.core.Ordered;

public abstract class AbstractPreRequestHandler implements PreRequestHandler, Ordered {

    /**
     * @param request
     */
    @Override
    public abstract void handle(SeckillWebMockRequestDTO request);

}
