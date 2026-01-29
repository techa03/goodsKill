package com.goodskill.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MD5UtilTest {

    @Test
    void testGetMD5() {
        long seckillId = 12345;
        String md5 = MD5Util.getMD5(seckillId);
        assertNotNull(md5);
        assertEquals(32, md5.length());
    }

    @Test
    void testGetMD5WithDifferentIds() {
        long seckillId1 = 12345;
        long seckillId2 = 67890;
        String md51 = MD5Util.getMD5(seckillId1);
        String md52 = MD5Util.getMD5(seckillId2);
        assertNotNull(md51);
        assertNotNull(md52);
        assertNotEquals(md51, md52);
    }


}
