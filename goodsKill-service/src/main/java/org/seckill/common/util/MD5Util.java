package org.seckill.common.util;

import org.springframework.util.DigestUtils;

/**
 * Created by heng on 2017/4/25.
 */
public class MD5Util {
    private static final String SLAT = "gfhfghdfsdgdfgsdgh5rtyhgfhgfhyty@$%@$%^";

    public static String getMD5(long seckillId) {
        String base = seckillId + "/" + SLAT;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    private MD5Util() {
    }
}
