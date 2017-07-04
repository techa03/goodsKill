package org.seckill.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by heng on 2017/7/4.
 */
public class PropertiesUtil {
    private final static String PROPERTY_FILE_LOCATION = "seckill.properties";

    public static String getProperties(String key) {
        InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_LOCATION);
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(key);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getProperties("GOODS_PHOTO_DIR"));
        File file = new File("F:\\Users\\goodsKill\\Pictures");
        file.mkdirs();
    }
}
