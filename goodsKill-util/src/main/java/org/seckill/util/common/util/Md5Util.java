package org.seckill.util.common.util;

import org.springframework.util.DigestUtils;

/**
 *
 * @author heng
 * @date 2017/4/25
 */
public class Md5Util {
    private static final String SLAT = "gfhfghdfsdgdfgsdgh5rtyhgfhgfhyty@$%@$%^";

    public static String getMD5(long seckillId) {
        String base = seckillId + "/" + SLAT;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    private Md5Util() {
    }
}
