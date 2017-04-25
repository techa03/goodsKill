package org.seckill.common.util;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by heng on 2017/3/11.
 */
public class String2DateUtil implements Converter<String,Date> {
    private final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final int leng_yyyyMMdd = 10;
    private final int leng_yyyyMMddHHmm = 16;
    private final int leng_yyyyMMddHHmmss = 19;

    @Override
    public Date convert(String source) {
        if(source.length() == leng_yyyyMMdd) {
            try {
                return yyyyMMdd.parse(source);
            } catch (ParseException e) {
                e.printStackTrace();

            }
        } else if(source.length() == leng_yyyyMMddHHmm) {
            try {
                return yyyyMMddHHmm.parse(source);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if(source.length() == leng_yyyyMMddHHmmss) {
            try {
                return yyyyMMddHHmmss.parse(source);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        ApplicationContext context=new ClassPathXmlApplicationContext("spring/spring-dao.xml");
//        SeckillServiceImpl seckillServiceImpl=(SeckillServiceImpl) context.getBean("seckillServiceImpl");
//        System.out.println(seckillServiceImpl.toString());
    }
}
