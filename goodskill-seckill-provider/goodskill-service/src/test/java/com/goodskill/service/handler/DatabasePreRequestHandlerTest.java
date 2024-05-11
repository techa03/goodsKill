package com.goodskill.service.handler;

import com.goodskill.common.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.mapper.SuccessKilledMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class DatabasePreRequestHandlerTest {

    @Mock
    private SeckillMapper seckillMapper;

    @Mock
    private SuccessKilledMapper successKilledMapper;

    @InjectMocks
    private DatabasePreRequestHandler databasePreRequestHandler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldUpdateSeckillAndDeleteSuccessKilled() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(123L);
        request.setSeckillCount(10);

        databasePreRequestHandler.handle(request);

        verify(seckillMapper, times(1)).updateById(any(Seckill.class));
        verify(successKilledMapper, times(1)).delete(any());
    }
}
