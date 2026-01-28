package com.goodskill.service.handler;

import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreRequestPipelineTest {
    @InjectMocks
    private PreRequestPipeline preRequestPipeline;

    @Mock
    private AbstractPreRequestHandler handler1;

    @Mock
    private AbstractPreRequestHandler handler2;

    @Mock
    private AbstractPreRequestHandler handler3;

    private List<AbstractPreRequestHandler> handlers;

    @BeforeEach
    void setUp() {
        handlers = new ArrayList<>();
        handlers.add(handler1);
        handlers.add(handler2);
        handlers.add(handler3);

        preRequestPipeline.setPrePreRequestHandlers(handlers);
    }

    @Test
    void testHandleSuccess() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(10);
        request.setTaskId("task1");

        when(handler1.getOrder()).thenReturn(1);
        when(handler2.getOrder()).thenReturn(2);
        when(handler3.getOrder()).thenReturn(3);

        preRequestPipeline.handle(request);

        verify(handler1, times(1)).handle(request);
        verify(handler2, times(1)).handle(request);
        verify(handler3, times(1)).handle(request);
    }

    @Test
    void testHandleWithReverseOrder() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(10);
        request.setTaskId("task1");

        when(handler1.getOrder()).thenReturn(3);
        when(handler2.getOrder()).thenReturn(2);
        when(handler3.getOrder()).thenReturn(1);

        preRequestPipeline.handle(request);

        verify(handler3, times(1)).handle(request);
        verify(handler2, times(1)).handle(request);
        verify(handler1, times(1)).handle(request);
    }

    @Test
    void testHandleWithException() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(10);
        request.setTaskId("task1");

        when(handler1.getOrder()).thenReturn(1);
        when(handler2.getOrder()).thenReturn(2);
        when(handler3.getOrder()).thenReturn(3);

        doThrow(new RuntimeException("Handler error")).when(handler2).handle(any());

        preRequestPipeline.handle(request);

        verify(handler1, times(1)).handle(request);
        verify(handler2, times(1)).handle(request);
        verify(handler3, times(1)).handle(request);
    }

    @Test
    void testHandleWithMultipleExceptions() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(10);
        request.setTaskId("task1");

        when(handler1.getOrder()).thenReturn(1);
        when(handler2.getOrder()).thenReturn(2);
        when(handler3.getOrder()).thenReturn(3);

        doThrow(new RuntimeException("Handler1 error")).when(handler1).handle(any());
        doThrow(new RuntimeException("Handler3 error")).when(handler3).handle(any());

        preRequestPipeline.handle(request);

        verify(handler1, times(1)).handle(request);
        verify(handler2, times(1)).handle(request);
        verify(handler3, times(1)).handle(request);
    }

    @Test
    void testHandleWithEmptyHandlers() {
        preRequestPipeline.setPrePreRequestHandlers(new ArrayList<>());

        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(10);
        request.setTaskId("task1");

        preRequestPipeline.handle(request);

        verify(handler1, never()).handle(any());
        verify(handler2, never()).handle(any());
        verify(handler3, never()).handle(any());
    }

    @Test
    void testHandleWithNullRequest() {
        when(handler1.getOrder()).thenReturn(1);
        when(handler2.getOrder()).thenReturn(2);
        when(handler3.getOrder()).thenReturn(3);

        preRequestPipeline.handle(null);

        verify(handler1, times(1)).handle(null);
        verify(handler2, times(1)).handle(null);
        verify(handler3, times(1)).handle(null);
    }

    @Test
    void testHandleWithSameOrder() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(10);
        request.setTaskId("task1");

        when(handler1.getOrder()).thenReturn(1);
        when(handler2.getOrder()).thenReturn(1);
        when(handler3.getOrder()).thenReturn(1);

        preRequestPipeline.handle(request);

        verify(handler1, times(1)).handle(request);
        verify(handler2, times(1)).handle(request);
        verify(handler3, times(1)).handle(request);
    }
}
