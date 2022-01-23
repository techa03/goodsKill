package com.goodskill.service.util;

import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class ZookeeperLockUtilMockTest {
    @InjectMocks
    private ZookeeperLockUtil zookeeperLockUtil;
    @Mock
    private CuratorFramework client;


    @Test
    public void lock() {
        assertFalse(zookeeperLockUtil.lock(1L));
    }

    @Test
    public void releaseLock() {
        assertFalse(zookeeperLockUtil.releaseLock(1L));
    }
}