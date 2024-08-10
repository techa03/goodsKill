package com.goodskill.service.handler;

import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class PreRequestPipelineTest {

    @Mock
    private AbstractPreRequestHandler handler1;

    @Mock
    private AbstractPreRequestHandler handler2;

    @InjectMocks
    private PreRequestPipeline preRequestPipeline;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        preRequestPipeline.setPrePreRequestHandlers(Arrays.asList(handler1, handler2));
    }

    @Test
    public void shouldHandleRequestSuccessfully() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        doNothing().when(handler1).handle(request);
        doNothing().when(handler2).handle(request);

        preRequestPipeline.handle(request);

        verify(handler1, times(1)).handle(request);
        verify(handler2, times(1)).handle(request);
    }

    @Test
    public void shouldHandleRequestEvenWhenOneHandlerFails() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        doThrow(new RuntimeException()).when(handler1).handle(request);
        doNothing().when(handler2).handle(request);

        preRequestPipeline.handle(request);

        verify(handler1, times(1)).handle(request);
        verify(handler2, times(1)).handle(request);
    }

    @Test
    public void shouldNotHandleRequestWhenNoHandlers() {
        PreRequestPipeline pipeline = new PreRequestPipeline();
        pipeline.setPrePreRequestHandlers(Arrays.asList());

        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        pipeline.handle(request);

        verify(handler1, times(0)).handle(any());
        verify(handler2, times(0)).handle(any());
    }
}
