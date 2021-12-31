package com.goodskill.service.util;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
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