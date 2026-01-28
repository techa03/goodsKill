package com.goodskill.service.util;

import com.goodskill.core.enums.Events;
import com.goodskill.core.enums.States;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.state.State;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StateMachineServiceTest {
    @InjectMocks
    private StateMachineService stateMachineService;

    @Mock
    private StateMachineFactory<States, Events> stateMachineFactory;

    @Mock
    private StateMachinePersister<States, Events, String> stateMachinePersister;

    @Mock
    private StateMachine<States, Events> stateMachine;

    @Mock
    private State<States, Events> state;

    @Mock
    private StateMachineEventResult<States, Events> eventResult;

    private static final long SECKILL_ID = 1000L;

    @BeforeEach
    void setUp() throws Exception {
        Field stateMachineMapField = StateMachineService.class.getDeclaredField("stateMachineMap");
        stateMachineMapField.setAccessible(true);
        stateMachineMapField.set(stateMachineService, new java.util.concurrent.ConcurrentHashMap<>());
    }

    @Test
    void testInitStateMachine() throws Exception {
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);

        StateMachine<States, Events> result = stateMachineService.initStateMachine(SECKILL_ID);

        assertNull(result);
        verify(stateMachineFactory, times(1)).getStateMachine();
        verify(stateMachinePersister, times(1)).persist(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
    }

    @Test
    void testInitStateMachineWithExisting() throws Exception {
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);

        stateMachineService.initStateMachine(SECKILL_ID);
        StateMachine<States, Events> result = stateMachineService.initStateMachine(SECKILL_ID);

        assertNotNull(result);
        assertEquals(stateMachine, result);
        verify(stateMachineFactory, times(2)).getStateMachine();
        verify(stateMachinePersister, times(2)).persist(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
    }

    @Test
    void testFeedMachineSuccess() throws Exception {
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.sendEvent(any(Mono.class))).thenReturn(Flux.just(eventResult));
        when(eventResult.getResultType()).thenReturn(StateMachineEventResult.ResultType.ACCEPTED);

        stateMachineService.initStateMachine(SECKILL_ID);

        boolean result = stateMachineService.feedMachine(Events.ACTIVITY_START, SECKILL_ID);

        assertTrue(result);
        verify(stateMachinePersister, times(1)).restore(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
        verify(stateMachine, times(1)).sendEvent(any(Mono.class));
        verify(stateMachinePersister, times(2)).persist(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
    }

    @Test
    void testFeedMachineRejected() throws Exception {
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.sendEvent(any(Mono.class))).thenReturn(Flux.just(eventResult));
        when(eventResult.getResultType()).thenReturn(StateMachineEventResult.ResultType.DENIED);

        stateMachineService.initStateMachine(SECKILL_ID);

        boolean result = stateMachineService.feedMachine(Events.ACTIVITY_START, SECKILL_ID);

        assertFalse(result);
        verify(stateMachinePersister, times(1)).restore(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
        verify(stateMachine, times(1)).sendEvent(any(Mono.class));
        verify(stateMachinePersister, times(2)).persist(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
    }

    @Test
    void testFeedMachineWithNullResult() throws Exception {
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.sendEvent(any(Mono.class))).thenReturn(Flux.empty());

        stateMachineService.initStateMachine(SECKILL_ID);

        boolean result = stateMachineService.feedMachine(Events.ACTIVITY_START, SECKILL_ID);

        assertFalse(result);
        verify(stateMachinePersister, times(1)).restore(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
        verify(stateMachine, times(1)).sendEvent(any(Mono.class));
        verify(stateMachinePersister, times(2)).persist(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
    }

    @Test
    void testFeedMachineWithDifferentEvents() throws Exception {
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.sendEvent(any(Mono.class))).thenReturn(Flux.just(eventResult));
        when(eventResult.getResultType()).thenReturn(StateMachineEventResult.ResultType.ACCEPTED);

        stateMachineService.initStateMachine(SECKILL_ID);

        Events[] events = {Events.ACTIVITY_START, Events.ACTIVITY_END, Events.ACTIVITY_RESET};
        for (Events event : events) {
            boolean result = stateMachineService.feedMachine(event, SECKILL_ID);
            assertTrue(result);
        }

        verify(stateMachinePersister, times(3)).restore(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
        verify(stateMachine, times(3)).sendEvent(any(Mono.class));
        verify(stateMachinePersister, times(4)).persist(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
    }

    @Test
    void testCheckStateTrue() throws Exception {
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.getState()).thenReturn(state);
        when(state.getId()).thenReturn(States.INIT);

        stateMachineService.initStateMachine(SECKILL_ID);

        boolean result = stateMachineService.checkState(SECKILL_ID, States.INIT);

        assertTrue(result);
        verify(stateMachinePersister, times(1)).restore(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
        verify(stateMachine, times(1)).getState();
    }

    @Test
    void testCheckStateFalse() throws Exception {
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.getState()).thenReturn(state);
        when(state.getId()).thenReturn(States.INIT);

        stateMachineService.initStateMachine(SECKILL_ID);

        boolean result = stateMachineService.checkState(SECKILL_ID, States.IN_PROGRESS);

        assertFalse(result);
        verify(stateMachinePersister, times(1)).restore(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
        verify(stateMachine, times(1)).getState();
    }

    @Test
    void testCheckStateWithDifferentStates() throws Exception {
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.getState()).thenReturn(state);

        stateMachineService.initStateMachine(SECKILL_ID);

        States[] states = {States.INIT, States.IN_PROGRESS, States.END, States.INTERRUPTTED};
        for (States testState : states) {
            when(state.getId()).thenReturn(testState);
            boolean result = stateMachineService.checkState(SECKILL_ID, testState);
            assertTrue(result);
        }

        verify(stateMachinePersister, times(4)).restore(eq(stateMachine), eq("seckillId:" + SECKILL_ID));
        verify(stateMachine, times(4)).getState();
    }

    @Test
    void testCheckStateWithNullStateMachine() {
        assertThrows(NullPointerException.class, () -> {
            stateMachineService.checkState(SECKILL_ID, States.INIT);
        });
    }

    @Test
    void testFeedMachineWithNullStateMachine() {
        assertThrows(NullPointerException.class, () -> {
            stateMachineService.feedMachine(Events.ACTIVITY_START, SECKILL_ID);
        });
    }

    @Test
    void testInitStateMachineWithDifferentIds() throws Exception {
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);

        long[] seckillIds = {1000L, 1001L, 1002L};
        for (long seckillId : seckillIds) {
            stateMachineService.initStateMachine(seckillId);
        }

        verify(stateMachineFactory, times(3)).getStateMachine();
        verify(stateMachinePersister, times(3)).persist(eq(stateMachine), anyString());
    }
}
