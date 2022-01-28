package com.goodskill.service.common.trade.alipay;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ZxingUtilsTest {

    @TempDir
    private File file;

    @Test
    void getQRCodeImge() {
        String imgPath = file.getPath() + "/qr-s.png";
        File qrCodeImge = ZxingUtils.getQRCodeImge("http://www.goodskill.com", 100, 100, imgPath);
        FileUtil.del(qrCodeImge);
        assertNotNull(qrCodeImge);
    }
}