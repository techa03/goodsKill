package com.goodskill.service.handler;

import com.goodskill.common.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.order.api.SuccessKilledMongoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MongoPreRequestHandlerTest {

    @Mock
    private SuccessKilledMongoService successKilledMongoService;

    @InjectMocks
    private MongoPreRequestHandler mongoPreRequestHandler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldDeleteSuccessKilledRecord() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(123L);

        mongoPreRequestHandler.handle(request);

        verify(successKilledMongoService, times(1)).deleteRecord(anyLong());
    }
}
