package com.goodskill.service.handler;

import com.goodskill.core.enums.Events;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.service.util.StateMachineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StateMachinePreRequestHandlerTest {
    @Mock
    private StateMachineService stateMachineService;

    @InjectMocks
    private StateMachinePreRequestHandler handler;

    private SeckillWebMockRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(100);
        request.setTaskId("task1");
    }

    @Test
    void testHandle() {
        when(stateMachineService.feedMachine(Events.ACTIVITY_RESET, 1000L)).thenReturn(true);
        when(stateMachineService.feedMachine(Events.ACTIVITY_START, 1000L)).thenReturn(true);

        handler.handle(request);

        verify(stateMachineService, times(1)).initStateMachine(1000L);
        verify(stateMachineService, times(1)).feedMachine(Events.ACTIVITY_RESET, 1000L);
        verify(stateMachineService, times(1)).feedMachine(Events.ACTIVITY_START, 1000L);
    }

    @Test
    void testHandleWithDifferentSeckillId() {
        request.setSeckillId(2000L);
        when(stateMachineService.feedMachine(Events.ACTIVITY_RESET, 2000L)).thenReturn(true);
        when(stateMachineService.feedMachine(Events.ACTIVITY_START, 2000L)).thenReturn(true);

        handler.handle(request);

        verify(stateMachineService, times(1)).initStateMachine(2000L);
        verify(stateMachineService, times(1)).feedMachine(Events.ACTIVITY_RESET, 2000L);
        verify(stateMachineService, times(1)).feedMachine(Events.ACTIVITY_START, 2000L);
    }

    @Test
    void testHandleWithResetFailure() {
        when(stateMachineService.feedMachine(Events.ACTIVITY_RESET, 1000L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            handler.handle(request);
        });

        assertEquals("活动尚未结束，请等待活动结束后再次操作", exception.getMessage());
        verify(stateMachineService, times(1)).initStateMachine(1000L);
        verify(stateMachineService, times(1)).feedMachine(Events.ACTIVITY_RESET, 1000L);
        verify(stateMachineService, never()).feedMachine(Events.ACTIVITY_START, 1000L);
    }

    @Test
    void testHandleWithStartFailure() {
        when(stateMachineService.feedMachine(Events.ACTIVITY_RESET, 1000L)).thenReturn(true);
        when(stateMachineService.feedMachine(Events.ACTIVITY_START, 1000L)).thenReturn(false);

        handler.handle(request);

        verify(stateMachineService, times(1)).initStateMachine(1000L);
        verify(stateMachineService, times(1)).feedMachine(Events.ACTIVITY_RESET, 1000L);
        verify(stateMachineService, times(1)).feedMachine(Events.ACTIVITY_START, 1000L);
    }

    @Test
    void testGetOrder() {
        int order = handler.getOrder();

        assertEquals(2, order);
    }
}
