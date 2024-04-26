package com.goodskill.service.util;

import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ZookeeperLockUtilMockTest {
    @InjectMocks
    private ZookeeperLockUtil zookeeperLockUtil;
    @Mock
    private CuratorFramework client;


    @Test
    public void lock() {
        assertNull(zookeeperLockUtil.lock(1L));
    }

}
